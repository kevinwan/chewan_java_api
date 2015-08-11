package com.gongpingjia.carplay.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.PhoneService;

@RestController
public class PhoneController {

	private static final Logger LOG = LoggerFactory.getLogger(PhoneController.class);

	@Autowired
	private PhoneService service;

	/**
	 * 2.1 获取注册验证码
	 * 
	 * @param phone
	 *            手机号
	 * @param type
	 *            默认为0， 传1 表示在 忘记密码 的流程。
	 * @return 验证码信息
	 */
	@RequestMapping(value = "/phone/{phone}/verification", method = RequestMethod.GET)
	public ResponseDo sendPhoneVerification(@PathVariable String phone,
			@RequestParam(value = "type", defaultValue = "0") int type) {

		LOG.debug("sendPhoneVerification begin");
		try {
			return service.sendVerification(phone, type);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.2 验证码校验
	 * 
	 * @param phone
	 *            手机号
	 * @param request
	 *            请求体信息
	 * @return 返回验证结果信息
	 */
	@RequestMapping(value = "/phone/{phone}/verification", method = RequestMethod.POST)
	public ResponseDo checkPhoneVerification(@PathVariable String phone, HttpServletRequest request) {

		LOG.debug("checkPhoneVerification begin");

		String code = (String) request.getAttribute("code");

		try {
			return service.verify(phone, code);
		} catch (ApiException e) {
			LOG.error(e.getMessage());
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}
}
