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
	public Integer selectCountByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.selectOne(Message.class.getName(), "selectCountByUserAndTypeComment", param);
	}

	@Override
	public Integer selectCountByUserAndTypeNotComment(Map<String, Object> param) {
		return DASUtil.selectOne(Message.class.getName(), "selectCountByUserAndTypeNotComment", param);
	}

	@Override
	public Map<String, Object> selectContentByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.selectOne(Message.class.getName(), "selectContentByUserAndTypeComment", param);
	}

	@Override
	public Map<String, Object> selectContentByUserAndTypeNotComment(Map<String, Object> param) {
		return DASUtil.selectOne(Message.class.getName(), "selectContentByUserAndTypeNotComment", param);
	}

	@Override
	public List<Map<String, Object>> selectMessageListByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectMessageListByUserAndTypeComment", param);
	}

	@Override
	public List<Map<String, Object>> selectMessageListByUserAndTypeNotComment(Map<String, Object> param) {
		return DASUtil.selectList(Message.class.getName(), "selectMessageListByUserAndTypeNotComment", param);
	}

	@Override
	public int updateIsCheckedByUserAndTypeComment(Map<String, Object> param) {
		return DASUtil.update(Message.class.getName(), "updateIsCheckedByUserAndTypeComment", param);
	}

	@Override
	public int updateIsCheckedByUserAndTypeCommentNotComment(Map<String, Object> param) {
		return DASUtil.update(Message.class.getName(), "updateIsCheckedByUserAndTypeNotComment", param);
	}

	@Override
	public int updateRemarksByExtra3(Message record) {
		return DASUtil.update(Message.class.getName(), "updateRemarksByExtra3", record);
	}

	@Override
	public Message selectByMeesageIdAndUserId(Map<String, Object> param) {
		return DASUtil.selectOne(Message.class.getName(), "selectByMeesageIdAndUserId", param);
	}

	@Override
	public int updateIsDeletedByMessageId(String messageId) {
		return DASUtil.update(Message.class.getName(), "updateIsDeletedByMessageId", messageId);
	}

	@Override
	public int updateIsDeletedByMessageId(List<String> messageIds) {
		return DASUtil.updateList(Message.class.getName(), "updateIsDeletedByMessageId", messageIds);
	}

}
