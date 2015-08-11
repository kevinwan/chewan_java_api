package com.gongpingjia.carplay.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

		if (!isPhoneNumber(phone)) {
			throw new ApiException("不是有效的手机号");
		}

		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("phone", phone);
		List<User> users = userDao.selectByParam(param);

		if (type == 0) {
			// 注册流程
			if (users.size() > 0) {
				// 用户已经存在，手机号不能重复注册
				throw new ApiException("手机号已被注册");
			}
		} else if (type == 1) {
			// 忘记密码流程
			if (users.size() == 0) {
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

	/**
	 * 往指定的手机上发送验证码消息
	 * 
	 * @param phone
	 * @param verifyCode
	 * @return
	 */
	private ResponseDo sendPhoneVerifyMessage(String phone, String verifyCode) {
		// 调用运营商接口发送验证码短信
		String message = MessageFormat.format(PropertiesUtil.getProperty("message.send.format", "【车玩】 您的短信验证码为（{0}）"),
				verifyCode);

		String url = PropertiesUtil.getProperty("message.send.url", "");
		Map<String, String> queryParams = new HashMap<String, String>(4, 1);
		queryParams.put("user", PropertiesUtil.getProperty("message.send.username", ""));
		queryParams.put("pwd", PropertiesUtil.getProperty("message.send.password", ""));
		queryParams.put("phone", phone);
		try {
			queryParams.put("msgcont", URLEncoder.encode(message, "GBK"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("Conver message failure, message: " + message, e);
			queryParams.put("msgcont", verifyCode);
		}

		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");

		CloseableHttpResponse response = HttpClientUtil.get(url, queryParams, Arrays.asList(header));

		if (Constants.HTTP_STATUS_OK == response.getStatusLine().getStatusCode()) {
			return ResponseDo.buildSuccessResponse("");
		} else {
			LOG.info(response.toString());
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

	@Override
	public ResponseDo verify(String phone, String code) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isPhoneNumber(String phone) {
		if (StringUtils.isEmpty(phone)) {
			LOG.error("phone number is empty");
			return false;
		}

		// 手机号必须为长度为11的数字字符串
		if (!phone.matches("^[0-9]{11}$")) {
			LOG.error("phone number is not a number sequence which length is 11");
			return false;
		}

		return true;
	}

}
