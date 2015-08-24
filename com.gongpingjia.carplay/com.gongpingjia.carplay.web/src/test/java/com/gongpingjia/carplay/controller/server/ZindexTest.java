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
	
	void index(){
		
		ActivityController ActivityController=new ActivityController();
		CarController CarController=new CarController();
		MessageController MessageController=new MessageController();
		PhoneController PhoneController=new PhoneController();
		UploadController UploadController=new UploadController();
		UserActivityController UserActivityController= new UserActivityController();
		UserInfoController UserInfoController=new UserInfoController();
		VersionController VersionController=new VersionController();
	
		//2.1 获取注册验证码
		PhoneController.sendPhoneVerification(null, 0);
		
		//2.2 验证码校验
		PhoneController.checkPhoneVerification(null, null);
		
		//2.3省市列表
		//采用XML替代，在客户端实现

		//2.4 头像上传
		UploadController.uploadUserPhoto(null, null);
		
		//2.5注册
		UserInfoController.register(null, null, null, null, null, null, null, null, null, null, null, null);
		
		//2.6 获取品牌
		CarController.carBrand();
		
		//2.7获取车型信息
		CarController.getCarModel(null);
		
		//2.8登录
		UserInfoController.loginUser(null, null);
		
		//2.9 忘记密码
		UserInfoController.forgetPassword(null, null, null);
		
		//2.10 行驶证上传
		UploadController.uploadLicensePhoto(null, null, null);
		
		//2.11 车主认证申请
		UserInfoController.applyAuthentication(null, null, null, null, null, null, null);
	
		//2.12(已停用)
		
		//2.13 获取可提供的空座数
		ActivityController.getAvailableSeats(null, null);
		
		//2.14 活动图片上传
		UploadController.uploadCoverPhoto(null, null);
		
		//2.15 创建活动
		ActivityController.registerActivity(null);
		
		//2.16 获取热门/附近/最新活动列表
		ActivityController.getActivityList(null);
		
		//2.17 获取活动详情
		ActivityController.getActivityInfo(null, null, null);
		
		//2.18 获取活动评论
		ActivityController.getActivityComments(null, null, null, null, null);
		
		//2.19评论活动
		ActivityController.publishComment(null, null, null, null, null);
		
		//2.20 个人详情
		UserInfoController.userInfo(null, null, null);
		
		//2.21 我(TA)的发布
		UserActivityController.getUserPost(null, null, null, null, null);
		
		//2.22 我(TA)的关注
		UserActivityController.getUserSubscribe(null, null, null, null, null);
		
		//2.23 我(TA)的参与
		UserActivityController.getUserJoin(null, null, null, null, null);
		
		//2.24 关注活动
		ActivityController.subscribeActivity(null, null, null);
		
		//2.25 申请加入活动
		ActivityController.joinActivity(null, null, null, null);
				
		//2.26 获取申请列表
		MessageController.getApplicationList(null, null, null, null);
		
		//2.27 同意/拒绝 活动申请
		ActivityController.processApplication(null, null, null, null);
		
		//2.28 获取车座/成员信息
		ActivityController.getMemberAndSeatInfo(null, null, null);
		
		//2.29 立即抢座
		ActivityController.takeSeat(null, null, null, null, null);
		
		//2.30 拉下座位
		ActivityController.returnSeat(null, null, null, null);
		
		//2.31 移除成员
		ActivityController.removeMember(null, null, null, null);
		
		//2.32 退出活动
		ActivityController.quitActivity(null, null, null);
		
		//2.33 编辑活动
		ActivityController.alterActivityInfo(null, null, null, null);
		
		//2.34 我关注的人
		UserInfoController.userListen(null, null, null, null);
		
		//2.35 关注其他用户
		UserInfoController.payAttention(null, null, null);
		
		//2.36 取消关注其他用户
		UserInfoController.unPayAttention(null, null, null);
		
		//2.37 更改头像
		UploadController.reUploadUserPhoto(null, null, null);
		
		//2.38 变更我的信息
		UserInfoController.alterUserInfo(null, null, null, null, null, null, null, null);
		
		//2.39 相册图片上传
		UploadController.uploadAlbumPhoto(null, null, null);
		
		//2.40 编辑相册图片
		UserInfoController.manageAlbumPhotos(null, null, null);
		
		//2.41 获取最新消息数
		MessageController.getMessageCount(null, null);
		
		//2.42 获取消息列表
		MessageController.getMessageList(null, null, null, null, null);
		
		//2.43 上传意见反馈图片
		UploadController.uploadFeedbackPhoto(null, null);
		
		//2.44 提交反馈信息
		MessageController.submitFeedback(null, null, null, null);
		
		//2.45 取消关注活动
		ActivityController.unsubscribeActivity(null, null, null);
		
		//2.46	 获取最新版本信息
		VersionController.version(null);
		
		//2.47批量删除消息
		MessageController.removeMessages(null, null, null);
		
		//2.48 批量删除评论
		MessageController.removeComments(null, null, null);
		
		

		

		

		

		
		
		
	}
}
