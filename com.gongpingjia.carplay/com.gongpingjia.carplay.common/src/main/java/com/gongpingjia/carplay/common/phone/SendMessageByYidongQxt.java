package com.gongpingjia.carplay.common.phone;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

/**
 * 通过移动QXT发送验证消息
 * 
 * @author licheng
 *
 */
public class SendMessageByYidongQxt {

	private static final Logger LOG = LoggerFactory.getLogger(SendMessageByYidongQxt.class);

	/**
	 * 往指定的手机上发送验证码消息
	 * 
	 * @param phone
	 * @param verifyCode
	 * @return
	 * @throws ApiException
	 */
	public static void sendMessage(String phone, String verifyCode) throws ApiException {
		LOG.debug("Send message by QXT beging, send phone:{} and verifyCode:{}", phone, verifyCode);
		// 调用运营商接口发送验证码短信
		String prop = PropertiesUtil.getProperty("message.send.yidong.format", "【车玩】 您的短信验证码为（{0}）");
		String message = MessageFormat.format(prop, verifyCode);

		String url = PropertiesUtil.getProperty("message.send.yidong.url", "");
		Map<String, String> queryParams = new HashMap<String, String>(4, 1);
		queryParams.put("user", PropertiesUtil.getProperty("message.send.yidong.username", ""));
		queryParams.put("pwd", PropertiesUtil.getProperty("message.send.yidong.password", ""));
		queryParams.put("phone", phone);
		queryParams.put("msgcont", message);

		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.get(url, queryParams, Arrays.asList(header), Constants.Charset.GBK);
			if (response.getStatusLine().getStatusCode() == Constants.HTTP_STATUS_OK) {
				LOG.debug("Send message by Yidong QXT successfully");
				LOG.debug(HttpClientUtil.parseResponse(response));
			}
		} finally {
			// 用完response需要释放资源
			HttpClientUtil.close(response);
		}
	}

	// public static void main(String[] args) throws ApiException {
	// Map<String, String> queryParams = new HashMap<String, String>(4, 1);
	// queryParams.put("user", "gpj_dev");
	// queryParams.put("pwd", "gpj_dev");
	// queryParams.put("phone", "18994082919");
	// queryParams.put("msgcont", "【车玩】【车玩】 您的短信验证码为（1234）");
	//
	// Header header = new BasicHeader("Accept",
	// "application/json; charset=UTF-8");
	//
	// CloseableHttpResponse response =
	// HttpClientUtil.get("http://qd.qxt666.cn:80/interface/tomsg.jsp",
	// queryParams,
	// Arrays.asList(header), Constants.Charset.GBK);
	//
	// System.out.println(response.getStatusLine());
	// System.out.println(HttpClientUtil.parseResponse(response));
	//
	// HttpClientUtil.close(response);
	// }

}
