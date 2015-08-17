package com.gongpingjia.carplay.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.User;

@Service
public interface UserService {

	/**
	 * 查询所有的用户
	 * 
	 * @return 用户列表
	 */
	List<User> queryUsers();

	/**
	 * 查询单个用户
	 * 
	 * @param id
	 * @return
	 */
	User findUser(Long id);

	/**
	 * 保存用户
	 * 
	 * @param user
	 * @return
	 */
	int saveUser(User user);
	
	/**
	 * 注册用户
	 * 
	 * @param user
	 * @param code
	 * @return
	 */
	ResponseDo register(User user, String code);
	
	/**
	 * 用户登录
	 * 
	 * @param user
	 * @return
	 */
	ResponseDo loginUser(User user);
	
	/**
	 * 忘记密码
	 * 
	 * @param user
	 * @param code
	 * @return
	 */
	ResponseDo forgetPassword(User user, String code);
	
	/**
	 * 车主认证
	 * 
	 * @param authenticationApplication
	 * @return
	 */
	ResponseDo applyAuthentication(AuthenticationApplication authenticationApplication,String token,String userId);
	

	/**
	 * 个人详情
	 * 
	 * @param interviewedUser
	 * @param visitorUser
	 * @param token
	 * @return
	 */
	ResponseDo userInfo(String interviewedUser, String visitorUser, String token);
}
