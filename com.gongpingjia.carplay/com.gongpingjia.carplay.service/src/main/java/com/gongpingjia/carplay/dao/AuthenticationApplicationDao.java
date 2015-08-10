package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.AuthenticationApplication;

public interface AuthenticationApplicationDao {
    int deleteByPrimaryKey(String id);

    int insert(AuthenticationApplication record);


    AuthenticationApplication selectByPrimaryKey(String id);


    int updateByPrimaryKey(AuthenticationApplication record);
}