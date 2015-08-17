package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

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
		return DASUtil.update(Message.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<Map<String, Object>> selectCountByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectCountByUserAndTypeComment", param);
	}

	@Override
	public List<Map<String, Object>> selectCountByUserAndTypeNotComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectCountByUserAndTypeNotComment", param);
	}

	@Override
	public List<Map<String, Object>> selectContentByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectContentByUserAndTypeComment", param);
	}

	@Override
	public List<Map<String, Object>> selectContentByUserAndTypeNotComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectContentByUserAndTypeNotComment", param);
	}

}
