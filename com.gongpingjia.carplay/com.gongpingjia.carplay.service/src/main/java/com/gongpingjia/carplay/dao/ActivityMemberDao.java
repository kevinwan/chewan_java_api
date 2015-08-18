package com.gongpingjia.carplay.dao;

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

	List<Map<String, String>> selectByActivity(Map<String, Object> param);

	List<Map<String, Object>> selectByUserId(Map<String, Object> param);
	
	List<ActivityMember> selectByParam(Map<String, Object> param);
}