package com.gongpingjia.carplay.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.phone.MessageService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
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

		PhoneVerification phoneVerification = savePhoneVerification(phone);

		int dayMaxSendTimes = PropertiesUtil.getProperty("message.send.day.max.times", 4);
		if (phoneVerification.getSendtimes() != null && phoneVerification.getSendtimes() > dayMaxSendTimes) {
			LOG.warn("Send message times has over the day max send times");
			throw new ApiException("今天验证码发送次数已经用完");
		}

		// 重新计算发送次数
		refreshPhoneVerification(phoneVerification);

		boolean sendResult = MessageService.sendMessage(phone, phoneVerification.getCode(),
				phoneVerification.getSendtimes());
		if (sendResult) {
			return ResponseDo.buildSuccessResponse();
		} else {
			LOG.error("Send message failure, phone:{}, verifyCode:{}", phone, phoneVerification.getCode());
			return ResponseDo.buildFailureResponse("验证码发送失败");
		}
	}

	private void refreshPhoneVerification(PhoneVerification phoneVerification) {
		Calendar calModify = Calendar.getInstance();
		Long modifyTime = phoneVerification.getModifiedtime();
		calModify.setTimeInMillis(modifyTime == null ? 0 : modifyTime);

		Long currentTime = DateUtil.getTime();
		Calendar current = Calendar.getInstance();
		current.setTimeInMillis(currentTime);

		if (calModify.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
				&& calModify.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {
			// 表示是同一天
			phoneVerification.setSendtimes(phoneVerification.getSendtimes() + 1);
		} else {
			// 不是同一天
			phoneVerification.setSendtimes(1);
		}
		phoneVerification.setModifiedtime(currentTime);
		phoneDao.updateByPrimaryKey(phoneVerification);
	}

	@Override
	public ResponseDo verify(String phone, String code, Integer type) throws ApiException {
		if (!CommonUtil.isPhoneNumber(phone)) {
			LOG.warn("Phone number is not correct format");
			throw new ApiException("不是有效的手机号");
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
	 * 保存手机验证码到数据库中
	 * 
	 * @param phone
	 */
	private PhoneVerification savePhoneVerification(String phone) {
		// 更新数据库记录
		PhoneVerification phoneVerify = phoneDao.selectByPrimaryKey(phone);
		if (phoneVerify == null) {
			// 不存在验证码
			phoneVerify = new PhoneVerification();
			phoneVerify.setPhone(phone);
			phoneVerify.setCode(CodeGenerator.generatorVerifyCode());
			phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
					PropertiesUtil.getProperty("message.effective.seconds", 7200)));
			phoneVerify.setSendtimes(1);
			phoneVerify.setModifiedtime(DateUtil.getTime());

			phoneDao.insert(phoneVerify);
		} else {
			// 已经存在验证码
			if (phoneVerify.getExpire() < DateUtil.getTime()) {
				LOG.debug("Exist phone verifyCode is out of date");
				phoneVerify.setCode(CodeGenerator.generatorVerifyCode());
				phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
						PropertiesUtil.getProperty("message.effective.seconds", 7200)));
				//过期了需要设置次数
				phoneDao.updateByPrimaryKey(phoneVerify);
			}
		}

		return phoneVerify;
	}

}
