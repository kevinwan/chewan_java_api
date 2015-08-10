package com.gongpingjia.carplay.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.po.Version;
import com.gongpingjia.carplay.service.VersionService;

@RestController
public class VersionController {

	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private VersionService service;

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public ResponseDo version(@RequestParam(value = "product", defaultValue = "android") String product) {

		LOG.debug("version is called, request parameter produce:" + product);

		Version ver = service.getVersion(product);

		Map<String, Object> data = buildData(ver);

		LOG.debug("version is called, response data:" + data);

		return ResponseDo.buildSuccessResponse(data);
	}

	/**
	 * 由获取的结果信息映射成返回结果所需的Data对象数据
	 * 
	 * @param ver
	 *            版本信息
	 * @return data对象
	 */
	private Map<String, Object> buildData(Version ver) {
		Map<String, Object> map = new HashMap<String, Object>(5, 1);
		map.put("product", ver.getProduct());
		map.put("version", ver.getVersion());
		map.put("forceUpgrade", ver.getForceupgrade());
		map.put("url", ver.getUrl());
		map.put("remarks", ver.getRemarks());

		return map;
	}

}
