package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Province;

public interface ProvinceDao {
    int deleteByPrimaryKey(String name);

    int insert(Province record);

    int insertSelective(Province record);
}