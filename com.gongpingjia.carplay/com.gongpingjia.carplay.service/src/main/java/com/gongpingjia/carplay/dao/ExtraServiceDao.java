package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ExtraService;

public interface ExtraServiceDao {
    int deleteByPrimaryKey(String id);

    int insert(ExtraService record);


    ExtraService selectByPrimaryKey(String id);


    int updateByPrimaryKey(ExtraService record);
}