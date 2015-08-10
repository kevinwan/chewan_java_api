package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Company;

public interface CompanyDao {
    int deleteByPrimaryKey(String id);

    int insert(Company record);


    Company selectByPrimaryKey(String id);


    int updateByPrimaryKey(Company record);
}