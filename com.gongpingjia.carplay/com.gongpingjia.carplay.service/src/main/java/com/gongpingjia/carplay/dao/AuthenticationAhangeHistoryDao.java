package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.AuthenticationAhangeHistory;

public interface AuthenticationAhangeHistoryDao {
    int deleteByPrimaryKey(String id);

    int insert(AuthenticationAhangeHistory record);


    AuthenticationAhangeHistory selectByPrimaryKey(String id);


    int updateByPrimaryKey(AuthenticationAhangeHistory record);
}