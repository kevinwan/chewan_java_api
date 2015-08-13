package com.gongpingjia.carplay.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
}