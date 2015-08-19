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
import com.gongpingjia.carplay.service.MessageService;
import com.gongpingjia.carplay.service.impl.ParameterCheck;

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

	/**
	 * 2.26 获取申请列表
	 * 
	 * @param userId
	 *            访问者的userId
	 * @param request
	 *            请求参数
	 * 
	 * @return 活动申请列表信息
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/application/list", method = RequestMethod.GET)
	public ResponseDo getApplicationList(@PathVariable("userId") String userId, HttpServletRequest request) {

		LOG.debug("=> getApplicationList");
		String token = request.getParameter("token");
		int ignore = request.getParameter("ignore") == null ? 0 : Integer.valueOf(request.getParameter("ignore"));
		int limit = request.getParameter("limit") == null ? 10 : Integer.valueOf(request.getParameter("limit"));

		try {
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.getApplicationList(userId, ignore, limit);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
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
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.getMessageCount(userId);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

	}

	/**
	 * 2.42 获取消息列表
	 * 
	 * @param userId
	 *            用户Id
	 * @param request
	 *            请求参数
	 * @return 消息列表
	 */
	@RequestMapping(value = "/user/{userId}/message/list", method = RequestMethod.GET)
	public ResponseDo getMessageList(@PathVariable("userId") String userId, HttpServletRequest request) {
		LOG.debug("==> getMessageList");

		String token = request.getParameter("token");
		String type = request.getParameter("type");
		int ignore = request.getParameter("ignore") == null ? 0 : Integer.valueOf(request.getParameter("ignore"));
		int limit = request.getParameter("limit") == null ? 10 : Integer.valueOf(request.getParameter("limit"));

		try {
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.getMessageList(userId, type, ignore, limit);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.44 提交反馈信息
	 * 
	 * @param request
	 *            请求参数
	 * 
	 * @return 提交成功
	 */
	@RequestMapping(value = "/feedback/submit", method = RequestMethod.POST)
	public ResponseDo submitFeedback(HttpServletRequest request) {
		LOG.debug("==> submitFeedback");

		String userId = request.getParameter("userId");
		String token = request.getParameter("token");
		String content = request.getParameter("content");
		String[] photos = request.getParameterValues("photos");
		try {
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.submitFeedback(userId, content, photos);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.47批量删除消息
	 * 
	 * @param request
	 *            请求参数
	 *
	 * @return 删除成功
	 */
	@RequestMapping(value = "/message/remove", method = RequestMethod.POST)
	public ResponseDo removeMessages(HttpServletRequest request) {
		LOG.debug("==> removeMessages");

		String userId = request.getParameter("userId");
		String token = request.getParameter("token");
		String[] messages = request.getParameterValues("messages");

		try {
			ParameterCheck.getInstance().checkUserInfo(userId, token);
			return messageService.removeMessages(userId,messages);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
