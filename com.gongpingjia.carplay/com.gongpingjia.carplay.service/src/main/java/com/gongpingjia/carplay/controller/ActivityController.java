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
	private ActivityService service;

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
	public ResponseDo getAvailableSeats(@PathVariable("userId") String userId, @RequestParam("token") String token,
			@RequestParam(value = "activityId", required = false) String activityId) {
		LOG.info("getAvailableSeats with userId: {}", userId);

		try {
			return service.getAvailableSeats(userId, token, activityId);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.15 创建活动
	 * 
	 * @param request
	 *            请求
	 * @return 返回响应结果对象
	 */
	@RequestMapping(value = "/activity/register", method = RequestMethod.POST)
	public ResponseDo registerActivity(HttpServletRequest request) {
		LOG.info("registerActivity begin");
		try {

			service.checkRegisterActivityParam(request);

			return service.registerActivity(request);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.16 获取热门/附近/最新活动列表
	 * 
	 * @param request
	 *            请求参数
	 * @return 返回响应结果
	 */
	@RequestMapping(value = "/activity/list", method = RequestMethod.GET)
	public ResponseDo getActivityList(HttpServletRequest request) {
		LOG.info("getActivityList begin");
		try {
			return service.getActivityList(request);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.17 获取活动详情
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回响应对象
	 */
	@RequestMapping(value = "/activity/{activityId}/info", method = RequestMethod.GET)
	public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "token", required = false) String token) {
		LOG.info("getActivityInfo begin");
		try {
			return service.getActivityInfo(activityId, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.18 获取活动评论
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @param ignore
	 *            忽略行数
	 * @param limit
	 *            限制行数
	 * @return 返回活动评论数据
	 */
	@RequestMapping(value = "/activity/{activityId}/comment", method = RequestMethod.GET)
	public ResponseDo getActivityComments(@PathVariable("activityId") String activityId,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "token", required = false) String token,
			@RequestParam(value = "ignore", defaultValue = "0", required = false) Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit) {
		LOG.info("getActivityComments begin");

		try {
			return service.getActivityComments(activityId, userId, token, ignore, limit);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.19 评论活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @param replyUserId
	 *            回复用户信息
	 * @param comment
	 *            评论的内容
	 * @return 返回评论结果
	 */
	@RequestMapping(value = "/activity/{activityId}/comment", method = RequestMethod.POST)
	public ResponseDo publishComment(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam(value = "replyUserId", required = false) String replyUserId,
			@RequestParam("comment") String comment) {
		LOG.info("publishComment begin");

		try {
			return service.publishComment(activityId, userId, token, replyUserId, comment);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.24 关注活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 关注结果
	 */
	@RequestMapping(value = "/activity/{activityId}/subscribe", method = RequestMethod.POST)
	public ResponseDo subscribeActivity(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token) {
		LOG.info("subscribeActivity begin");

		try {
			return service.subscribeActivity(activityId, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 
	 * 2.25 申请加入活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param seat
	 *            出几个座位，不出座位传0
	 * @return 返回加入结果信息
	 */
	@RequestMapping(value = "/activity/{activityId}/join", method = RequestMethod.POST)
	public ResponseDo joinActivity(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam("seat") Integer seat) {
		LOG.info("joinActivity begin");

		try {
			return service.joinActivity(activityId, userId, token, seat);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.27 同意/拒绝 活动申请
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @param action
	 *            对申请的处理，0代表拒绝， 1代表同意
	 * @return 返回处理结果
	 */
	@RequestMapping(value = "/application/{applicationId}/process", method = RequestMethod.POST)
	public ResponseDo processApplication(@PathVariable("applicationId") String applicationId,
			@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam("action") Integer action) {
		LOG.info("processApplication begin");

		try {
			return service.processApplication(applicationId, userId, token, action);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.28 获取车座/成员信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回结果响应对象
	 */
	@RequestMapping(value = "/activity/{activityId}/members", method = RequestMethod.GET)
	public ResponseDo getMemberAndSeatInfo(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token) {
		LOG.info("getMemberAndSeatInfo begin");
		try {
			return service.getMemberAndSeatInfo(activityId, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.29 立即抢座
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param carId
	 *            车辆ID
	 * @param seatIndex
	 *            座位索引
	 * @return 返回抢座结果
	 */
	@RequestMapping(value = "/activity/{activityId}/seat/take", method = RequestMethod.POST)
	public ResponseDo takeSeat(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId,
			@RequestParam("token") String token, @RequestParam("carId") String carId,
			@RequestParam("seatIndex") Integer seatIndex) {
		LOG.info("takeSeat begin");

		try {
			return service.takeSeat(activityId, userId, token, carId, seatIndex);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.30 拉下座位
	 * 
	 * @param activityId
	 *            活动ID
	 * @param member
	 *            活动中被拉下来的成员
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @return 返回被拉下来的结果
	 */
	@RequestMapping(value = "/activity/{activityId}/seat/return", method = RequestMethod.POST)
	public ResponseDo returnSeat(@PathVariable("activityId") String activityId, @RequestParam("member") String member,
			@RequestParam("userId") String userId, @RequestParam("token") String token) {
		LOG.info("returnSeat beging");

		try {
			return service.returnSeat(activityId, member, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.31 移除成员
	 * 
	 * @param activityId
	 *            活动ID
	 * @param member
	 *            活动中被移除的成员
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @return 返回被移除的结果
	 */
	@RequestMapping(value = "/activity/{activityId}/member/remove", method = RequestMethod.POST)
	public ResponseDo removeMember(@PathVariable("activityId") String activityId,
			@RequestParam("member") String member, @RequestParam("userId") String userId,
			@RequestParam("token") String token) {
		LOG.info("removeMember begin");
		try {
			return service.removeMember(activityId, member, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.32 退出活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回退出结果信息
	 * 
	 */
	@RequestMapping(value = "/activity/{activityId}/quit", method = RequestMethod.POST)
	public ResponseDo quitActivity(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token) {
		LOG.info("quitActivity begin");

		try {
			return service.quitActivity(activityId, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.33 编辑活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param request
	 *            编辑活动的请求参数
	 * @return 返回编辑结果
	 */
	@RequestMapping(value = "/activity/{activityId}/info", method = RequestMethod.POST)
	public ResponseDo alterActivityInfo(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token, HttpServletRequest request) {
		LOG.info("alterActivityInfo begin");
		try {
			service.checkRegisterActivityParam(request);

			return service.alterActivityInfo(activityId, userId, token, request);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.45 取消关注活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @return 返回结果对象
	 */
	@RequestMapping(value = "/activity/{activityId}/unsubscribe", method = RequestMethod.POST)
	public ResponseDo unsubscribeActivity(@PathVariable("activityId") String activityId,
			@RequestParam("userId") String userId, @RequestParam("token") String token) {
		LOG.info("unsubscribeActivity begin");
		try {
			return service.unsubscribeActivity(activityId, userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
