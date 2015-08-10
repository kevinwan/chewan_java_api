package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.InvitationCode;

public interface InvitationCodeDao {
	int deleteByPrimaryKey(String code);

	int insert(InvitationCode record);

	InvitationCode selectByPrimaryKey(String code);

	int updateByPrimaryKey(InvitationCode record);
}