package com.gongpingjia.carplay.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.PhoneVerification;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.service.PhoneService;

@Service
public class PhoneServiceImpl implements PhoneService {

	private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private PhoneVerificationDao phoneDao;

	@Autowired
	private ParameterChecker checker;

	@Override
	public ResponseDo sendVerification(String phone, Integer type) throws ApiException {

		if (!CommonUtil.isPhoneNumber(phone)) {
			LOG.warn("Phone number is not correct format");
			throw new ApiException("不是有效的手机号");
		}

		List<User> users = getUserList(phone);

		if (type == 0) {
			// 注册流程
			if (users.size() > 0) {
				// 用户已经存在，手机号不能重复注册
				LOG.warn("Phone number is already registed");
				throw new ApiException("手机号已被注册");
			}
		} else if (type == 1) {
			// 忘记密码流程
			if (users.size() == 0) {
				LOG.warn("User is not exist");
				throw new ApiException("用户不存在");
			}
		} else {
			LOG.warn("Request parameter type is not 1 or 0");
			throw new ApiException("参数错误");
		}

		String verifyCode = savePhoneVerification(phone);

		return sendPhoneVerifyMessage(phone, verifyCode);
	}

	@Override
	public ResponseDo verify(String phone, String code, Integer type) throws ApiException {
		if (!CommonUtil.isPhoneNumber(phone)) {
			LOG.warn("Phone number is not correct format");
			throw new ApiException("不是有效的手机号");
		}
		if (StringUtils.isEmpty(code)) {
			LOG.warn("Parameter code is empty");
			throw new ApiException("输入参数有误");
		}

		if (type == 0) {
			// 只有当注册的时候去校验手机号是否已经被注册过
			List<User> userList = getUserList(phone);
			if (userList.size() > 0) {
				LOG.warn("User with phone number is already registed, phone: {}", phone);
				throw new ApiException("该用户已注册");
			}
		}

		checker.checkPhoneVerifyCode(phone, code);

		return ResponseDo.buildSuccessResponse();
	}

	/**
	 * 通过手机号获取用户信息
	 * 
	 * @param phone
	 *            手机号
	 * @return 用户列表信息
	 */
	private List<User> getUserList(String phone) {
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("phone", phone);
		return userDao.selectByParam(param);
	}

	/**
	 * 往指定的手机上发送验证码消息
	 * 
	 * @param phone
	 * @param verifyCode
	 * @return
	 * @throws ApiException
	 */
	private ResponseDo sendPhoneVerifyMessage(String phone, String verifyCode) throws ApiException {
		LOG.debug("Begin send phone:{} verifyCode:{}", phone, verifyCode);
		// 调用运营商接口发送验证码短信
		JSONObject content = new JSONObject();
		content.put("param1", verifyCode);

		StringBuilder url = new StringBuilder();
		url.append(PropertiesUtil.getProperty("message.send.url", ""));

		Object[] params = new Object[6];
		try {
			params[0] = phone;
			params[1] = PropertiesUtil.getProperty("message.send.template.id", "");
			params[2] = PropertiesUtil.getProperty("message.send.app.id", "");
			params[3] = getAccessToken();
			params[4] = URLEncoder.encode(
					new SimpleDateFormat(Constants.DateFormat.PHONE_VERIFY_TIMESTAMP).format(DateUtil.getDate()),
					Constants.Charset.UTF8);
			params[5] = URLEncoder.encode(content.toString(), Constants.Charset.UTF8);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("Pharse url parameters by encoder failure");
			throw new ApiException("验证码发送失败");
		}

		String paramStr = MessageFormat.format(PropertiesUtil.getProperty("message.send.param", ""), params);

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
		return ResponseDo.buildSuccessResponse();
	}

	/**
	 * 保存手机验证码到数据库中
	 * 
	 * @param phone
	 */
	private String savePhoneVerification(String phone) {
		// 更新数据库记录
		PhoneVerification phoneVerify = phoneDao.selectByPrimaryKey(phone);
		if (phoneVerify == null) {
			// 不存在验证码
			phoneVerify = new PhoneVerification();
			phoneVerify.setPhone(phone);
			phoneVerify.setCode(CodeGenerator.generatorVerifyCode());
			phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
					PropertiesUtil.getProperty("message.effective.seconds", 7200)));

			phoneDao.insert(phoneVerify);
		} else {
			// 已经存在验证码
			if (phoneVerify.getExpire() < DateUtil.getTime()) {
				LOG.debug("Exist phone verifyCode is out of date");
				phoneVerify.setCode(CodeGenerator.generatorVerifyCode());
				phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
						PropertiesUtil.getProperty("message.effective.seconds", 7200)));
				phoneDao.updateByPrimaryKey(phoneVerify);
			}
		}

		return phoneVerify.getCode();
	}

	private String getAccessToken() throws ApiException {
		LOG.debug("get phone verification access token");
		Object[] params = new Object[] { PropertiesUtil.getProperty("message.send.app.id", ""),
				PropertiesUtil.getProperty("message.send.app.secret", "") };

		String url = PropertiesUtil.getProperty("message.send.token.url", "");

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
