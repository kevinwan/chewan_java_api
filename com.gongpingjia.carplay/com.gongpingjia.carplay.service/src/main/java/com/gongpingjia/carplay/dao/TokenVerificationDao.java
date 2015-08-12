package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.TokenVerification;

public interface TokenVerificationDao {
	int deleteByPrimaryKey(String userid);

	int insert(TokenVerification record);

	TokenVerification selectByPrimaryKey(String userid);

	int updateByPrimaryKey(TokenVerification record);
}