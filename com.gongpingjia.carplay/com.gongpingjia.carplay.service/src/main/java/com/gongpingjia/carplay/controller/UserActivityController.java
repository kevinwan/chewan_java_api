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
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.service.UserActivityService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;

/**
 * 2.21 我(TA)的发布, 2.22 我(TA)的关注, 2.23 我(TA)的参与,
 * 
 * @author zhou shuofu
 */

@RestController
public class UserActivityController {
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private UserActivityService userActivityService;

	@Autowired
	private ParameterChecker checker;

	/**
	 * 2.21 我(TA)的发布
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动发布列表信息
	 */
	@RequestMapping(value = "/user/{userId1}/post", method = RequestMethod.GET)
	public ResponseDo getUserPost(@PathVariable("userId1") String userId1,
			@RequestParam(value = "userId") String userId2, @RequestParam(value = "token") String token,
			@RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {

		LOG.info("=> getUserPost");

		if (!CommonUtil.isUUID(userId1)) {
			LOG.warn("Invalid params, userId1:{}", userId1);
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userId2, token);

			return userActivityService.getUserPost(userId1, userId2, token, ignore, limit);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.22 我(TA)的关注
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动发布列表信息
	 */
	@RequestMapping(value = "/user/{userId1}/subscribe", method = RequestMethod.GET)
	public ResponseDo getUserSubscribe(@PathVariable("userId1") String userId1,
			@RequestParam(value = "userId") String userId2, @RequestParam(value = "token") String token,
			@RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {

		LOG.info("=> getUserSubscribe");
		if (!CommonUtil.isUUID(userId1)) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userId2, token);

			return userActivityService.getUserSubscribe(userId1, userId2, token, ignore, limit);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.23 我(TA)的参与
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动发布列表信息
	 */
	@RequestMapping(value = "/user/{userId1}/join", method = RequestMethod.GET)
	public ResponseDo getUserJoin(@PathVariable("userId1") String userId1,
			@RequestParam(value = "userId") String userId2, @RequestParam(value = "token") String token,
			@RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {

		LOG.info("=> getUserJoin");

		if (!CommonUtil.isUUID(userId1)) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}
		try {
			checker.checkUserInfo(userId2, token);
			
			return userActivityService.getUserJoin(userId1, userId2, token, ignore, limit);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
