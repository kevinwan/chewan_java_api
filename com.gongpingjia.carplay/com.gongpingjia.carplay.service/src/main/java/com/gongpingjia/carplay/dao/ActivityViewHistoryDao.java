package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityViewHistory;

public interface ActivityViewHistoryDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityViewHistory record);


    ActivityViewHistory selectByPrimaryKey(String id);


    int updateByPrimaryKey(ActivityViewHistory record);
}