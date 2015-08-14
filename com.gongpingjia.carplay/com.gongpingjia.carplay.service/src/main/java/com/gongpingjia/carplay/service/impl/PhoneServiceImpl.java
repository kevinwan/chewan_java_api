package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
	public ResponseDo sendVerification(String phone, int type) throws ApiException {

		if (!CommonUtil.isPhoneNumber(phone)) {
			LOG.error("Phone number is not correct format");
			throw new ApiException("不是有效的手机号");
		}

		List<User> users = getUserList(phone);

		if (type == 0) {
			// 注册流程
			if (users.size() > 0) {
				// 用户已经存在，手机号不能重复注册
				LOG.error("Phone number is already registed");
				throw new ApiException("手机号已被注册");
			}
		} else if (type == 1) {
			// 忘记密码流程
			if (users.size() == 0) {
				LOG.error("User is not exist");
				throw new ApiException("用户不存在");
			}
		} else {
			LOG.error("Request parameter type is not 1 or 0");
			throw new ApiException("参数错误");
		}

		String verifyCode = CodeGenerator.generatorVerifyCode();

		savePhoneVerification(phone, verifyCode);

		ResponseDo response = sendPhoneVerifyMessage(phone, verifyCode);

		return response;
	}

	@Override
	public ResponseDo verify(String phone, String code) throws ApiException {
		if (!CommonUtil.isPhoneNumber(phone)) {
			LOG.error("Phone number is not correct format");
			throw new ApiException("不是有效的手机号");
		}
		if (StringUtils.isEmpty(code)) {
			LOG.error("Parameter code is empty");
			throw new ApiException("输入参数有误");
		}

		List<User> userList = getUserList(phone);
		if (userList.size() > 0) {
			LOG.error("User with phone number is already registed, phone: {}", phone);
			throw new ApiException("该用户已注册");
		}

		PhoneVerification phoneVerify = phoneDao.selectByPrimaryKey(phone);
		if (phoneVerify == null) {
			LOG.error("Phone number is not exist in the phone verification table");
			throw new ApiException("未能获取该手机的验证码");
		}

		if (!code.equals(phoneVerify.getCode())) {
			LOG.error("Phone verify code is not corrected");
			throw new ApiException("未能获取该手机的验证码");
		}

		if (phoneVerify.getExpire() < DateUtil.getTime()) {
			LOG.error("Phone verify code is expired, please re acquisition");
			throw new ApiException("该验证码已过期，请重新获取验证码");
		}

		return ResponseDo.buildSuccessResponse("");
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
		// 调用运营商接口发送验证码短信
		String prop = PropertiesUtil.getProperty("message.send.format", "【车玩】 您的短信验证码为（{0}）");
		String message = MessageFormat.format(prop, verifyCode);

		String url = PropertiesUtil.getProperty("message.send.url", "");
		Map<String, String> queryParams = new HashMap<String, String>(4, 1);
		queryParams.put("user", PropertiesUtil.getProperty("message.send.username", ""));
		queryParams.put("pwd", PropertiesUtil.getProperty("message.send.password", ""));
		queryParams.put("phone", phone);
		queryParams.put("msgcont", message);

		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");

		CloseableHttpResponse response = HttpClientUtil.get(url, queryParams, Arrays.asList(header), "GBK");
		int status = response.getStatusLine().getStatusCode();
		LOG.info(response.toString());

		// 用完response需要释放资源
		HttpClientUtil.close(response);

		if (status == Constants.HTTP_STATUS_OK) {
			return ResponseDo.buildSuccessResponse("");
		} else {
			return ResponseDo.buildFailureResponse("未能成功获取验证码");
		}
	}

	/**
	 * 保存手机验证码到数据库中
	 * 
	 * @param phone
	 * @param verifyCode
	 */
	private void savePhoneVerification(String phone, String verifyCode) {
		// 更新数据库记录
		PhoneVerification existPhoneVerify = phoneDao.selectByPrimaryKey(phone);
		PhoneVerification phoneVerify = new PhoneVerification();
		phoneVerify.setPhone(phone);
		phoneVerify.setCode(verifyCode);
		phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.HOUR,
				PropertiesUtil.getProperty("message.effective.seconds", 7200)));

		if (existPhoneVerify == null) {
			phoneDao.insert(phoneVerify);
		} else {
			phoneDao.updateByPrimaryKey(phoneVerify);
		}
	}



}
