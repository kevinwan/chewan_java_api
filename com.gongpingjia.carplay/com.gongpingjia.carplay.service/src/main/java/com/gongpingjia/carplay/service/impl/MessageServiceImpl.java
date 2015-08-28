package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.enums.ApplicationStatus;
import com.gongpingjia.carplay.common.enums.MessageType;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.controller.VersionController;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.dao.ActivityCommentDao;
import com.gongpingjia.carplay.dao.AuthenticationApplicationDao;
import com.gongpingjia.carplay.dao.FeedbackDao;
import com.gongpingjia.carplay.dao.FeedbackPhotoDao;
import com.gongpingjia.carplay.dao.MessageDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.Feedback;
import com.gongpingjia.carplay.po.FeedbackPhoto;
import com.gongpingjia.carplay.po.Message;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private ActivityApplicationDao activityApplicationDao;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private AuthenticationApplicationDao authenticationApplicationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private FeedbackPhotoDao feedbackPhotoDao;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ActivityCommentDao activityCommentDao;

	@Override
	public ResponseDo getApplicationList(String userId, int ignore, int limit) throws ApiException {
		LOG.debug("select activityApplicationList");

		Map<String, Object> param = new HashMap<String, Object>(5, 1);
		param.put("userId", userId);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		param.put("assertUrl", PropertiesUtil.getProperty("qiniu.server.url", ""));
		param.put("gpjImgUrl", PropertiesUtil.getProperty("gongpingjia.brand.logo.url", ""));
		List<Map<String, Object>> activityApplicationList = activityApplicationDao.selectByOrganizer(param);

		return ResponseDo.buildSuccessResponse(activityApplicationList);
	}

	@Override
	public ResponseDo getMessageCount(String userId) throws ApiException {
		LOG.debug("select message count");
		Map<String, Object> messageCountMap = new HashMap<String, Object>(2, 1);

		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());

		Map<String, Object> commentMap = new HashMap<String, Object>(4, 1);
		Map<String, Object> commentContent = messageDao.selectContentByUserAndTypeComment(param);
		commentMap.putAll(buildContentMap(commentContent));
		commentMap.put("count", messageDao.selectCountByUserAndTypeComment(param));
		commentMap.put("type", MessageType.COMMENT.getName());
		messageCountMap.put("comment", commentMap);

		Map<String, Object> application = new HashMap<String, Object>(4, 1);
		Map<String, Object> applicationContent = messageDao.selectContentByUserAndTypeNotComment(param);
		application.putAll(buildContentMap(applicationContent));
		application.put("count", messageDao.selectCountByUserAndTypeNotComment(param));
		messageCountMap.put("application", application);

		return ResponseDo.buildSuccessResponse(messageCountMap);
	}

	/**
	 * 根据content构造返回值，如果为null构造初始值
	 * 
	 * @param content
	 *            内容Map
	 * @return 返回构造之后的Map
	 */
	private Map<String, Object> buildContentMap(Map<String, Object> content) {
		if (content == null) {
			content = new HashMap<String, Object>(3, 1);
			content.put("content", "");
			content.put("createTime", 0);
			content.put("type", "");
		}
		return content;
	}

	@Override
	public ResponseDo getMessageList(String userId, String type, int ignore, int limit) throws ApiException {
		LOG.debug("Get message list begin");

		Map<String, Object> param = new HashMap<String, Object>(6, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("brandImgUrl", PropertiesUtil.getProperty("gongpingjia.brand.logo.url", ""));
		param.put("assetImgUrl", PropertiesUtil.getProperty("qiniu.server.url", ""));

		List<Map<String, Object>> messageList = new ArrayList<Map<String, Object>>(0);

		if (type.equals("comment")) {
			messageList = messageDao.selectMessageListByUserAndTypeComment(param);

			Map<String, Object> paramUp = new HashMap<String, Object>(2, 1);
			paramUp.put("userId", userId);
			paramUp.put("type", MessageType.COMMENT.getName());
			messageDao.updateIsCheckedByUserAndTypeComment(paramUp);

		} else if (type.equals("application")) {
			messageList = messageDao.selectMessageListByUserAndTypeNotComment(param);

			Map<String, Object> paramUp = new HashMap<String, Object>(2, 1);
			paramUp.put("userId", userId);
			paramUp.put("type", MessageType.COMMENT.getName());
			messageDao.updateIsCheckedByUserAndTypeCommentNotComment(paramUp);

			for (int i = 0; i < messageList.size(); i++) {
				if (MessageType.AUTHENTICATION.getName().equals(messageList.get(i).get("type"))) {
					Map<String, Object> paramModel = new HashMap<String, Object>(1, 1);
					paramModel.put("applicationId", messageList.get(i).get("applicationId"));
					List<Map<String, Object>> carModel = authenticationApplicationDao.selectCarModelbyId(paramModel);

					if (!carModel.isEmpty()) {
						messageList.get(i).put("carModel", carModel.get(0).get("carModel"));
					}
				}
			}
		}

		return ResponseDo.buildSuccessResponse(messageList);
	}

	@Override
	public ResponseDo submitFeedback(String userId, String content, String[] photos) throws ApiException {
		LOG.debug("Submit feedback begin");

		String feedbackId = CodeGenerator.generatorId();
		Feedback feedback = new Feedback();
		feedback.setId(feedbackId);
		feedback.setContent(content);
		feedback.setCreatetime(DateUtil.getTime());

		if (!StringUtils.isEmpty(userId)) {
			LOG.debug("Input userId is not empty, record user info, userId:{}", userId);
			User user = userDao.selectByPrimaryKey(userId);
			feedback.setNickname(user.getNickname());
			feedback.setPhone(user.getPhone());
			feedback.setUserid(user.getId());
		}

		feedbackDao.insert(feedback);

		if (photos != null) {
			for (String photo : photos) {
				String key = MessageFormat.format(Constants.PhotoKey.FEEDBACK_KEY, photo);
				// 注意：这里如果反馈的Photo不存在的话，是否需要抛出异常？
				if (photoService.isExist(key)) {
					FeedbackPhoto feedbackPhoto = new FeedbackPhoto();
					feedbackPhoto.setFeedbackid(feedbackId);
					feedbackPhoto.setId(photo);
					feedbackPhoto.setUploadtime(DateUtil.getTime());
					feedbackPhoto.setUrl(key);
					feedbackPhotoDao.deleteByPrimaryKey(photo);
					feedbackPhotoDao.insert(feedbackPhoto);
				}
			}
		}
		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo removeMessages(String userId, String[] messages) throws ApiException {
		LOG.debug("remove messages begin");

		List<String> messageIds = new ArrayList<String>(messages.length);

		for (String messageId : messages) {
			if (!CommonUtil.isUUID(messageId)) {
				LOG.warn("Message id is not uuid format, messageId:{}", messageId);
				throw new ApiException("输入参数错误");
			}

			Map<String, Object> param = new HashMap<String, Object>(2, 1);
			param.put("messageId", messageId);
			param.put("userId", userId);
			Message message = messageDao.selectByMeesageIdAndUserId(param);

			if (message == null) {
				LOG.error("Message not found : {}", messageId);
				throw new ApiException("未找到该消息");
			}
			messageIds.add(messageId);
		}

		messageDao.updateIsDeletedByMessageId(messageIds);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo removeComments(String userId, String[] comments) throws ApiException {

		List<String> commentIds = new ArrayList<String>(comments.length);
		for (String commentId : comments) {
			if (!CommonUtil.isUUID(commentId)) {
				LOG.warn("invalid comment id {}", commentId);
				throw new ApiException("输入参数有误");
			}

			Map<String, Object> param = new HashMap<String, Object>(1);
			param.put("commentId", commentId);
			List<Map<String, Object>> commentinfo = activityCommentDao.selectAuthorAndOrganizerByCommentId(param);

			if (commentinfo.size() == 0) {
				LOG.warn("Fail to get comment author and activity organizer");
				throw new ApiException("未能获取该评论消息");
			}

			Map<String, Object> comment = commentinfo.get(0);
			if (!userId.equals(comment.get("author")) && !userId.equals(comment.get("organizer"))) {
				LOG.warn("Only organizer or author can delete this comment");
				throw new ApiException("只有活动管理员或评论发布者有权删除该条评论");
			}

			commentIds.add(commentId);
		}

		activityCommentDao.deleteByPrimaryKey(commentIds);

		return ResponseDo.buildSuccessResponse();
	}
}
