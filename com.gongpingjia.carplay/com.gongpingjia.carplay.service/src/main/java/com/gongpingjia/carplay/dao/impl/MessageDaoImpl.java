package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.MessageDao;
import com.gongpingjia.carplay.po.Message;

@Service
public class MessageDaoImpl implements MessageDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(Message.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(Message record) {
		return DASUtil.save(Message.class.getName(), "insert", record);
	}

	@Override
	public Message selectByPrimaryKey(String id) {
		return DASUtil.selectOne(Message.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(Message record) {
		return DASUtil.save(Message.class.getName(), "updateByPrimaryKey", record);
	}

}
