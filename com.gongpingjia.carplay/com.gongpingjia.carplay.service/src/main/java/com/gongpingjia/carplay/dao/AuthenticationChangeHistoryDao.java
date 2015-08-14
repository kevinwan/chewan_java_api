package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.AuthenticationChangeHistory;

public interface AuthenticationChangeHistoryDao {
    int deleteByPrimaryKey(String id);

    int insert(AuthenticationChangeHistory record);

    int insertSelective(AuthenticationChangeHistory record);

    AuthenticationChangeHistory selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AuthenticationChangeHistory record);

    int updateByPrimaryKey(AuthenticationChangeHistory record);
}