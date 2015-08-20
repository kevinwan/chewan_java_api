package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.EncoderHandler;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.common.util.ToolsUtils;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.AlbumPhotoDao;
import com.gongpingjia.carplay.dao.AuthenticationApplicationDao;
import com.gongpingjia.carplay.dao.AuthenticationChangeHistoryDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.EmchatAccountDao;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.dao.UserAlbumDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.dao.UserSubscriptionDao;
import com.gongpingjia.carplay.po.AlbumPhoto;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.AuthenticationChangeHistory;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.EmchatAccount;
import com.gongpingjia.carplay.po.PhoneVerification;
import com.gongpingjia.carplay.po.TokenVerification;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserAlbum;
import com.gongpingjia.carplay.po.UserInfo;
import com.gongpingjia.carplay.po.UserSubscription;
import com.gongpingjia.carplay.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private PhoneVerificationDao phoneVerificationDao;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenVerificationDao tokenVerificationDao;

	@Autowired
	private EmchatAccountDao emchatAccountDao;

	@Autowired
	private UserAlbumDao userAlbumDao;

	@Autowired
	private CarDao carDao;

	@Autowired
	private AuthenticationApplicationDao authenticationApplicationDao;

	@Autowired
	private AuthenticationChangeHistoryDao authenticationChangeHistoryDao;

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private AlbumPhotoDao albumPhotoDao;

	@Autowired
	private UserSubscriptionDao userSubscriptionDao;

	@Autowired
	private ParameterChecker checker;

	@Override
	public ResponseDo register(User user, String code) {
		LOG.debug("Begin check input parameters of register");
		// 验证参数
		if (!CommonUtil.isPhoneNumber(user.getPhone()) || !CommonUtil.isUUID(user.getPhoto())) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}
		user.setPhoto(MessageFormat.format(Constants.USER_PHOTO_KEY, user.getPhoto()));

		// 验证验证码
		ResponseDo failureResponse = checkPhoneVerification(user.getPhone(), code);
		if (null != failureResponse) {
			return failureResponse;
		}

		// 判断七牛上图片是否存在
		if (!photoService.isExist(user.getPhoto())) {
			LOG.warn("photo not Exist");
			return ResponseDo.buildFailureResponse("注册图片未上传");
		}

		// 判断用户是否注册过
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", user.getPhone());
		List<User> users = userDao.selectByParam(param);
		if (users.size() > 0) {
			LOG.warn("Phone already registed");
			return ResponseDo.buildFailureResponse("该用户已注册");
		}
		String userId = user.getId();

		LOG.debug("save data");
		// 注册用户
		userDao.insert(user);

		TokenVerification tokenVerification = new TokenVerification();
		tokenVerification.setUserid(userId);
		tokenVerification.setToken(CodeGenerator.generatorId());
		tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
		tokenVerificationDao.insert(tokenVerification);

		EmchatAccount emchatAccount = new EmchatAccount();
		emchatAccount.setUserid(userId);
		emchatAccount.setPassword(user.getPassword());
		emchatAccount.setRegistertime(DateUtil.getTime());
		emchatAccount.setUsername(EncoderHandler.encodeByMD5(user.getId()));
		emchatAccountDao.insert(emchatAccount);

		UserAlbum userAlbum = new UserAlbum();
		userAlbum.setId(CodeGenerator.generatorId());
		userAlbum.setUserid(userId);
		userAlbum.setCreatetime(DateUtil.getTime());
		userAlbumDao.insert(userAlbum);

		Map<String, Object> data = new HashMap<String, Object>(2, 1);
		data.put("userId", userId);
		data.put("token", tokenVerification.getToken());
		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo loginUser(User user) {

		Map<String, Object> data = new HashMap<String, Object>();
		// 验证参数
		if (!CommonUtil.isPhoneNumber(user.getPhone())) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		// 查找用户
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", user.getPhone());
		List<User> users = userDao.selectByParam(param);
		if (users.isEmpty()) {
			LOG.warn("Fail to find user");
			return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
		}

		User userData = users.get(0);
		if (!user.getPassword().equals(userData.getPassword())) {
			LOG.warn("Fail to find user");
			return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
		}

		data.put("userId", userData.getId());
		data.put("isAuthenticated", userData.getIsauthenticated());

		// 获取用户授权信息
		ResponseDo tokenResponseDo = getUserToken(userData.getId());
		if (tokenResponseDo.isFailure()) {
			return tokenResponseDo;
		}
		data.put("token", tokenResponseDo.getData());

		// 查询用户车辆信息
		Car car = carDao.selectByUserId(userData.getId());
		if (null != car) {
			data.put("brand", car.getBrand());
			data.put("brandLogo",
					car.getBrandlogo() == null ? "" : PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "")
							+ car.getBrandlogo());
			data.put("model", car.getModel());
			data.put("seatNumber", car.getSeat());
		}

		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo forgetPassword(User user, String code) {
		Map<String, Object> data = new HashMap<String, Object>();
		// 验证参数
		if (!ToolsUtils.isPhoneNumber(user.getPhone())) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		// 验证验证码
		ResponseDo failureResponse = checkPhoneVerification(user.getPhone(), code);
		if (null != failureResponse) {
			return failureResponse;
		}

		// 查询用户注册信息
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phone", user.getPhone());
		List<User> users = userDao.selectByParam(param);
		if (null == users || users.size() < 1) {
			LOG.warn("Fail to find user");
			return ResponseDo.buildFailureResponse("用户不存在");
		}

		// 跟新密码
		User upUser = users.get(0);
		upUser.setPassword(user.getPassword());
		if (0 == userDao.updateByPrimaryKey(upUser)) {
			LOG.warn("Fail to update password");
			return ResponseDo.buildFailureResponse("更新密码失败");
		}

		// 获取用户授权信息
		ResponseDo tokenResponseDo = getUserToken(upUser.getId());
		if (tokenResponseDo.isFailure()) {
			return tokenResponseDo;
		}
		data.put("userId", upUser.getId());
		data.put("token", tokenResponseDo.getData());

		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo applyAuthentication(AuthenticationApplication authen, String token, String userId) {

		// 参数验证
		ResponseDo paramRes = applyAuthenticationParam(authen, token, userId);
		if (null != paramRes) {
			return paramRes;
		}

		User user = userDao.selectByPrimaryKey(userId);
		if (null == user) {
			LOG.warn("User not exist");
			return ResponseDo.buildFailureResponse("用户不存在");
		}
		if (user.getIsauthenticated() != null && 0 != user.getIsauthenticated()) {
			LOG.warn("User already authenticated");
			return ResponseDo.buildFailureResponse("用户已认证，请勿重复认证");
		}

		// 判断七牛上图片是否存在
		if (!photoService.isExist(user.getPhoto())) {
			LOG.warn("photo not Exist");
			return ResponseDo.buildFailureResponse("注册图片未上传");
		}

		// 是否已经提起认证处理
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		List<AuthenticationApplication> authenticationApplications = authenticationApplicationDao.selectByParam(param);
		if (null != authenticationApplications && authenticationApplications.size() > 0) {
			LOG.warn("already applied authentication");
			return ResponseDo.buildFailureResponse("之前的认证申请仍在处理中");
		}

		authen.setId(CodeGenerator.generatorId());
		authen.setUserid(userId);
		authen.setBrandlogo(authen.getBrandlogo().substring(authen.getBrandlogo().lastIndexOf('/') + 1));
		authen.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authen.setCreatetime(DateUtil.getTime());
		if (0 == authenticationApplicationDao.insert(authen)) {
			LOG.warn("Fail to insert into authentication_application table");
			return ResponseDo.buildFailureResponse("未能成功申请认证");
		}
		AuthenticationChangeHistory authenHistory = new AuthenticationChangeHistory();
		authenHistory.setId(CodeGenerator.generatorId());
		authenHistory.setApplicationid(authen.getId());
		authenHistory.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authenHistory.setTimestamp(DateUtil.getTime());
		if (0 == authenticationChangeHistoryDao.insert(authenHistory)) {
			LOG.warn("Fail to insert into authentication_change_history table");
			return ResponseDo.buildFailureResponse("添加认证申请历史记录失败");
		}

		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo userInfo(String interviewedUser, String visitorUser, String token) {

		Map<String, Object> param = new HashMap<String, Object>();
		// 验证参数
		if (!CommonUtil.isUUID(interviewedUser) || !CommonUtil.isUUID(visitorUser) || !CommonUtil.isUUID(token)) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		// 获取活动组织者信息
		UserInfo userInfo = userDao.userInfo(interviewedUser);
		if (null == userInfo) {
			LOG.warn("Fail to get organizer info of activity");
			return ResponseDo.buildFailureResponse("获取活动组织者信息失败");
		}

		param.put("userId", userInfo.getUserId());
		param.put("nickname", userInfo.getNickname());
		param.put("gender", userInfo.getGender());
		param.put("age", userInfo.getAge());
		param.put("photo", PropertiesUtil.getProperty("gongpingjia.person.pic.url", "") + userInfo.getPhoto()
				+ CommonUtil.getActivityPhotoPostfix() + "&timestamp=" + DateUtil.getTime());
		param.put("carBrandLogo", userInfo.getCarBrandLogo());
		param.put("carModel", userInfo.getCarModel());
		param.put("drivingExperience", userInfo.getDrivingExperience());
		param.put("province", userInfo.getProvince());
		param.put("city", userInfo.getCity());
		param.put("district", userInfo.getDistrict());
		param.put("isAuthenticated", userInfo.getIsauthenticated());

		param.put("label", interviewedUser == visitorUser ? Constants.UserLabel.USER_ME
				: Constants.UserLabel.USER_OTHERS);

		param.put("postNumber", activityDao.activityPostNumber(interviewedUser));

		List<AlbumPhoto> albumPhotos = albumPhotoDao.selectAlbumPhotoUrl(interviewedUser);
		List<AlbumPhotoView> albumPhotoViewList = new ArrayList<UserServiceImpl.AlbumPhotoView>();
		if (null != albumPhotos && !albumPhotos.isEmpty()) {
			for (AlbumPhoto albumPhoto : albumPhotos) {
				AlbumPhotoView albumPhotoView = new AlbumPhotoView();
				String url = albumPhoto.getUrl();
				int idxStart = url.indexOf("/album/");
				albumPhotoView.setPhotoId(url.substring(idxStart + 7, idxStart + 43));
				albumPhotoView.setThumbnail_pic(url);
			}
		}
		param.put("albumPhotos", albumPhotoViewList);

		Map<String, Object> subparam = new HashMap<String, Object>();
		subparam.put("interviewedUser", interviewedUser);
		subparam.put("visitorUser", visitorUser);
		int subCount = userSubscriptionDao.subscriptionCount(subparam);

		param.put("isSubscribed", (subCount == 0) ? 0 : 1);

		return ResponseDo.buildSuccessResponse(param);
	}

	@Override
	public ResponseDo userListen(String userId, Integer ignore, Integer limit, String token) {

		try {
			checker.checkUserInfo(userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		// 查询关注我的人
		Map<String, Object> listenParam = new HashMap<String, Object>();
		listenParam.put("userId", userId);
		listenParam.put("ignore", null == ignore ? 0 : ignore);
		listenParam.put("limit", null == limit ? 10 : limit);
		List<UserInfo> userInfos = userDao.userListenList(listenParam);

		return ResponseDo.buildSuccessResponse(userInfos);
	}

	@Override
	public ResponseDo payAttention(UserSubscription userSubscription, String token) {
		// 验证参数
		if (!CommonUtil.isUUID(userSubscription.getTouser())
				|| userSubscription.getFromuser().equals(userSubscription.getTouser())) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userSubscription.getFromuser(), token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		// 关注用户是否存在
		User user = userDao.selectByPrimaryKey(userSubscription.getTouser());
		if (null == user) {
			LOG.warn("User not exist");
			return ResponseDo.buildFailureResponse("关注用户不存在");
		}

		// 是否已关注
		UserSubscription userSub = userSubscriptionDao.selectByPrimaryKey(userSubscription);
		if (null != userSub) {
			LOG.warn("already listened to this person before");
			return ResponseDo.buildFailureResponse("已关注该用户，请勿重复关注");
		}
		userSubscription.setSubscribetime(DateUtil.getTime());
		// 关注
		if (0 == userSubscriptionDao.insert(userSubscription)) {
			LOG.warn("fail to listen to this persion");
			return ResponseDo.buildFailureResponse("未能成功关注该用户");
		}
		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo unPayAttention(UserSubscription userSubscription, String token) {
		// 验证参数
		if (!CommonUtil.isUUID(userSubscription.getFromuser()) || !CommonUtil.isUUID(token)
				|| !CommonUtil.isUUID(userSubscription.getTouser())
				|| userSubscription.getFromuser().equals(userSubscription.getTouser())) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		// 是否已关注
		UserSubscription userSub = userSubscriptionDao.selectByPrimaryKey(userSubscription);
		if (null == userSub) {
			LOG.warn("cannot unlisten as not listened before");
			return ResponseDo.buildFailureResponse("没有关注该用户，不能取消关注");
		}

		if (0 == userSubscriptionDao.deleteByPrimaryKey(userSubscription)) {
			LOG.warn("fail to listen to this persion");
			return ResponseDo.buildFailureResponse("未能成功取消对该用户的关注");
		}

		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo alterUserInfo(User user, String token) {

		try {
			checker.checkUserInfo(user.getId(), token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		// 用户是否存在
		User userDB = userDao.selectByPrimaryKey(user.getId());
		if (null == userDB) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		userDB.setNickname(user.getNickname());
		userDB.setGender(user.getGender());
		userDB.setProvince(user.getProvince());
		userDB.setCity(user.getCity());
		userDB.setDistrict(user.getDistrict());
		userDB.setDrivinglicenseyear(user.getDrivinglicenseyear());

		// 跟新用户信息
		if (0 == userDao.updateByPrimaryKey(userDB)) {
			LOG.warn("Fail to update user info");
			return ResponseDo.buildFailureResponse("未能成功更新用户信息");
		}
		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo manageAlbumPhotos(String userId, String[] photos, String token) {

		// 验证参数
		if (photos.length > 9) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userId, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		// 获取用户相册
		List<UserAlbum> userAlbums = userAlbumDao.selectListByUserId(userId);
		if (null == userAlbums || userAlbums.isEmpty()) {
			LOG.warn("Fail to get user album");
			return ResponseDo.buildFailureResponse("未能成功获取用户相册");
		}
		String albumId = userAlbums.get(0).getId();
		// 判断七牛上图片是否存在
		for (String photo : photos) {
			if (!photoService.isExist(MessageFormat.format(Constants.USER_ALBUM_PHOTO_KEY, userId, photo))) {
				LOG.warn("photo not Exist");
				return ResponseDo.buildFailureResponse("注册图片未上传");
			}
		}

		// 相册先删除再添加
		albumPhotoDao.deleteByPrimaryKey(albumId);
		for (String photo : photos) {
			AlbumPhoto albumPhoto = new AlbumPhoto();
			albumPhoto.setId(photo);
			albumPhoto.setAlbumid(albumId);
			albumPhoto.setUploadtime(DateUtil.getTime());
			albumPhoto.setUrl(MessageFormat.format(Constants.USER_ALBUM_PHOTO_KEY, userId, photo));
			albumPhotoDao.insert(albumPhoto);
		}

		return ResponseDo.buildSuccessResponse(photos);
	}

	private ResponseDo checkPhoneVerification(String phone, String code) {

		// 验证验证码
		PhoneVerification phoneVerification = phoneVerificationDao.selectByPrimaryKey(phone);
		if (null == phoneVerification) {
			LOG.warn("Cannot find code of this phone :" + phone);
			return ResponseDo.buildFailureResponse("未能获取该手机的验证码");
		} else if (!code.equals(phoneVerification.getCode())) {
			LOG.warn("Code not correct,phone : " + phone + ". error code :" + code);
			return ResponseDo.buildFailureResponse("验证码错误");
		} else if (phoneVerification.getExpire() < DateUtil.getTime()) {
			LOG.warn("Code expired");
			return ResponseDo.buildFailureResponse("该验证码已过期，请重新获取验证码");
		}
		return null;
	}

	private ResponseDo getUserToken(String userId) {
		TokenVerification tokenVerification = tokenVerificationDao.selectByPrimaryKey(userId);
		if (null == tokenVerification) {
			LOG.warn("Fail to get token and expire info from token_verification");
			return ResponseDo.buildFailureResponse("获取用户授权信息失败");
		}

		// 如果过期 跟新Token
		if (tokenVerification.getExpire() > DateUtil.getTime()) {
			return ResponseDo.buildSuccessResponse(tokenVerification.getToken());
		}

		String uuid = CodeGenerator.generatorId();
		tokenVerification.setToken(uuid);
		tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
				PropertiesUtil.getProperty("gongpingjia.token.over.date", 7)));
		if (0 == tokenVerificationDao.updateByPrimaryKey(tokenVerification)) {
			LOG.warn("Fail to update new token and expire info");
			return ResponseDo.buildFailureResponse("更新用户授权信息失败");
		}
		return ResponseDo.buildSuccessResponse(uuid);
	}

	private ResponseDo applyAuthenticationParam(AuthenticationApplication authen, String token, String userId) {
		// 验证参数
		if ((authen.getDrivingexperience() > 50) || (authen.getBrandlogo().indexOf("http://") < 0)
				|| authen.getBrandlogo().lastIndexOf("/") <= 6) {
			LOG.warn("invalid params");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userId, token);
		} catch (ApiException e) {
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
		return null;
	}

	class AlbumPhotoView {

		private String photoId;

		private String thumbnail_pic;

		public String getPhotoId() {
			return photoId;
		}

		public void setPhotoId(String photoId) {
			this.photoId = photoId;
		}

		public String getThumbnail_pic() {
			return thumbnail_pic;
		}

		public void setThumbnail_pic(String thumbnail_pic) {
			this.thumbnail_pic = thumbnail_pic;
		}

	}

}
