package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
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
	private String assetUrl = PropertiesUtil.getProperty("qiniu.server.url", "") + "asset";
	private String brandImgUrl = PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "");
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
		param.put("assertUrl", assetUrl);
		param.put("gpjImgUrl", brandImgUrl);
		List<Map<String, Object>> activityApplicationList = activityApplicationDao.selectByOrganizer(param);

		return ResponseDo.buildSuccessResponse(activityApplicationList);
	}

	@Override
	public ResponseDo getMessageCount(String userId) throws ApiException {
		LOG.debug("select message count");
		Map<String, Object> messageCountMap = new HashMap<String, Object>(2, 1);

		Map<String, Object> commentMap = new HashMap<String, Object>(2, 1);
		commentMap.put("content", getCommentContent(userId));
		commentMap.put("count", getCommentCount(userId));
		messageCountMap.put("comment", commentMap);

		Map<String, Object> application = new HashMap<String, Object>(2, 1);
		application.put("content", getApplicationContent(userId));
		application.put("count", getApplicationCount(userId));
		messageCountMap.put("application", application);

		return ResponseDo.buildSuccessResponse(messageCountMap);
	}

	private Integer getCommentCount(String userId) throws ApiException {
		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		return messageDao.selectCountByUserAndTypeComment(param);
	}

	private Integer getApplicationCount(String userId) throws ApiException {
		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		return messageDao.selectCountByUserAndTypeNotComment(param);
	}

	private String getCommentContent(String userId) {
		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		String content = messageDao.selectContentByUserAndTypeComment(param);
		if (content == null) {
			content = "";
		}
		return content;
	}

	private String getApplicationContent(String userId) {
		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("userId", userId);
		param.put("type", MessageType.COMMENT.getName());
		String content = messageDao.selectContentByUserAndTypeNotComment(param);
		if (content == null) {
			content = "";
		}
		return content;
	}

	@Override
	public ResponseDo getMessageList(String userId, String type, int ignore, int limit) throws ApiException {
		String typeflag = type.toString();

		type = MessageType.COMMENT.getName();
		Map<String, Object> param = new HashMap<String, Object>(6, 1);
		param.put("userId", userId);
		param.put("type", type);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("brandImgUrl", brandImgUrl);
		param.put("assetImgUrl", assetUrl);

		List<Map<String, Object>> messageList;

		if (typeflag.equals("comment")) {
			messageList = messageDao.selectMessageListByUserAndTypeComment(param);

			Map<String, Object> paramUp = new HashMap<String, Object>(2, 1);
			paramUp.put("userId", userId);
			paramUp.put("type", type);
			messageDao.updateIsCheckedByUserAndTypeComment(paramUp);

		} else if (typeflag.equals("application")) {
			messageList = messageDao.selectMessageListByUserAndTypeNotComment(param);

			Map<String, Object> paramUp = new HashMap<String, Object>(2, 1);
			paramUp.put("userId", userId);
			paramUp.put("type", type);
			messageDao.updateIsCheckedByUserAndTypeCommentNotComment(paramUp);

			for (int i = 0; i < messageList.size(); i++) {

				if (messageList.get(i).get("type").equals(MessageType.AUTHENTICATION.getName())) {
					Map<String, Object> paramModel = new HashMap<String, Object>(1, 1);
					paramModel.put("applicationId", messageList.get(i).get("applicationId"));
					List<Map<String, Object>> carModel = authenticationApplicationDao.selectCarModelbyId(paramModel);

					if (carModel.size() > 0)
						messageList.get(i).put("carModel", carModel.get(0).get("carModel"));
				}
			}
		} else {
			LOG.warn("error： getMessageList ，the messageType is error");
			messageList = null;
			throw new ApiException("消息类型错误");
		}
		return ResponseDo.buildSuccessResponse(messageList);
	}

	@Override
	public ResponseDo submitFeedback(String userId, String content, String[] photos) throws ApiException {
		User user = userDao.selectByPrimaryKey(userId);
		String feedbackId = CodeGenerator.generatorId();
		Feedback feedback = new Feedback();
		feedback.setId(feedbackId);
		feedback.setContent(content);
		feedback.setCreatetime(DateUtil.getTime());
		feedback.setNickname(user.getNickname());
		feedback.setPhone(user.getPhone());
		feedback.setUserid(user.getId());
		int affectedRows = feedbackDao.insert(feedback);
		if (affectedRows == 0) {
			LOG.warn("Fail to submit feedback");
			throw new ApiException("提交反馈意见失败");
		}
		if (photos != null) {
			for (String photo : photos) {
				if (photoService.isExist(MessageFormat.format(Constants.PhotoKey.FEEDBACK_KEY, photo))) {

					FeedbackPhoto feedbackPhoto = new FeedbackPhoto();
					feedbackPhoto.setFeedbackid(feedbackId);
					feedbackPhoto.setId(photo);
					feedbackPhoto.setUploadtime(DateUtil.getTime());
					String url = "/feedback/" + photo + ".jpg";
					feedbackPhoto.setUrl(url);
					feedbackPhotoDao.deleteByPrimaryKey(photo);
					affectedRows = feedbackPhotoDao.insert(feedbackPhoto);
					if (affectedRows == 0) {
						LOG.warn("Fail to insert into feedback_photo table");
						throw new ApiException("未能成功插入反馈图片");
					}
				}
			}
		}
		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo removeMessages(String userId, String[] messages) throws ApiException {
		for (String messageId : messages) {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("messageId", messageId);
			param.put("userId", userId);
			Message message = messageDao.selectByMeesageIdAndUserId(param);

			if (message == null) {
				LOG.error("Message not found : {}", messageId);
				throw new ApiException("未找到该消息");
			}

			int affeced = messageDao.updateIsDeletedByMessageId(messageId);
			if (affeced == 0) {
				LOG.error("Fail to delete message :{}", messageId);
				throw new ApiException("未能成功删除消息");
			}
		}
		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo removeComments(String userId, String[] comments) throws ApiException {
		for (String commentId : comments) {
			if (!CommonUtil.isUUID(commentId)) {
				LOG.warn("invalid comment id {}", commentId);
				throw new ApiException("评论id 格式有误");
			}

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("commentId", commentId);
			List<Map<String, Object>> commentinfo = activityCommentDao.selectAuthorAndOrganizerByCommentId(param);

			if (commentinfo.size() == 0) {
				LOG.warn("Fail to get comment author and activity organizer");
				throw new ApiException("未能获取该评论消息");
			}

			if (!commentinfo.get(0).get("author").equals(userId) && !commentinfo.get(0).get("organizer").equals(userId)) {
				LOG.warn("'Only organizer or author can delete this comment");
				throw new ApiException("只有活动管理员或评论发布者有权删除该条评论");
			}

			int affeced = activityCommentDao.deleteByPrimaryKey(commentId);
			if (affeced == 0) {
				LOG.error("Fail to delete comment :{}", commentId);
				throw new ApiException("未能成功删除评论");
			}
		}
		return ResponseDo.buildSuccessResponse("");
	}
}
