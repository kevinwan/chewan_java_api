package com.gongpingjia.carplay.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.enums.ApplicationStatus;
import com.gongpingjia.carplay.common.enums.MessageType;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.controller.VersionController;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.dao.MessageDao;
import com.gongpingjia.carplay.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private ActivityApplicationDao activityApplicationDao;

	@Autowired
	private MessageDao messageDao;

	@Override
	public ResponseDo getApplicationList(String userId, int ignore, int limit) throws ApiException {

		String assetUrl = PropertiesUtil.getProperty("qiniu.server.url", "") + "asset";
		String gpjImgUrl = PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "");

		Map<String, Object> param = new HashMap<>(5, 1);
		param.put("userId", userId);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		param.put("assertUrl", assetUrl);
		param.put("gpjImgUrl", gpjImgUrl);
		List<Map<String, Object>> activityApplicationList = activityApplicationDao.selectByOrganizer(param);

		LOG.debug("select activityApplicationList");

		return ResponseDo.buildSuccessResponse(activityApplicationList);
	}

	@Override
	public ResponseDo getMessageCount(String userId) throws ApiException {

		String commentCount = getCommentCount(userId);
		String applicationCount = getApplicationCount(userId);
		String commentContent = getCommentContent(userId);
		String applicationContent = getApplicationContent(userId);

		LOG.debug("select success");

		Map<String, Object> commentMap = new HashMap<>(2, 1);
		Map<String, Object> application = new HashMap<>(2, 1);
		commentMap.put("content", commentContent);
		commentMap.put("count", commentCount);
		application.put("content", applicationContent);
		application.put("count", applicationCount);

		Map<String, Object> messageCountMap = new HashMap<>(2, 1);
		messageCountMap.put("comment", commentMap);
		messageCountMap.put("application", application);

		return ResponseDo.buildSuccessResponse(messageCountMap);
	}

	private String getCommentCount(String userId) throws ApiException {
		Map<String, Object> param = new HashMap<>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		List<Map<String, Object>> messageCountList = messageDao.selectCountByUserAndTypeComment(param);
		if (messageCountList.size() == 0) {
			LOG.error("Fail to get comment count");
			throw new ApiException("未能获取发给该用户的留言数");
		}
		return String.valueOf(messageCountList.get(0).get("count"));
	}

	private String getApplicationCount(String userId) throws ApiException {
		Map<String, Object> param = new HashMap<>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		List<Map<String, Object>> messageCountList = messageDao.selectCountByUserAndTypeNotComment(param);
		if (messageCountList.size() == 0) {
			LOG.error("Fail to get application count");
			throw new ApiException("未能获取发给该用户的活动申请数");
		}
		return String.valueOf(messageCountList.get(0).get("count"));
	}

	private String getCommentContent(String userId) {
		Map<String, Object> param = new HashMap<>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		List<Map<String, Object>> messageCountList = messageDao.selectContentByUserAndTypeComment(param);
		if (messageCountList.size() == 0)
			return "";
		return (String) messageCountList.get(0).get("Content");
	}

	private String getApplicationContent(String userId) {
		Map<String, Object> param = new HashMap<>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		List<Map<String, Object>> messageCountList = messageDao.selectContentByUserAndTypeNotComment(param);
		if (messageCountList.size() == 0)
			return "";
		return (String) messageCountList.get(0).get("Content");
	}

}
