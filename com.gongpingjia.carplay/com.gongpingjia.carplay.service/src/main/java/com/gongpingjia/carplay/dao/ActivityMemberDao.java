package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.ActivityMemberKey;

public interface ActivityMemberDao {
	int deleteByPrimaryKey(ActivityMemberKey key);

	int insert(ActivityMember record);

	ActivityMember selectByPrimaryKey(ActivityMemberKey key);

	int updateByPrimaryKey(ActivityMember record);
	
	int deleteByParam(ActivityMemberKey key);
}