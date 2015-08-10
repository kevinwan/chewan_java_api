package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.InvitationFollower;

public interface InvitationFollowerDao {
    int deleteByPrimaryKey(String id);

    int insert(InvitationFollower record);


    InvitationFollower selectByPrimaryKey(String id);


    int updateByPrimaryKey(InvitationFollower record);
}