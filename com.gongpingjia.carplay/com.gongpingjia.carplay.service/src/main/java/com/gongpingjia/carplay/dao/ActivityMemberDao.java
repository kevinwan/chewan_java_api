package com.gongpingjia.carplay.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.ActivityMemberKey;

public interface ActivityMemberDao {
	int deleteByPrimaryKey(ActivityMemberKey key);

	int insert(ActivityMember record);

	ActivityMember selectByPrimaryKey(ActivityMemberKey key);

	int updateByPrimaryKey(ActivityMember record);
	
	int deleteByParam(ActivityMemberKey key);

	List<LinkedHashMap<String, String>> selectByActivity(Map<String, Object> param);

	List<LinkedHashMap<String, Object>> selectByUserId(Map<String, Object> param);
}