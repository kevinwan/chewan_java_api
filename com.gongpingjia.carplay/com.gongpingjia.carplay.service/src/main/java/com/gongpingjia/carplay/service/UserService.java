package com.gongpingjia.carplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserSubscription;

@Service
public interface UserService {
	
	/**
	 * 注册用户
	 * 
	 * @param user
	 * @param code
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo register(User user, String code);
	
	/**
	 * 用户登录
	 * 
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = true)
	ResponseDo loginUser(User user);
	
	/**
	 * 忘记密码
	 * 
	 * @param user
	 * @param code
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo forgetPassword(User user, String code);
	
	/**
	 * 车主认证
	 * 
	 * @param authenticationApplication
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo applyAuthentication(AuthenticationApplication authenticationApplication,String token,String userId);
	
	/**
	 * 个人详情
	 * 
	 * @param interviewedUser
	 * @param visitorUser
	 * @param token
	 * @return
	 */
	@Transactional(readOnly = true)
	ResponseDo userInfo(String interviewedUser, String visitorUser, String token);
	
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
	ResponseDo userListen(String userId,Integer ignore,Integer limit,String token);
	
	/**
	 * 关注
	 * 
	 * @param userId
	 * @param targetUserId
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo payAttention(UserSubscription userSubscription,String token);
	
	/**
	 * 取消关注
	 * 
	 * @param userSubscription
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo unPayAttention(UserSubscription userSubscription,String token);
	
	/**
	 * 变更我的信息
	 * 
	 * @param userSubscription
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo alterUserInfo(User user,String token);
	
	/**
	 * 编辑相册图片
	 * 
	 * @param userId
	 * @param photos
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo manageAlbumPhotos(String userId,String[] photos,String token);
}
