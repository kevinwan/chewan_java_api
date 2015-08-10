package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityCover;

public interface ActivityCoverDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityCover record);


    ActivityCover selectByPrimaryKey(String id);


    int updateByPrimaryKey(ActivityCover record);
}