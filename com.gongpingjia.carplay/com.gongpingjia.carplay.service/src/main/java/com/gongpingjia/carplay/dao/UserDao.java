package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.User;

public interface UserDao {
	int deleteByPrimaryKey(String id);

	int insert(User user);

	User selectByPrimaryKey(String id);

	List<User> selectByParam(Map<String, Object> param);

	int updateByPrimaryKey(User user);
}