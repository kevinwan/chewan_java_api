package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.UserLocationDao;
import com.gongpingjia.carplay.po.UserLocation;

@Service
public class UserLocationDaoImpl implements UserLocationDao {

	@Override
	public int deleteByPrimaryKey(String deviceToken) {
		return DASUtil.delete(UserLocation.class.getName(), "deleteByPrimaryKey", deviceToken);
	}

	@Override
	public int insert(UserLocation record) {
		return DASUtil.save(UserLocation.class.getName(), "insert", record);
	}

	@Override
	public UserLocation selectByPrimaryKey(String deviceToken) {
		return DASUtil.selectOne(UserLocation.class.getName(), "selectByPrimaryKey", deviceToken);
	}

	@Override
	public int updateByPrimaryKey(UserLocation record) {
		return DASUtil.update(UserLocation.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public int replaceIntoLocation(UserLocation record) {
		return DASUtil.save(UserLocation.class.getName(), "replaceIntoLocation", record);
	}

	@Override
	public List<Map<String, Object>> listUserByParam(Map<String, Object> param) {
		return DASUtil.selectList(UserLocation.class.getName(), "listUserByParam", param);
	}

}
