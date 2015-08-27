package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserInfo;

@Service
public class UserDaoImpl implements UserDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(User.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(User user) {
		return DASUtil.save(User.class.getName(), "insert", user);
	}

	@Override
	public User selectByPrimaryKey(String id) {
		return DASUtil.selectOne(User.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public List<User> selectByParam(Map<String, Object> param) {
		return DASUtil.selectList(User.class.getName(), "selectByParam", param);
	}

	@Override
	public int updateByPrimaryKey(User user) {
		return DASUtil.update(User.class.getName(), "updateByPrimaryKey", user);
	}

	@Override
	public UserInfo selectUserInfo(String userid) {
		return DASUtil.selectOne(UserInfo.class.getName(), "selectUserInfo", userid);
	}

	@Override
	public List<UserInfo> userListenList(Map<String, Object> param) {
		return DASUtil.selectList(UserInfo.class.getName(), "userListenList", param);
	}

}
