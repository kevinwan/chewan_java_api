package com.gongpingjia.carplay.common.phone;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

/**
 * 通过天翼发送短信验证码消息
 * 
 * @author licheng
 *
 */
public class SendMessageByTianyi {

	private static final Logger LOG = LoggerFactory.getLogger(SendMessageByTianyi.class);

	/**
	 * 往指定的手机上发送验证码消息
	 * 
	 * @param phone
	 *            手机号
	 * @param verifyCode
	 *            验证码
	 * @throws ApiException
	 *             发送过程如果出现异常，转换成该异常抛出
	 */
	public static void sendMessage(String phone, String verifyCode) throws ApiException {
		LOG.debug("Send message by Tianyi begin, send phone:{} verifyCode:{}", phone, verifyCode);
		// 调用运营商接口发送验证码短信
		JSONObject content = new JSONObject();
		content.put("param1", verifyCode);

		StringBuilder url = new StringBuilder();
		url.append(PropertiesUtil.getProperty("message.send.tianyi.url", ""));

		Object[] params = new Object[6];
		try {
			params[0] = phone;
			params[1] = PropertiesUtil.getProperty("message.send.tianyi.template.id", "");
			params[2] = PropertiesUtil.getProperty("message.send.tianyi.app.id", "");
			params[3] = getAccessToken();
			params[4] = URLEncoder.encode(
					new SimpleDateFormat(Constants.DateFormat.PHONE_VERIFY_TIMESTAMP).format(DateUtil.getDate()),
					Constants.Charset.UTF8);
			params[5] = URLEncoder.encode(content.toString(), Constants.Charset.UTF8);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Pharse url parameters by encoder failure");
			throw new ApiException("验证码发送失败");
		}

		String paramStr = MessageFormat.format(PropertiesUtil.getProperty("message.send.tianyi.param", ""), params);

		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(url.toString(), paramStr, Arrays.asList(header), Constants.Charset.UTF8);
			if (response.getStatusLine().getStatusCode() != Constants.HTTP_STATUS_OK) {
				LOG.debug("Send verify code failure, send result status:{}", response.getStatusLine());
				throw new ApiException("验证码发送失败");
			}

			JSONObject json = HttpClientUtil.parseResponseGetJson(response);
			if (!"0".equals(json.getString("res_code"))) {
				LOG.debug("Send verify code failure, send result:{}", json.toString());
				throw new ApiException("验证码发送失败");
			}

		} finally {
			// 用完response需要释放资源
			HttpClientUtil.close(response);
		}
	}

	/**
	 * 获取会话Token
	 * 
	 * @return 授权会话Token
	 * @throws ApiException
	 *             业务异常
	 */
	private static String getAccessToken() throws ApiException {
		LOG.debug("get phone verification access token");
		Object[] params = new Object[] { PropertiesUtil.getProperty("message.send.tianyi.app.id", ""),
				PropertiesUtil.getProperty("message.send.tianyi.app.secret", "") };

		String url = PropertiesUtil.getProperty("message.send.tianyi.token.url", "");

		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");

		CloseableHttpResponse response = null;

		try {
			response = HttpClientUtil.post(MessageFormat.format(url, params), "", Arrays.asList(header),
					Constants.Charset.UTF8);

			if (!HttpClientUtil.isStatusOK(response)) {
				LOG.warn("Get access token failure");
				throw new ApiException("获取验证码失败");
			}

			JSONObject json = HttpClientUtil.parseResponseGetJson(response);
			if (!"0".equals(json.getString("res_code"))) {
				LOG.warn("Get access token failure, result info:{}", json.toString());
				throw new ApiException("获取验证码失败");
			}

			return json.getString("access_token");
		} finally {
			HttpClientUtil.close(response);
		}
	}
}
