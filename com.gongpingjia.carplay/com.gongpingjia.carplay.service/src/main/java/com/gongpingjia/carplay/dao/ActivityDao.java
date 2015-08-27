package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.Activity;

public interface ActivityDao {
	int deleteByPrimaryKey(String id);

	int insert(Activity record);

	Activity selectByPrimaryKey(String id);

	int updateByPrimaryKey(Activity record);

	List<Map<String, Object>> selectByOrganizer(Map<String, Object> param);

	int selectActivityPostNumber(String userid);
}