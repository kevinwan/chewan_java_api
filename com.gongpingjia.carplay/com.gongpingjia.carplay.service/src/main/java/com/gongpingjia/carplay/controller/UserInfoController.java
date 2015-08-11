package com.gongpingjia.carplay.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;

@RestController
public class UserInfoController {

	private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);
	
	@RequestMapping(value = "/user/register", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDo register() {

		LOG.debug("register is called, request parameter produce:");

		

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("test", "1");

		LOG.debug("register is called, response data:" + data);

		return ResponseDo.buildSuccessResponse(data);
	}
}
