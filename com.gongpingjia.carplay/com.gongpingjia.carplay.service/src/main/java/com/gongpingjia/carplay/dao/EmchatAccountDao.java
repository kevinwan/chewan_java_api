package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.EmchatAccount;

public interface EmchatAccountDao {
	int deleteByPrimaryKey(String userid);

	int insert(EmchatAccount record);

	EmchatAccount selectByPrimaryKey(String userid);

	int updateByPrimaryKey(EmchatAccount record);
}