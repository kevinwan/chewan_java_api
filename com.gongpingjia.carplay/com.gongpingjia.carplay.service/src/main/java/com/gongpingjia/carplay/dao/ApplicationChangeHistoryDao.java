package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ApplicationChangeHistory;

public interface ApplicationChangeHistoryDao {
    int deleteByPrimaryKey(String id);

    int insert(ApplicationChangeHistory record);


    ApplicationChangeHistory selectByPrimaryKey(String id);


    int updateByPrimaryKey(ApplicationChangeHistory record);
}