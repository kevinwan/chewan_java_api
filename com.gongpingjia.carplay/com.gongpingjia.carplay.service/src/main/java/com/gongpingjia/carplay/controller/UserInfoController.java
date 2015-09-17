package com.gongpingjia.carplay.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.TypeConverUtil;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserSubscription;
import com.gongpingjia.carplay.service.UserService;

@RestController
public class UserInfoController {

	private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);

	@Autowired
	private UserService userService;

	/**
	 * 2.5注册
	 * 
	 * @param phone
	 *            手机号
	 * @param code
	 *            验证码
	 * @param password
	 *            密码 (6-16位字符的 MD5 加密)
	 * @param nickname
	 *            昵称
	 * @param gender
	 *            性别： “男” 或 “女”
	 * @param birthYear
	 *            出生年
	 * @param birthMonth
	 *            出生月
	 * @param birthDay
	 *            出生日
	 * @param province
	 *            省份名，例如 “江苏”
	 * @param city
	 *            城市名，例如 “南京”
	 * @param district
	 *            区域名，例如 “栖霞区”
	 * @param photo
	 *            该 photo的uuid
	 * @return 注册结果
	 */
	@RequestMapping(value = "/user/register", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo register(@RequestBody JSONObject json) {

		LOG.debug("register is called, request parameter produce:");
		try {
			// 检查必须参数是否为空
			if (CommonUtil.isEmpty(json, Arrays.asList("nickname", "gender", "province", "city", "district", "photo"))) {
				throw new ApiException("输入参数错误");
			}

			User user = new User();
			user.setId(json.getString("photo"));
			user.setNickname(json.getString("nickname"));
			user.setGender(json.getString("gender"));
			user.setBirthyear(json.getInt("birthYear"));
			user.setBirthmonth(json.getInt("birthMonth"));
			user.setBirthday(json.getInt("birthDay"));
			user.setProvince(json.getString("province"));
			user.setCity(json.getString("city"));
			user.setDistrict(json.getString("district"));
			user.setPhoto(json.getString("photo"));
			user.setRole(Constants.UserCatalog.COMMON);

			userService.checkRegisterParameters(user, json);

			return userService.register(user);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.8登录
	 * 
	 * @param phone
	 *            手机号
	 * @param password
	 *            密码
	 * @return 登陆结果
	 */
	@RequestMapping(value = "/user/login", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo loginUser(@RequestBody User user) {
		LOG.debug("login is called, request parameter produce:");

		try {
			if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPhone())) {
				LOG.warn("Input parameters password or phone is empty");
				throw new ApiException("输入参数有误");
			}

			return userService.loginUser(user);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.9 忘记密码
	 * 
	 * @param phone
	 *            手机号
	 * @param code
	 *            验证码
	 * @param password
	 *            密码 (MD5加密后的值)
	 * @return 忘记密码结果
	 */
	@RequestMapping(value = "/user/password", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo forgetPassword(@RequestBody JSONObject json) {

		LOG.debug("forgetPassword is called, request parameter produce:");

		try {
			if (CommonUtil.isEmpty(json, Arrays.asList("phone", "code", "password"))) {
				throw new ApiException("输入参数有误");
			}

			User user = new User();
			user.setPhone(json.getString("phone"));
			user.setPassword(json.getString("password"));

			return userService.forgetPassword(user, json.getString("code"));
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.11 车主认证申请
	 * 
	 * @param token
	 *            token
	 * @param drivingExperience
	 *            驾龄， 例如 2 代表两年驾龄
	 * @param carBrand
	 *            车辆品牌中文名称，例如 “奥迪”
	 * @param carBrandLogo
	 *            车辆品牌Logo地址
	 * @param carModel
	 *            车型， 例如 “奥迪A4L”
	 * @param slug
	 *            车型API返回结果的slug字段,例如 dazhong-cc
	 * @param userId
	 *            用户uuid
	 * @return 认证结果
	 */
	@RequestMapping(value = "/user/{userId}/authentication", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo applyAuthentication(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "token") String token, @RequestBody JSONObject json) {
		LOG.debug("applyAuthentication is called, request parameter produce:");

		if (CommonUtil
				.isEmpty(json, Arrays.asList("drivingExperience", "carBrand", "carBrandLogo", "carModel", "slug"))) {
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		Integer drivingExperience = 0;
		try {
			drivingExperience = TypeConverUtil.convertToInteger("drivingExperience",
					json.getString("drivingExperience"), true);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}

		AuthenticationApplication authenticationApplication = new AuthenticationApplication();
		authenticationApplication.setDrivingexperience(drivingExperience);
		authenticationApplication.setBrand(json.getString("carBrand"));
		authenticationApplication.setBrandlogo(json.getString("carBrandLogo"));
		authenticationApplication.setModel(json.getString("carModel"));
		authenticationApplication.setSlug(json.getString("slug"));

		return userService.applyAuthentication(authenticationApplication, token, userId);
	}

	/**
	 * 2.20 个人详情
	 * 
	 * @param interviewedUser
	 *            被访问用户的userId
	 * @param visitorUser
	 *            访问者的userId
	 * @param token
	 *            token
	 * @return 个人详情返回结果
	 */
	@RequestMapping(value = "/user/{interviewedUser}/info", method = RequestMethod.GET)
	public ResponseDo getUserInfo(@PathVariable(value = "interviewedUser") String interviewedUser,
			@RequestParam(value = "userId", required = false) String visitorUser,
			@RequestParam(value = "token", required = false) String token) {

		LOG.debug("getUserInfo is called, interviewedUser:{}, vistorUser:{}", interviewedUser, visitorUser);

		try {
			return userService.getUserInfo(interviewedUser, visitorUser, token);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.34 我关注的人
	 * 
	 * @param userId
	 *            用户uuid
	 * @param ignore
	 *            返回结果将扔掉的条数, 不填默认为 0
	 * @param limit
	 *            返回的条数, 默认为 10
	 * @param token
	 *            token
	 * @return 我关注的人返回结果
	 */
	@RequestMapping(value = "/user/{userId}/listen", method = RequestMethod.GET)
	public ResponseDo userListen(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "ignore", required = false) Integer ignore,
			@RequestParam(value = "limit", required = false) Integer limit, @RequestParam(value = "token") String token) {

		LOG.debug("userListen is called, request parameter produce:");

		return userService.userListen(userId, ignore, limit, token);
	}

	/**
	 * 2.35 关注其他用户
	 * 
	 * @param userId
	 *            用户uuid
	 * @param targetUserId
	 *            要关注的用户id
	 * @param token
	 *            token
	 * @return 关注其他用户返回结果
	 */
	@RequestMapping(value = "/user/{userId}/listen", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo payAttention(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "token") String token, @RequestBody JSONObject json) {

		LOG.debug("userListen is called, request parameter produce:");

		if (CommonUtil.isEmpty(json, "targetUserId")) {
			LOG.warn("Input parameter targetUserId is empty");
			return ResponseDo.buildFailureResponse("输入参数错误");
		}

		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setFromuser(userId);
		userSubscription.setTouser(json.getString("targetUserId"));

		return userService.payAttention(userSubscription, token);
	}

	/**
	 * 2.36 取消关注其他用户
	 * 
	 * @param userId
	 *            用户uuid
	 * @param targetUserId
	 *            要取消关注的用户id
	 * @param token
	 *            token
	 * @return 取消关注其他用户返回结果
	 */
	@RequestMapping(value = "/user/{userId}/unlisten", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo unPayAttention(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "token") String token, @RequestBody JSONObject json) {

		LOG.debug("userListen is called, request parameter produce:");

		if (CommonUtil.isEmpty(json, "targetUserId")) {
			LOG.warn("Input parameter targetUserId is empty");
			return ResponseDo.buildFailureResponse("输入参数错误");
		}

		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setFromuser(userId);
		userSubscription.setTouser(json.getString("targetUserId"));

		return userService.unPayAttention(userSubscription, token);
	}

	/**
	 * 2.38 变更我的信息
	 * 
	 * @param userId
	 *            用户uuid
	 * @param nickname
	 *            用户昵称
	 * @param gender
	 *            性别 ， 男 或 女
	 * @param drivingExperience
	 *            驾龄， 例如 2 代表两年驾龄
	 * @param province
	 *            省份
	 * @param city
	 *            城市
	 * @param district
	 *            区名
	 * @param token
	 *            token
	 * @return 变更我的信息返回结果
	 */
	@RequestMapping(value = "/user/{userId}/info", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo alterUserInfo(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "token") String token, @RequestBody JSONObject json) {

		LOG.debug("alterUserInfo is called, request parameter produce:");

		User user = new User();
		user.setId(userId);
		if (!CommonUtil.isEmpty(json, "nickname")) {
			user.setNickname(json.getString("nickname"));
		}

		if (!CommonUtil.isEmpty(json, "gender")) {
			user.setGender(json.getString("gender"));
		}

		if (!CommonUtil.isEmpty(json, "province")) {
			user.setProvince(json.getString("province"));
		}

		if (!CommonUtil.isEmpty(json, "city")) {
			user.setCity(json.getString("city"));
		}

		if (!CommonUtil.isEmpty(json, "district")) {
			user.setDistrict(json.getString("district"));
		}

		if (!CommonUtil.isEmpty(json, "drivingExperience")) {

			Integer drivingExperience = 0;
			try {
				drivingExperience = TypeConverUtil.convertToInteger("drivingExperience",
						json.getString("drivingExperience"), false);
			} catch (ApiException e) {
				LOG.warn("Input parameter drivingExperience is not number format");
				return ResponseDo.buildFailureResponse("输入参数有误");
			}

			user.setDrivinglicenseyear(DateUtil.getValue(DateUtil.getDate(), Calendar.YEAR) - drivingExperience);
		} else {
			user.setDrivinglicenseyear(0);
		}

		return userService.alterUserInfo(user, token);
	}

	/**
	 * 2.40 编辑相册图片
	 * 
	 * @param userId
	 *            用户uuid
	 * @param photos
	 *            相册图片id 数组，长度最大为9
	 * @param token
	 *            token
	 * @return 编辑相册图片返回结果
	 */
	@RequestMapping(value = "/user/{userId}/album/photos", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo manageAlbumPhotos(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "token") String token, @RequestBody JSONObject json) {

		LOG.debug("manageAlbumPhotos is called, request parameter produced");

		if (!CommonUtil.isArrayEmpty(json, "photos")) {
			LOG.warn("Input parameters photos is empty");
			return ResponseDo.buildFailureResponse("输入参数有误");
		}

		String[] photos = CommonUtil.getStringArray(json.getJSONArray("photos"));

		return userService.manageAlbumPhotos(userId, photos, token);
	}

	/**
	 * 2.49 三方登录
	 * 
	 * @param uid
	 *            三方登录返回的用户唯一标识
	 * @param channel
	 *            wechat 、qq 或 sinaWeibo
	 * @param sign
	 *            API签名，计算方法为 MD5(uid + channel + BundleID) 其中，BundleID 为
	 *            com.gongpingjia.carplay
	 * 
	 * @param username
	 *            三方登录返回的用户昵称
	 * @param url
	 *            三方登录返回的用户头像地址
	 * @return 返回登录结果
	 */
	@RequestMapping(value = "/sns/login", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo snsLogin(@RequestBody JSONObject json) {
		LOG.info("snsLogin begin");

		try {
			if (CommonUtil.isEmpty(json, Arrays.asList("uid", "channel", "sign"))) {
				throw new ApiException("输入参数有误");
			}

			String username = null;
			if (!CommonUtil.isEmpty(json, "username")) {
				username = json.getString("username");
			}

			String url = null;
			if (!CommonUtil.isEmpty(json, "url")) {
				url = json.getString("url");
			}

			return userService.snsLogin(json.getString("uid"), json.getString("channel"), json.getString("sign"),
					username, url);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.53 变更用户位置信息
	 * 
	 * @param json
	 *            请求Body参数信息
	 * @return 变更结果信息
	 */
	@RequestMapping(value = "/user/location", method = RequestMethod.POST, headers = {
			"Accept=application/json; charset=UTF-8", "Content-Type=application/json" })
	public ResponseDo changeLocation(@RequestBody JSONObject json) {
		try {
			LOG.debug("Begin change user loaction, request:{}", json);
			if (CommonUtil.isEmpty(json, Arrays.asList("deviceToken", "longitude", "latitude"))) {
				throw new ApiException("输入参数有误");
			}

			return userService.changeLocation(json);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 获取周边用户信息
	 * 
	 * @param minLongitude
	 *            经度下限
	 * @param maxLongitude
	 *            经度上限
	 * @param minLatitude
	 *            纬度下限
	 * @param maxLatitude
	 *            纬度上限
	 * @param gender
	 *            性别
	 * @return 返回
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public ResponseDo userList(@RequestParam("minLongitude") Double minLongitude,
			@RequestParam("maxLongitude") Double maxLongitude, @RequestParam("minLatitude") Double minLatitude,
			@RequestParam("maxLatitude") Double maxLatitude,
			@RequestParam(value = "gender", required = false) String gender) {

		LOG.debug("Begin get user list by Longitude and Latitude range");
		Map<String, Object> param = new HashMap<String, Object>(6, 1);
		param.put("minLongitude", minLongitude);
		param.put("maxLongitude", maxLongitude);
		param.put("minLatitude", minLatitude);
		param.put("maxLatitude", maxLatitude);
		param.put("gender", gender);

		return userService.userList(param);
	}
}
