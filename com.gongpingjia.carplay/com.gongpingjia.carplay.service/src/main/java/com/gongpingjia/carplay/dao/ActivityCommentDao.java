package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityComment;

public interface ActivityCommentDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityComment record);


    ActivityComment selectByPrimaryKey(String id);


    int updateByPrimaryKey(ActivityComment record);
}