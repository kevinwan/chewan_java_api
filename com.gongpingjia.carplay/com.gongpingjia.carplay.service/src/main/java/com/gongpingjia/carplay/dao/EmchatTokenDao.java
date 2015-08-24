package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.EmchatToken;

public interface EmchatTokenDao {
	int deleteByPrimaryKey(String application);

	int insert(EmchatToken record);

	EmchatToken selectByPrimaryKey(String application);

	int updateByPrimaryKey(EmchatToken record);
	
	EmchatToken selectFirstOne();
}