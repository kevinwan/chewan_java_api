package com.gongpingjia.carplay.service;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserSubscription;

@Service
public interface UserService {

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo register(User user) throws ApiException;

	/**
	 * 用户登录
	 * 
	 * @param user
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo loginUser(User user) throws ApiException;

	/**
	 * 忘记密码
	 * 
	 * @param user
	 * @param code
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo forgetPassword(User user, String code) throws ApiException;

	/**
	 * 车主认证
	 * 
	 * @param authenticationApplication
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo applyAuthentication(AuthenticationApplication authenticationApplication, String token, String userId);

	/**
	 * 个人详情
	 * 
	 * @param interviewedUser
	 * @param visitorUser
	 * @param token
	 * @return
	 * @throws ApiException
	 */
	@Transactional(readOnly = true)
	ResponseDo getUserInfo(String interviewedUser, String visitorUser, String token) throws ApiException;

	/**
	 * 关注我的人
	 * 
	 * @param userId
	 * @param ignore
	 * @param limit
	 * @param token
	 * @return
	 */
	@Transactional(readOnly = true)
	ResponseDo userListen(String userId, Integer ignore, Integer limit, String token);

	/**
	 * 关注
	 * 
	 * @param userId
	 * @param targetUserId
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo payAttention(UserSubscription userSubscription, String token);

	/**
	 * 取消关注
	 * 
	 * @param userSubscription
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo unPayAttention(UserSubscription userSubscription, String token);

	/**
	 * 变更我的信息
	 * 
	 * @param userSubscription
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo alterUserInfo(User user, String token);

	/**
	 * 编辑相册图片
	 * 
	 * @param userId
	 * @param photos
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo manageAlbumPhotos(String userId, String[] photos, String token);

	/**
	 * 检查注册用户的参数是否正确
	 * 
	 * @param user
	 *            用户信息
	 * @param request
	 *            请求参数
	 * @throws ApiException
	 *             业务异常
	 */
	void checkRegisterParameters(User user, JSONObject request) throws ApiException;

	/**
	 * 第三方登录
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
	 *
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo snsLogin(String uid, String channel, String sign, String username, String url) throws ApiException;
}
