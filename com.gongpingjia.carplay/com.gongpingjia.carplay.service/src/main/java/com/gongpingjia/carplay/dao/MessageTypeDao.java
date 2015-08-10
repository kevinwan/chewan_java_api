package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.MessageType;

public interface MessageTypeDao {
    int deleteByPrimaryKey(String name);

    int insert(MessageType record);

    int insertSelective(MessageType record);
}