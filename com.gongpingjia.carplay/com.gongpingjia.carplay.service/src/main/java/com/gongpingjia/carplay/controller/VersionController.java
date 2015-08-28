package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.VersionService;

/**
 * 版本信息控制
 * 
 * @author licheng
 *
 */
@RestController
public class VersionController {

	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private VersionService service;

	/**
	 * 2.46 获取最新版本信息
	 * 
	 * @param product
	 *            产品类型
	 * @return 返回版本信息
	 */
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public ResponseDo version(@RequestParam(value = "product", defaultValue = "android") String product) {

		LOG.debug("version is called, request parameter produce:" + product);

		try {
			return service.getVersion(product);
		} catch (ApiException e) {
			LOG.warn(e.getMessage());
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
