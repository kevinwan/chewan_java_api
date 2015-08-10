package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityType;

public interface ActivityTypeDao {
    int deleteByPrimaryKey(String name);

    int insert(ActivityType record);

    int insertSelective(ActivityType record);
}