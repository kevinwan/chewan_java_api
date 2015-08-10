package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.City;

public interface CityDao {
    int deleteByPrimaryKey(String name);

    int insert(City record);


    City selectByPrimaryKey(String name);


    int updateByPrimaryKey(City record);
}