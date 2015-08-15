package com.gongpingjia.carplay.dao;

import java.util.List;

import com.gongpingjia.carplay.po.ActivityApplication;

public interface ActivityApplicationDao {
	int deleteByPrimaryKey(String id);

	int insert(ActivityApplication record);

	ActivityApplication selectByPrimaryKey(String id);

	int updateByPrimaryKey(ActivityApplication record);

	List<ActivityApplication> selectByParam(Object param);

}