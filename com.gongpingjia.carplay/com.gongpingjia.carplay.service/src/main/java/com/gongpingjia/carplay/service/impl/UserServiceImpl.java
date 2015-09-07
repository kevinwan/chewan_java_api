package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.EncoderHandler;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.dao.AlbumPhotoDao;
import com.gongpingjia.carplay.dao.AuthenticationApplicationDao;
import com.gongpingjia.carplay.dao.AuthenticationChangeHistoryDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.EmchatAccountDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.dao.UserAlbumDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.dao.UserSubscriptionDao;
import com.gongpingjia.carplay.po.AlbumPhoto;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.AuthenticationChangeHistory;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.EmchatAccount;
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

	@Autowired
	private ChatThirdPartyService chatThirdService;

	@Autowired
	private ChatCommonService chatCommonService;

	@Autowired
	private ActivitySubscriptionDao subscriptionDao;

	@Autowired
	private ActivityMemberDao memberDao;

	@Autowired
	private CacheManager cacheManager;

	@Override
	public ResponseDo register(User user) throws ApiException {
		String userId = CodeGenerator.generatorId();

		user.setId(userId);
		LOG.debug("Save register data begin");
		// 注册用户
		userDao.insert(user);

		TokenVerification tokenVerification = new TokenVerification();
		tokenVerification.setUserid(userId);
		tokenVerification.setToken(CodeGenerator.generatorId());
		tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
		tokenVerificationDao.insert(tokenVerification);

		UserAlbum userAlbum = new UserAlbum();
		userAlbum.setId(CodeGenerator.generatorId());
		userAlbum.setUserid(userId);
		userAlbum.setCreatetime(DateUtil.getTime());
		userAlbumDao.insert(userAlbum);

		EmchatAccount emchatAccount = new EmchatAccount();
		emchatAccount.setUserid(userId);
		emchatAccount.setPassword(user.getPassword());
		emchatAccount.setRegistertime(DateUtil.getTime());
		emchatAccount.setUsername(chatCommonService.getUsernameByUserid(user.getId()));

		// 注册环信用户
		LOG.debug("Register emchat user by call remote service");
		Map<String, String> chatUser = new HashMap<String, String>(2, 1);
		chatUser.put("username", emchatAccount.getUsername());
		chatUser.put("password", emchatAccount.getPassword());

		JSONObject result = chatThirdService.registerChatUser(chatCommonService.getChatToken(), chatUser);
		if (result.isEmpty()) {
			LOG.warn("Create emchat user failure");
			throw new ApiException("未能成功创建环信用户");
		}

		// 不为空说明注册成功
		emchatAccount.setActivatetime(DateUtil.getTime());
		emchatAccountDao.insert(emchatAccount);

		cacheManager.setUserTokenVerification(tokenVerification);

		Map<String, Object> data = new HashMap<String, Object>(2, 1);
		data.put("userId", userId);
		data.put("token", tokenVerification.getToken());
		data.put("photo", CommonUtil.getPhotoServer() + user.getPhoto());
		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public void checkRegisterParameters(User user, HttpServletRequest request) throws ApiException {
		LOG.debug("Begin check input parameters of register");

		// 验证参数
		if (!CommonUtil.isUUID(user.getPhoto())) {
			LOG.warn("Invalid params photo:{}", user.getPhoto());
			throw new ApiException("输入参数有误");
		}

		user.setPhoto(MessageFormat.format(Constants.PhotoKey.USER_KEY, user.getPhoto()));

		boolean phoneRegister = isPhoneRegister(request);
		boolean snsRegister = isSnsRegister(request);

		if (!phoneRegister && !snsRegister) {
			// 既不是Phone注册，也不是第三方SNS注册，需要报输入参数有误
			LOG.warn("Invalid params, it is neither phone register, nor sns register");
			throw new ApiException("输入参数有误");
		}

		checkPhoneRegister(phoneRegister, request);

		// 判断七牛上图片是否存在
		if (!photoService.isExist(user.getPhoto())) {
			LOG.warn("photo not Exist");
			throw new ApiException("注册图片未上传");
		}

		refreshUserBySnsRegister(user, snsRegister, request);
	}

	/**
	 * 根据第三方注册的信息，刷新用户信息
	 * 
	 * @param user
	 * @param snsRegister
	 * @param request
	 */
	private void refreshUserBySnsRegister(User user, boolean snsRegister, HttpServletRequest request) {
		if (snsRegister) {
			// SNS注册 刷新用户信息
			String snsChannel = request.getParameter("snsChannel");
			String uid = request.getParameter("snsUid");
			LOG.debug("Register user by sns way, snsChannel:{}", snsChannel);
			if (Constants.Channel.WECHAT.equals(snsChannel)) {
				user.setWechatid(uid);
				user.setWechatname(request.getParameter("snsUserName"));
				user.setWechatphoto(user.getPhoto());
			} else if (Constants.Channel.QQ.equals(snsChannel)) {
				user.setQqid(uid);
				user.setQqname(request.getParameter("snsUserName"));
				user.setQqphoto(user.getPhoto());
			} else if (Constants.Channel.SINA_WEIBO.equals(snsChannel)) {
				user.setSinaweiboid(uid);
				user.setSinaweiboname(request.getParameter("snsUserName"));
				user.setSinaweibophoto(user.getPhoto());
			}
			// 设置第三方登录密码
			StringBuilder builder = new StringBuilder();
			builder.append(uid);
			builder.append(snsChannel);
			builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));
			user.setPassword(EncoderHandler.encodeByMD5(builder.toString()));
		} else {
			user.setPhone(request.getParameter("phone"));
			user.setPassword(request.getParameter("password"));
		}
	}

	/**
	 * 检查用户注册的手机号和验证码是否正确
	 * 
	 * @param phoneRegister
	 *            是否为手机注册
	 * @param request
	 *            请求参数
	 * @throws ApiException
	 */
	private void checkPhoneRegister(boolean phoneRegister, HttpServletRequest request) throws ApiException {
		if (phoneRegister) {
			String phone = request.getParameter("phone");
			if (!CommonUtil.isPhoneNumber(phone)) {
				LOG.warn("Phone number:{} is not correct format", phone);
				throw new ApiException("输入参数有误");
			}

			checker.checkPhoneVerifyCode(phone, request.getParameter("code"));

			// 判断用户是否注册过
			Map<String, Object> param = new HashMap<String, Object>(1);
			param.put("phone", phone);
			List<User> users = userDao.selectByParam(param);
			if (users.size() > 0) {
				LOG.warn("Phone already registed");
				throw new ApiException("该手机号已注册");
			}
		}
	}

	/**
	 * 判断是否为手机注册
	 * 
	 * @param request
	 *            请求
	 * @return 手机注册返回true
	 */
	private boolean isPhoneRegister(HttpServletRequest request) {
		String phone = request.getParameter("phone");
		if (StringUtils.isEmpty(phone)) {
			return false;
		}

		String code = request.getParameter("code");
		if (StringUtils.isEmpty(code)) {
			return false;
		}

		String password = request.getParameter("password");
		if (StringUtils.isEmpty(password)) {
			return false;
		}

		return true;
	}

	/**
	 * 判读是否为第三方注册
	 * 
	 * @param request
	 *            请求参数
	 * @return 第三方注册，返回true
	 */
	private boolean isSnsRegister(HttpServletRequest request) {
		String snsUid = request.getParameter("snsUid");
		if (StringUtils.isEmpty(snsUid)) {
			return false;
		}

		String snsUserName = request.getParameter("snsUserName");
		if (StringUtils.isEmpty(snsUserName)) {
			return false;
		}

		String snsChannel = request.getParameter("snsChannel");
		if (StringUtils.isEmpty(snsChannel)) {
			return false;
		}

		if (!Constants.Channel.CHANNEL_LIST.contains(snsChannel)) {
			// 检查Channel是否包含在Channel——List中
			LOG.warn("Input channel:{} is not in the channel list", snsChannel);
			return false;
		}

		return true;
	}

	@Override
	public ResponseDo loginUser(User user) throws ApiException {

		Map<String, Object> data = new HashMap<String, Object>(8, 1);
		// 验证参数
		if (!CommonUtil.isPhoneNumber(user.getPhone())) {
			LOG.warn("Invalid params, phone:{}", user.getPhone());
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		// 查找用户
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put("phone", user.getPhone());
		List<User> users = userDao.selectByParam(param);
		if (users.isEmpty()) {
			LOG.warn("Fail to find user");
			return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
		}

		User userData = users.get(0);
		if (!user.getPassword().equals(userData.getPassword())) {
			LOG.warn("User password is incorrect");
			return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
		}

		data.put("userId", userData.getId());
		data.put("isAuthenticated", userData.getIsauthenticated());

		// 获取用户授权信息
		data.put("token", getUserToken(userData.getId()));

		// 查询用户车辆信息
		Car car = carDao.selectByUserId(userData.getId());
		if (null != car) {
			data.put("brand", car.getBrand());
			data.put("brandLogo",
					car.getBrandlogo() == null ? "" : PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "")
							+ car.getBrandlogo());
			data.put("model", car.getModel());
			data.put("seatNumber", car.getSeat());
		} else {
			data.put("brand", "");
			data.put("brandLogo", "");
			data.put("model", "");
			data.put("seatNumber", "");
		}

		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo forgetPassword(User user, String code) throws ApiException {
		LOG.debug("Begin reset password by forget password");

		// 验证参数
		if (!CommonUtil.isPhoneNumber(user.getPhone())) {
			LOG.warn("invalid params");
			throw new ApiException("输入参数有误");
		}

		// 验证验证码
		checker.checkPhoneVerifyCode(user.getPhone(), code);

		// 查询用户注册信息
		Map<String, Object> param = new HashMap<String, Object>(1, 1);
		param.put("phone", user.getPhone());
		List<User> users = userDao.selectByParam(param);
		if (users.isEmpty()) {
			LOG.warn("Fail to find user");
			throw new ApiException("用户不存在");
		}

		// 跟新密码
		User upUser = users.get(0);
		upUser.setPassword(user.getPassword());
		userDao.updateByPrimaryKey(upUser);

		LOG.debug("Begin reset emchat account password by forget password");
		EmchatAccount emchatAccount = emchatAccountDao.selectByPrimaryKey(upUser.getId());
		// 更新环信用户的密码
		chatThirdService.alterUserPassword(chatCommonService.getChatToken(), emchatAccount.getUsername(),
				user.getPassword());
		emchatAccount.setPassword(user.getPassword());
		emchatAccountDao.updateByPrimaryKey(emchatAccount);

		Map<String, Object> data = new HashMap<String, Object>(2, 1);
		// 获取用户授权信息
		data.put("userId", upUser.getId());
		data.put("token", getUserToken(upUser.getId()));

		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo applyAuthentication(AuthenticationApplication authen, String token, String userId) {
		LOG.debug("begin check apply authentication parameters");
		// 参数验证
		ResponseDo paramRes = applyAuthenticationParam(authen, token, userId);
		if (null != paramRes) {
			return paramRes;
		}

		User user = userDao.selectByPrimaryKey(userId);
		if (user == null) {
			LOG.warn("User not exist, userId:{}", userId);
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
		Map<String, Object> param = new HashMap<String, Object>(1, 1);
		param.put("userId", userId);
		List<AuthenticationApplication> authenticationApplications = authenticationApplicationDao.selectByParam(param);
		if (!authenticationApplications.isEmpty()) {
			LOG.warn("already applied authentication before");
			return ResponseDo.buildFailureResponse("之前的认证申请仍在处理中");
		}

		authen.setId(CodeGenerator.generatorId());
		authen.setUserid(userId);
		authen.setBrandlogo(authen.getBrandlogo().substring(authen.getBrandlogo().lastIndexOf('/') + 1));
		authen.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authen.setCreatetime(DateUtil.getTime());
		authenticationApplicationDao.insert(authen);

		AuthenticationChangeHistory authenHistory = new AuthenticationChangeHistory();
		authenHistory.setId(CodeGenerator.generatorId());
		authenHistory.setApplicationid(authen.getId());
		authenHistory.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authenHistory.setTimestamp(DateUtil.getTime());
		authenticationChangeHistoryDao.insert(authenHistory);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo getUserInfo(String interviewedUser, String visitorUser, String token) throws ApiException {
		LOG.debug("Check input parameters");
		// 验证参数
		if (!CommonUtil.isUUID(interviewedUser)) {
			LOG.warn("invalid params interviewedUser:{}", interviewedUser);
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		if (!StringUtils.isEmpty(visitorUser)) {
			// 当且仅当非空的时候校验
			checker.checkUserInfo(visitorUser, token);
		}

		// 获取活动组织者信息
		UserInfo userInfo = userDao.selectUserInfo(interviewedUser);
		if (null == userInfo) {
			LOG.warn("Fail to get organizer info of activity");
			return ResponseDo.buildFailureResponse("获取活动组织者信息失败");
		}

		Map<String, Object> data = new HashMap<String, Object>(20, 1);
		data.put("userId", userInfo.getUserId());
		data.put("nickname", userInfo.getNickname());
		data.put("gender", userInfo.getGender());
		data.put("age", userInfo.getAge());

		StringBuilder photo = new StringBuilder();
		photo.append(CommonUtil.getPhotoServer());
		photo.append(userInfo.getPhoto());
		photo.append(CommonUtil.getActivityPhotoPostfix());
		photo.append("&timestamp=");
		photo.append(DateUtil.getTime());
		data.put("photo", photo.toString());

		// 检查是否需要添加公平价服务器的前缀
		data.put("carBrandLogo", PropertiesUtil.getProperty("gongpingjia.brand.url", "") + userInfo.getCarBrandLogo());
		data.put("carModel", userInfo.getCarModel());

		data.put("drivingLicensePhoto", userInfo.getDrivingLicensePhoto());
		if (!StringUtils.isEmpty(userInfo.getDrivingLicensePhoto())) {
			StringBuilder drivingLicensePhoto = new StringBuilder();
			drivingLicensePhoto.append(CommonUtil.getPhotoServer());
			drivingLicensePhoto.append(userInfo.getDrivingLicensePhoto());
			drivingLicensePhoto.append(CommonUtil.getActivityPhotoPostfix());
			data.put("drivingLicensePhoto", drivingLicensePhoto.toString());
		}

		data.put("drivingExperience", userInfo.getDrivingExperience());
		data.put("province", userInfo.getProvince());
		data.put("city", userInfo.getCity());
		data.put("district", userInfo.getDistrict());
		data.put("isAuthenticated", userInfo.getIsauthenticated());

		data.put("label", interviewedUser.equals(visitorUser) ? Constants.UserLabel.USER_ME
				: Constants.UserLabel.USER_OTHERS);

		data.put("postNumber", activityDao.selectActivityPostNumber(interviewedUser));

		Map<String, Object> queryParam = new HashMap<String, Object>(1);
		queryParam.put("userId", interviewedUser);
		data.put("subscribeNumber", subscriptionDao.selectCountByParam(queryParam));
		data.put("joinNumber", memberDao.selectByParam(queryParam).size());

		List<AlbumPhoto> albumPhotos = albumPhotoDao.selectAlbumPhotoUrl(interviewedUser);
		List<Map<String, String>> albumPhotoViewList = new ArrayList<Map<String, String>>();
		for (AlbumPhoto albumPhoto : albumPhotos) {
			Map<String, String> albumPhotoView = new HashMap<String, String>(2, 1);
			String url = albumPhoto.getUrl();
			albumPhotoView.put("photoId", url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")));
			albumPhotoView.put("thumbnail_pic", CommonUtil.getPhotoServer() + url);

			albumPhotoViewList.add(albumPhotoView);
		}
		data.put("albumPhotos", albumPhotoViewList);

		Map<String, Object> subparam = new HashMap<String, Object>(2, 1);
		subparam.put("interviewedUser", interviewedUser);
		subparam.put("visitorUser", CommonUtil.ifNull(visitorUser, "null"));// 设置为null主要是查询返回0
		int subCount = userSubscriptionDao.subscriptionCount(subparam);

		data.put("isSubscribed", (subCount == 0) ? 0 : 1);

		return ResponseDo.buildSuccessResponse(data);
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
		Map<String, Object> listenParam = new HashMap<String, Object>(3, 1);
		listenParam.put("userId", userId);
		listenParam.put("ignore", null == ignore ? 0 : ignore);
		listenParam.put("limit", null == limit ? 10 : limit);
		List<UserInfo> userInfos = userDao.userListenList(listenParam);

		return ResponseDo.buildSuccessResponse(userInfos);
	}

	@Override
	public ResponseDo payAttention(UserSubscription userSubscription, String token) {
		LOG.debug("Pay attention to other user");
		// 验证参数
		if (!CommonUtil.isUUID(userSubscription.getTouser())
				|| userSubscription.getFromuser().equals(userSubscription.getTouser())) {
			LOG.warn("Invalid params, id format error or targetUserId equals userId error");
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
		if (user == null) {
			LOG.warn("User not exist");
			return ResponseDo.buildFailureResponse("关注用户不存在");
		}

		// 是否已关注
		UserSubscription userSub = userSubscriptionDao.selectByPrimaryKey(userSubscription);
		if (userSub != null) {
			LOG.warn("already listened to this person before");
			return ResponseDo.buildFailureResponse("已关注该用户，请勿重复关注");
		}
		userSubscription.setSubscribetime(DateUtil.getTime());
		// 关注
		userSubscriptionDao.insert(userSubscription);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo unPayAttention(UserSubscription userSubscription, String token) {
		LOG.debug("un pay attention to other user");
		// 验证参数
		if (!CommonUtil.isUUID(userSubscription.getFromuser())
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

		// 是否已关注
		UserSubscription userSub = userSubscriptionDao.selectByPrimaryKey(userSubscription);
		if (userSub == null) {
			LOG.warn("cannot unlisten as not listened before");
			return ResponseDo.buildFailureResponse("没有关注该用户，不能取消关注");
		}

		userSubscriptionDao.deleteByPrimaryKey(userSubscription);

		return ResponseDo.buildSuccessResponse("");
	}

	@Override
	public ResponseDo alterUserInfo(User user, String token) {

		try {
			checker.checkUserInfo(user.getId(), token);

			if (StringUtils.isEmpty(user.getNickname())) {
				LOG.warn("Input parameter nickname cannot be empty");
				throw new ApiException("输入参数有误");
			}

		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		// 用户是否存在
		User userDB = userDao.selectByPrimaryKey(user.getId());
		if (null == userDB) {
			LOG.warn("Invalid params, user not exist, userId:{}", user.getId());
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		LOG.debug("update system data");
		userDB.setNickname(user.getNickname());
		userDB.setGender(user.getGender());
		userDB.setProvince(user.getProvince());
		userDB.setCity(user.getCity());
		userDB.setDistrict(user.getDistrict());
		userDB.setDrivinglicenseyear(user.getDrivinglicenseyear());

		// 跟新用户信息
		userDao.updateByPrimaryKey(userDB);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo manageAlbumPhotos(String userId, String[] photos, String token) {

		// 验证参数
		if (photos.length > PropertiesUtil.getProperty("user.album.photo.max.count", 9)) {
			LOG.warn("Invalid params, photos count:{}", photos.length);
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
			if (!photoService.isExist(MessageFormat.format(Constants.PhotoKey.USER_ALBUM_KEY, userId, photo))) {
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
			albumPhoto.setUrl(MessageFormat.format(Constants.PhotoKey.USER_ALBUM_KEY, userId, photo));
			albumPhotoDao.insert(albumPhoto);
		}

		return ResponseDo.buildSuccessResponse(photos);
	}

	private String getUserToken(String userId) throws ApiException {
		TokenVerification tokenVerification = cacheManager.getUserTokenVerification(userId);
		if (null == tokenVerification) {
			LOG.warn("Fail to get token and expire info from token_verification");
			throw new ApiException("获取用户授权信息失败");
		}

		// 如果过期 跟新Token
		if (tokenVerification.getExpire() > DateUtil.getTime()) {
			return tokenVerification.getToken();
		}

		String uuid = CodeGenerator.generatorId();
		tokenVerification.setToken(uuid);
		tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
				PropertiesUtil.getProperty("carplay.token.over.date", 7)));
		tokenVerificationDao.updateByPrimaryKey(tokenVerification);

		cacheManager.setUserTokenVerification(tokenVerification);

		return uuid;
	}

	private ResponseDo applyAuthenticationParam(AuthenticationApplication authen, String token, String userId) {
		// 验证参数
		if ((authen.getDrivingexperience() > PropertiesUtil.getProperty("user.max.driving.experience.years", 50))) {
			LOG.warn("Invalid params driving experience:{}", authen.getDrivingexperience());
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		if ((authen.getBrandlogo().indexOf("http://") < 0) || authen.getBrandlogo().lastIndexOf("/") <= 6) {
			LOG.warn("Invalid params brandlogo:{}", authen.getBrandlogo());
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		try {
			checker.checkUserInfo(userId, token);
		} catch (ApiException e) {
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
		return null;
	}

	@Override
	public ResponseDo snsLogin(String uid, String channel, String sign, String username, String url)
			throws ApiException {
		checkSnsLoginParameters(uid, channel, sign);

		LOG.debug("Save data begin");
		Map<String, Object> param = new HashMap<String, Object>(1);
		param.put(channel + "Id", uid);
		List<User> users = userDao.selectByParam(param);

		if (users.isEmpty()) {
			// 没有找到对应的已经存在的用户，注册新用户, 由客户端调用注册接口，这里只完成图片上传
			LOG.debug("No exist user in the system, register new user by client call register interface");

			// 用户ID
			String id = CodeGenerator.generatorId();
			String key = MessageFormat.format(Constants.PhotoKey.USER_KEY, id);

			uploadPhotoToServer(url, key);

			Map<String, String> data = new HashMap<String, String>(5, 1);
			data.put("snsUid", uid);
			data.put("snsUserName", username);
			data.put("snsChannel", channel);
			data.put("photoUrl", CommonUtil.getPhotoServer() + key);
			data.put("photoId", id);

			return ResponseDo.buildSuccessResponse(data);
		} else {
			// 用户已经存在于系统中
			LOG.debug("User is exist in the system, return login infor");
			User user = users.get(0);

			TokenVerification token = cacheManager.getUserTokenVerification(user.getId());

			Map<String, Object> data = new HashMap<String, Object>(9, 1);
			data.put("userId", user.getId());
			data.put("token", token.getToken());
			data.put("isAuthenticated", 0);
			data.put("nickname", user.getNickname());
			data.put("photo",
					CommonUtil.getPhotoServer() + MessageFormat.format(Constants.PhotoKey.USER_KEY, user.getId()));

			Car car = carDao.selectByUserId(user.getId());
			if (car != null) {
				data.put("brand", CommonUtil.ifNull(car.getBrand(), ""));
				data.put("brandLogo", CommonUtil.getGPJImagePrefix() + CommonUtil.ifNull(car.getBrandlogo(), ""));
				data.put("model", CommonUtil.ifNull(car.getModel(), ""));
				data.put("seatNumber", car.getSeat());
			} else {
				data.put("brand", "");
				data.put("brandLogo", "");
				data.put("model", "");
				data.put("seatNumber", 0);
			}
			return ResponseDo.buildSuccessResponse(data);
		}
	}

	/**
	 * 第三方登录，上传图片到七牛服务器
	 * 
	 * @param url
	 *            请求URL
	 * @param key
	 *            图片 Key值
	 * @throws ApiException
	 *             业务异常
	 */
	private void uploadPhotoToServer(String url, String key) throws ApiException {
		LOG.debug("Download user photo from internet, url:{}", url);
		if (StringUtils.isEmpty(url)) {
			LOG.warn("Failed to obtain user photo from server");
			throw new ApiException("未能从三方登录获取头像信息");
		}

		CloseableHttpResponse response = HttpClientUtil.get(url, new HashMap<String, String>(0), new ArrayList<Header>(
				0), Constants.Charset.UTF8);
		if (!HttpClientUtil.isStatusOK(response)) {
			LOG.warn("Failed to obtain user photo from server");
			HttpClientUtil.close(response);
			throw new ApiException("未能从三方登录获取头像信息");
		}

		byte[] imageBytes = HttpClientUtil.parseResponseGetBytes(response);
		HttpClientUtil.close(response);

		LOG.debug("Upload photo to photo server");
		PhotoService photoService = BeanUtil.getBean(PhotoService.class);
		Map<String, String> uploadResult = photoService.upload(imageBytes, key, true);
		if (!Constants.Result.SUCCESS.equals(uploadResult.get("result"))) {
			// 上传失败了
			LOG.warn("Failed to upload photo to the server");
			throw new ApiException("未能成功上传图像");
		}
	}

	private void checkSnsLoginParameters(String uid, String channel, String sign) throws ApiException {
		LOG.debug("Check input parameters");

		if (!Constants.Channel.CHANNEL_LIST.contains(channel)) {
			// Channel不在范围内
			LOG.warn("Input channel is not in the list, input channel:{}", channel);
			throw new ApiException("输入参数有误");
		}

		if (!isPassSignCheck(uid, channel, sign)) {
			// 检查sign不通过
			LOG.warn("Input sign correct");
			throw new ApiException("输入参数有误");
		}
	}

	private boolean isPassSignCheck(String uid, String channel, String sign) {
		StringBuilder builder = new StringBuilder();
		builder.append(uid);
		builder.append(channel);
		builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));

		String pass = EncoderHandler.encodeByMD5(builder.toString());
		if (pass.equals(sign)) {
			return true;
		}
		return false;
	}
}
