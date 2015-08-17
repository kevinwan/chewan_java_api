package com.gongpingjia.carplay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.dao.impl.TokenVerificationDaoImpl;
import com.gongpingjia.carplay.po.TokenVerification;

/**
 * 业务参数检查
 * 
 * @author licheng
 *
 */
public class ParameterCheck {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterCheck.class);

	private static ParameterCheck instance = new ParameterCheck();

	private TokenVerificationDao tokenDao;

	private ParameterCheck() {
		tokenDao = new TokenVerificationDaoImpl();
	}

	public static ParameterCheck getInstance() {
		return instance;
	}

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

		TokenVerification tokenVerify = tokenDao.selectByPrimaryKey(userId);
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
			LOG.error("Paramter [{}={}] is not Long type", paramName, paramValue);
			LOG.error(e.getMessage(), e);
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
			LOG.error("Paramter [{}={}] is not Integer type", paramName, paramValue);
			LOG.error(e.getMessage(), e);
			throw new ApiException("输入参数有误");
		}
	}
}
