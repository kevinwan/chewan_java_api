package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityApplication;

public interface ActivityApplicationDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityApplication record);

    ActivityApplication selectByPrimaryKey(String id);

    int updateByPrimaryKey(ActivityApplication record);
}