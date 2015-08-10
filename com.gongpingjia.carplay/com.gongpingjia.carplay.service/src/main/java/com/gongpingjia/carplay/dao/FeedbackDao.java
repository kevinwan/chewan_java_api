package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Feedback;

public interface FeedbackDao {
    int deleteByPrimaryKey(String id);

    int insert(Feedback record);


    Feedback selectByPrimaryKey(String id);


    int updateByPrimaryKey(Feedback record);
}