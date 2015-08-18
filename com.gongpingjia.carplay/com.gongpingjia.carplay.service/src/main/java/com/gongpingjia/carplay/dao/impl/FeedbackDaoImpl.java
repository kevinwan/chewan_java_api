package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.FeedbackDao;
import com.gongpingjia.carplay.po.Feedback;

@Service
public class FeedbackDaoImpl implements FeedbackDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(Feedback.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(Feedback record) {
		return DASUtil.save(Feedback.class.getName(), "insert", record);
	}

	@Override
	public Feedback selectByPrimaryKey(String id) {
		return DASUtil.selectOne(Feedback.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(Feedback record) {
		return DASUtil.update(Feedback.class.getName(), "updateByPrimaryKey", record);
	}

}
