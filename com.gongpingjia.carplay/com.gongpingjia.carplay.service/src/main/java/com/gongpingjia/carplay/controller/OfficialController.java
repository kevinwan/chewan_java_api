package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.service.OfficialService;

/**
 * 官方活动相关的操作
 * 
 * @author licheng
 *
 */
@RestController
public class OfficialController {
	private static final Logger LOG = LoggerFactory.getLogger(OfficialController.class);

	@Autowired
	private OfficialService service;

	/**
	 * 2.50 官方活动
	 * 
	 * @return 获取官方活动信息
	 */
	@RequestMapping(value = "/official/activity/list", method = RequestMethod.GET)
	public ResponseDo getActivityList() {
		LOG.info("getActivityList begin");

		return service.getActivityList();
	}
}
