package com.gongpingjia.carplay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.po.TokenVerification;

public class ParameterCheck {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterCheck.class);

	private static ParameterCheck instance = new ParameterCheck();

	@Autowired
	private TokenVerificationDao tokenDao;

	private ParameterCheck() {
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

		if ((!CodeGenerator.isUUID(userId)) || (!CodeGenerator.isUUID(token))) {
			LOG.error("userId or token is not correct format UUID string, userId:{0}, token:{1}", userId, token);
			throw new ApiException("输入参数有误");
		}

		TokenVerification tokenVerify = tokenDao.selectByPrimaryKey(userId);
		if (tokenVerify == null) {
			LOG.error("No user token exist in the system, userId:{0}", userId);
			throw new ApiException("用户不存在");
		}

		if (tokenVerify.getExpire() < DateUtil.getTime()) {
			LOG.error("User token is out of date, userId: {0}", userId);
			throw new ApiException("口令已过期，请重新登录获取新口令");
		}
	}

}
