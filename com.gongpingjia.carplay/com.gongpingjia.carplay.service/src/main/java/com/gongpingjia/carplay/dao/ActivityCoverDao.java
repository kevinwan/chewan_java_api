package com.gongpingjia.carplay.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityCover;

public interface ActivityCoverDao {
	int deleteByPrimaryKey(String id);

	int insert(ActivityCover record);

	int insert(List<ActivityCover> recordList);

	ActivityCover selectByPrimaryKey(String id);

	int updateByPrimaryKey(ActivityCover record);

	List<LinkedHashMap<String, String>> selectByActivity(Map<String, Object> param);

}