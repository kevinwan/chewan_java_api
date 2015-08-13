package com.gongpingjia.carplay.controller;

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
import com.gongpingjia.carplay.service.ActivityService;

/**
 * 活动相关的操作
 * 
 * @author licheng
 *
 */
@RestController
public class ActivityController {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ActivityService activityService;

	/**
	 * 2.13 获取可提供的空座数
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @return 返回响应对象
	 */
	@RequestMapping(value = "/user/{userId}/seats", method = RequestMethod.GET)
	public ResponseDo getAvailableSeats(@PathVariable("userId") String userId, @RequestParam("token") String token) {
		LOG.info("getAvailableSeats with userId: {}", userId);

		try {
			return activityService.getAvailableSeats(userId, token);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
