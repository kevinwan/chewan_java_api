package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ApplicationStatus;

public interface ApplicationStatusDao {
    int deleteByPrimaryKey(String name);

    int insert(ApplicationStatus record);

    int insertSelective(ApplicationStatus record);
}