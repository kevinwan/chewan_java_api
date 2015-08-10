package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.User;

public interface UserDao {
    int deleteByPrimaryKey(String id);

    int insert(User record);


    User selectByPrimaryKey(String id);


    int updateByPrimaryKey(User record);
}