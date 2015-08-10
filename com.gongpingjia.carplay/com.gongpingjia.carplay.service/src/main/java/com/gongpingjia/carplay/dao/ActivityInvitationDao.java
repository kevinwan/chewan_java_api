package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivityInvitation;

public interface ActivityInvitationDao {
    int deleteByPrimaryKey(String id);

    int insert(ActivityInvitation record);


    ActivityInvitation selectByPrimaryKey(String id);


    int updateByPrimaryKey(ActivityInvitation record);
}