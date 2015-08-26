package com.gongpingjia.carplay.service;

import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface PhoneService {
	/**
	 * 发送验证码
	 * 
	 * @param phone
	 *            手机号
	 * @return 发送成功返回成功信息，否则返回失败信息
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo sendVerification(String phone, Integer type) throws ApiException;

	/**
	 * 验证用户验证码
	 * 
	 * @param phone
	 *            手机号
	 * @param code
	 *            验证码
	 * @return 返回验证结果信息
	 * @throws ApiException 
	 */
	@Transactional(readOnly = true)
	ResponseDo verify(String phone, String code, Integer type) throws ApiException;
}
