package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.InvitationChannel;

public interface InvitationChannelDao {
    int deleteByPrimaryKey(String name);

    int insert(InvitationChannel record);

    int insertSelective(InvitationChannel record);
}