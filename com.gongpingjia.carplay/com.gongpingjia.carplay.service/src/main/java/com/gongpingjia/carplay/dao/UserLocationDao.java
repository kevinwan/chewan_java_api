package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.UserLocation;

public interface UserLocationDao {
	int deleteByPrimaryKey(String deviceToken);

	int insert(UserLocation record);

	UserLocation selectByPrimaryKey(String deviceToken);

	int updateByPrimaryKey(UserLocation record);

	int replaceIntoLocation(UserLocation record);
	
	List<Map<String, Object>> listUserByParam(Map<String, Object> param);
}