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
import com.gongpingjia.carplay.service.MessageService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;

/**
 * 消息message
 * 
 * @author zhou shuofu
 */
@RestController
public class MessageController {

	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private ParameterChecker checker;

	/**
	 * 2.26 获取申请列表
	 * 
	 * @param userId
	 *            访问者的userId
	 * @param token
	 *            访问者的token
	 * @param ignore
	 * 
	 * @param limit
	 * 
	 * @return 活动申请列表信息
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/application/list", method = RequestMethod.GET)
	public ResponseDo getApplicationList(@PathVariable("userId") String userId, @RequestParam("token") String token,
			@RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {

		LOG.debug("=> getApplicationList");

		try {
			checker.checkUserInfo(userId, token);
			return messageService.getApplicationList(userId, ignore, limit);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.41 获取最新消息数
	 * 
	 * @param userId
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * 
	 * @return 未读的消息数量和最新的一条消息信息
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/message/count", method = RequestMethod.GET)
	public ResponseDo getMessageCount(@PathVariable("userId") String userId, @RequestParam("token") String token) {

		LOG.debug("=> getMessageCount");

		try {
			checker.checkUserInfo(userId, token);
			return messageService.getMessageCount(userId);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

	}

	/**
	 * 2.42 获取消息列表
	 * 
	 * @param userId
	 *            用户Id
	 * @param token
	 * 
	 * @param type
	 *            消息类型，comment与application
	 * 
	 * @param ignore
	 * 
	 * @param limit
	 * 
	 * @return 消息列表
	 */
	@RequestMapping(value = "/user/{userId}/message/list", method = RequestMethod.GET)
	public ResponseDo getMessageList(@PathVariable("userId") String userId, @RequestParam("token") String token,
			@RequestParam("type") String type, @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
		LOG.debug("==> getMessageList");

		try {
			if (!type.equals("comment") && !type.equals("application")) {
				LOG.warn("invalid params");
				throw new ApiException("消息类型有误");
			}
			checker.checkUserInfo(userId, token);

			return messageService.getMessageList(userId, type, ignore, limit);
		} catch (ApiException e) {

			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.44 提交反馈信息
	 * 
	 * @param userId
	 * 
	 * @param token
	 * 
	 * @param content
	 *            反馈信息
	 * 
	 * @param photos
	 *            反馈图片ID
	 * 
	 * @return 提交成功
	 */
	@RequestMapping(value = "/feedback/submit", method = RequestMethod.POST)
	public ResponseDo submitFeedback(@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam("content") String content, @RequestParam("photos") String[] photos) {

		LOG.debug("==> submitFeedback");

		try {
			if (content == null || (photos != null && photos.length > 3)) {
				LOG.warn("invalid params");
				throw new ApiException("请输入需要反馈的信息");
			}
			checker.checkUserInfo(userId, token);
			return messageService.submitFeedback(userId, content, photos);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.47批量删除消息
	 * 
	 * @param userId
	 * 
	 * @param token
	 * 
	 * @param messages
	 *            消息Id
	 *
	 * @return 删除成功
	 */
	@RequestMapping(value = "/message/remove", method = RequestMethod.POST)
	public ResponseDo removeMessages(@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam("messages") String[] messages) {
		LOG.debug("==> removeMessages");

		try {
			checker.checkUserInfo(userId, token);
			if (messages == null || messages.length == 0) {
				LOG.warn("invalid params");
				throw new ApiException("输入参数有误");
			}
			return messageService.removeMessages(userId, messages);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.48 批量删除评论
	 * 
	 * @param userId
	 * 
	 * @param token
	 * 
	 * @param comments
	 *            评论ID
	 * 
	 * @return 删除成功
	 * 
	 */
	@RequestMapping(value = "/comment/remove", method = RequestMethod.POST)
	public ResponseDo removeComments(@RequestParam("userId") String userId, @RequestParam("token") String token,
			@RequestParam("comments") String[] comments) {
		LOG.debug("==>removeComments");

		try {
			checker.checkUserInfo(userId, token);
			if (comments == null || comments.length == 0) {
				LOG.warn("invalid params");
				throw new ApiException("输入参数有误");
			}
			return messageService.removeComments(userId, comments);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

	}

}
