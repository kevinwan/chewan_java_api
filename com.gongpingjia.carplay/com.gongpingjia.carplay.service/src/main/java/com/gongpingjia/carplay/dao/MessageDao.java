package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Message;

public interface MessageDao {
	int deleteByPrimaryKey(String id);

	int insert(Message record);

	Message selectByPrimaryKey(String id);

	int updateByPrimaryKey(Message record);
}