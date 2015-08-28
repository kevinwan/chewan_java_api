package com.gongpingjia.carplay.controller.server;

import com.gongpingjia.carplay.controller.ActivityController;
import com.gongpingjia.carplay.controller.CarController;
import com.gongpingjia.carplay.controller.MessageController;
import com.gongpingjia.carplay.controller.PhoneController;
import com.gongpingjia.carplay.controller.UploadController;
import com.gongpingjia.carplay.controller.UserActivityController;
import com.gongpingjia.carplay.controller.UserInfoController;
import com.gongpingjia.carplay.controller.VersionController;

public class ZindexTest {

	void index() {

		ActivityController activityController = new ActivityController();
		CarController carController = new CarController();
		MessageController messageController = new MessageController();
		PhoneController phoneController = new PhoneController();
		UploadController uploadController = new UploadController();
		UserActivityController userActivityController = new UserActivityController();
		UserInfoController userInfoController = new UserInfoController();
		VersionController versionController = new VersionController();

		// 2.1 获取注册验证码
		phoneController.sendPhoneVerification(null, 0);

		// 2.2 验证码校验
		phoneController.checkPhoneVerification(null, null, null);

		// 2.3省市列表
		// 采用XML替代，在客户端实现

		// 2.4 头像上传
		uploadController.uploadUserPhoto(null);

		// 2.5注册
		userInfoController.register(null, null, null, null, null, null, null, null, null, null);

		// 2.6 获取品牌
		carController.carBrand();

		// 2.7获取车型信息
		carController.getCarModel(null);

		// 2.8登录
		userInfoController.loginUser(null, null);

		// 2.9 忘记密码
		userInfoController.forgetPassword(null, null, null);

		// 2.10 行驶证上传
		uploadController.uploadLicensePhoto(null, null, null);

		// 2.11 车主认证申请
		userInfoController.applyAuthentication(null, null, null, null, null, null, null);

		// 2.12(已停用)

		// 2.13 获取可提供的空座数
		activityController.getAvailableSeats(null, null, null);

		// 2.14 活动图片上传
		uploadController.uploadCoverPhoto(null, null, null);

		// 2.15 创建活动
		activityController.registerActivity(null);

		// 2.16 获取热门/附近/最新活动列表
		activityController.getActivityList(null);

		// 2.17 获取活动详情
		activityController.getActivityInfo(null, null, null);

		// 2.18 获取活动评论
		activityController.getActivityComments(null, null, null, null, null);

		// 2.19评论活动
		activityController.publishComment(null, null, null, null, null);

		// 2.20 个人详情
		userInfoController.userInfo(null, null, null);

		// 2.21 我(TA)的发布
		userActivityController.getUserPost(null, null, null, null, null);

		// 2.22 我(TA)的关注
		userActivityController.getUserSubscribe(null, null, null, null, null);

		// 2.23 我(TA)的参与
		userActivityController.getUserJoin(null, null, null, null, null);

		// 2.24 关注活动
		activityController.subscribeActivity(null, null, null);

		// 2.25 申请加入活动
		activityController.joinActivity(null, null, null, null);

		// 2.26 获取申请列表
		messageController.getApplicationList(null, null, null, null);

		// 2.27 同意/拒绝 活动申请
		activityController.processApplication(null, null, null, null);

		// 2.28 获取车座/成员信息
		activityController.getMemberAndSeatInfo(null, null, null);

		// 2.29 立即抢座
		activityController.takeSeat(null, null, null, null, null);

		// 2.30 拉下座位
		activityController.returnSeat(null, null, null, null);

		// 2.31 移除成员
		activityController.removeMember(null, null, null, null);

		// 2.32 退出活动
		activityController.quitActivity(null, null, null);

		// 2.33 编辑活动
		activityController.alterActivityInfo(null, null, null, null);

		// 2.34 我关注的人
		userInfoController.userListen(null, null, null, null);

		// 2.35 关注其他用户
		userInfoController.payAttention(null, null, null);

		// 2.36 取消关注其他用户
		userInfoController.unPayAttention(null, null, null);

		// 2.37 更改头像
		uploadController.alterAvatar(null, null, null);

		// 2.38 变更我的信息
		userInfoController.alterUserInfo(null, null, null, null, null, null, null, null);

		// 2.39 相册图片上传
		uploadController.uploadAlbumPhoto(null, null, null);

		// 2.40 编辑相册图片
		userInfoController.manageAlbumPhotos(null, null, null);

		// 2.41 获取最新消息数
		messageController.getMessageCount(null, null);

		// 2.42 获取消息列表
		messageController.getMessageList(null, null, null, null, null);

		// 2.43 上传意见反馈图片
		uploadController.uploadFeedbackPhoto(null);

		// 2.44 提交反馈信息
		messageController.submitFeedback(null, null, null, null);

		// 2.45 取消关注活动
		activityController.unsubscribeActivity(null, null, null);

		// 2.46 获取最新版本信息
		versionController.version(null);

		// 2.47批量删除消息
		messageController.removeMessages(null, null, null);

		// 2.48 批量删除评论
		messageController.removeComments(null, null, null);

	}
}
