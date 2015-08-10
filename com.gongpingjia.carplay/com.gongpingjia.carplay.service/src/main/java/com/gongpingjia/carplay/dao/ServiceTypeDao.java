package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ServiceType;

public interface ServiceTypeDao {
    int deleteByPrimaryKey(String name);

    int insert(ServiceType record);

    int insertSelective(ServiceType record);
}