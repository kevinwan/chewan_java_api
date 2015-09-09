package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.TypeConverUtil;
import com.gongpingjia.carplay.service.PhoneService;

import net.sf.json.JSONObject;

/**
 * 手机验证码相关的操作
 * 
 * @author licheng
 *
 */
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
	public ResponseDo sendPhoneVerification(@PathVariable("phone") String phone,
			@RequestParam(value = "type", defaultValue = "0") Integer type) {

		LOG.debug("sendPhoneVerification begin");
		try {
			return service.sendVerification(phone, type);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
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
	@RequestMapping(value = "/phone/{phone}/verification", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo checkPhoneVerification(@PathVariable("phone") String phone, @RequestBody JSONObject json) {

		LOG.debug("checkPhoneVerification begin");

		try {
			if (CommonUtil.isEmpty(json, "code")) {
				LOG.warn("Input parameter nickname is empty");
				throw new ApiException("输入参数错误");
			}
			String code = json.getString("code");

			String typeString = CommonUtil.getString(json, "type", null);
			Integer type;
			if (CommonUtil.isEmpty(json, json.getString("type"))) {
				type = 0;
			}
			type = TypeConverUtil.convertToInteger("type", typeString, true);
			if (type != 0 && type != 1) {
				LOG.warn("Input parameter type is not 1 or 0");
				throw new ApiException("输入参数有误");
			}

			return service.verify(phone, code, type);
		} catch (ApiException e) {
			LOG.warn(e.getMessage());
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}
}
