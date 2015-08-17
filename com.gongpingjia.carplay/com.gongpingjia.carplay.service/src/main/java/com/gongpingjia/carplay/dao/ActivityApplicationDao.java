package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityApplication;

public interface ActivityApplicationDao {
	int deleteByPrimaryKey(String id);

	int insert(ActivityApplication record);

	ActivityApplication selectByPrimaryKey(String id);

	int updateByPrimaryKey(ActivityApplication record);

	List<ActivityApplication> selectByParam(Object param);

	List<Map<String, Long>> selectByCountOfActivityUserAndStatus(Map<String, Object> param);

	List<Map<String, Object>> selectByOrganizer(Map<String, Object> param);
}