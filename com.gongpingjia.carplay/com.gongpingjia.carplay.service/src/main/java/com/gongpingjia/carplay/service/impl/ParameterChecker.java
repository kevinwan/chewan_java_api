package com.gongpingjia.carplay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.po.PhoneVerification;
import com.gongpingjia.carplay.po.TokenVerification;

/**
 * 业务参数检查
 * 
 * @author licheng
 *
 */
@Service
public class ParameterChecker {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterChecker.class);

	@Autowired
	private TokenVerificationDao tokenDao;

	@Autowired
	private PhoneVerificationDao phoneDao;

	@Autowired
	private CacheManager cacheManager;

	/**
	 * 检查传入的参数userID和token的合格性，以及token是否过期
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话token
	 * @throws ApiException
	 *             如果参数错误则抛出异常
	 */
	public void checkUserInfo(String userId, String token) throws ApiException {

		if ((!CommonUtil.isUUID(userId)) || (!CommonUtil.isUUID(token))) {
			LOG.error("userId or token is not correct format UUID string, userId:{}, token:{}", userId, token);
			throw new ApiException("输入参数有误");
		}

		TokenVerification tokenVerify = cacheManager.getUserTokenVerification(userId);
		if (tokenVerify == null) {
			LOG.error("No user token exist in the system, userId:{}", userId);
			throw new ApiException("用户不存在");
		}

		if (!tokenVerify.getToken().equals(token)) {
			LOG.error("User token response to userId in the system, token:{}", token);
			throw new ApiException("输入参数有误");
		}

		if (tokenVerify.getExpire() < DateUtil.getTime()) {
			LOG.error("User token is out of date, userId: {}", userId);
			throw new ApiException("口令已过期，请重新登录获取新口令");
		}
	}

	/**
	 * 检查用户是否存在
	 * 
	 * @param userId
	 *            用户ID
	 * @return 用户存在返回true， 用户不存在返回false
	 */
	public boolean isUserExist(String userId) {
		TokenVerification tokenVerify = cacheManager.getUserTokenVerification(userId);
		if (tokenVerify == null) {
			LOG.warn("No user token exist in the system, userId:{}", userId);
			return false;
		}

		return true;
	}

	/**
	 * 检查参数是否为空，为空抛出异常
	 * 
	 * @param paramName
	 *            参数名称，主要用于记录日志
	 * @param paramValue
	 *            参数值
	 * @throws ApiException
	 *             参数为空抛出业务异常
	 */
	public void checkParameterEmpty(String paramName, String paramValue) throws ApiException {
		if (StringUtils.isEmpty(paramValue)) {
			LOG.error("Parameter {} is empty", paramName);
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查参数是否为空，为空抛出异常
	 * 
	 * @param paramName
	 *            参数名称，主要用于记录日志
	 * @param paramValue
	 *            参数值
	 * @throws ApiException
	 *             参数为空抛出业务异常
	 */
	public void checkParameterEmpty(String paramName, String[] paramValues) throws ApiException {
		if (paramValues == null || paramValues.length == 0) {
			LOG.error("Parameter {} is empty", paramName);
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查参数是否为Long类型
	 * 
	 * @param paramName
	 *            参数名称
	 * @param paramValue
	 *            参数值
	 * @throws ApiException
	 *             业务异常信息,参数不为Long类型
	 */
	public void checkParameterLongType(String paramName, String paramValue) throws ApiException {
		try {
			Long.valueOf(paramValue);
		} catch (NumberFormatException e) {
			LOG.warn("Paramter [{}={}] is not Long type", paramName, paramValue);
			LOG.warn(e.getMessage(), e);
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查参数是否为Integer类型
	 * 
	 * @param paramName
	 *            参数名称
	 * @param paramValue
	 *            参数值
	 * @throws ApiException
	 *             业务异常信息,参数不为Long类型
	 */
	public void checkParameterIntegerType(String paramName, String paramValue) throws ApiException {
		try {
			Integer.valueOf(paramValue);
		} catch (NumberFormatException e) {
			LOG.warn("Paramter [{}={}] is not Integer type", paramName, paramValue);
			LOG.warn(e.getMessage(), e);
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查参数是否为UUID类型，如果不是抛出"输入参数有误异常"
	 * 
	 * @param paramName
	 *            参数名称
	 * @param value
	 *            参数值
	 * @throws ApiException
	 *             业务异常信息
	 */
	public void checkParameterUUID(String paramName, String value) throws ApiException {
		if ((!CommonUtil.isUUID(value))) {
			LOG.error("Parameter {} is not correct format UUID string, paramValue:{}", paramName, value);
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 验证手机号和验证码是否匹配
	 * 
	 * @param phone
	 *            手机号
	 * @param code
	 *            验证码
	 * @throws ApiException
	 *             不匹配抛出异常
	 */
	public void checkPhoneVerifyCode(String phone, String code) throws ApiException {
		PhoneVerification phoneVerify = phoneDao.selectByPrimaryKey(phone);
		if (phoneVerify == null) {
			LOG.warn("Phone number is not exist in the phone verification table");
			throw new ApiException("未能获取该手机的验证码");
		}

		if (!code.equals(phoneVerify.getCode())) {
			LOG.warn("Phone verify code is not corrected");
			throw new ApiException("验证码有误");
		}

		if (phoneVerify.getExpire() < DateUtil.getTime()) {
			LOG.warn("Phone verify code is expired, please re acquisition");
			throw new ApiException("该验证码已过期，请重新获取验证码");
		}
	}
}
