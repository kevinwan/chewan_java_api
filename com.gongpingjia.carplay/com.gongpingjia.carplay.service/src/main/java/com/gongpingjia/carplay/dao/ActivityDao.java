package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Activity;

public interface ActivityDao {
    int deleteByPrimaryKey(String id);

    int insert(Activity record);

    Activity selectByPrimaryKey(String id);

    int updateByPrimaryKey(Activity record);
}