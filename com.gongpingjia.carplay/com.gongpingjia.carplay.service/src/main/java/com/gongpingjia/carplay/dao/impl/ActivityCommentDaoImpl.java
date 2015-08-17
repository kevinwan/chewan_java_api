package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityCommentDao;
import com.gongpingjia.carplay.po.ActivityComment;

@Service
public class ActivityCommentDaoImpl implements ActivityCommentDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(ActivityComment.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ActivityComment record) {
		return DASUtil.save(ActivityComment.class.getName(), "insert", record);
	}

	@Override
	public ActivityComment selectByPrimaryKey(String id) {
		return DASUtil.selectOne(ActivityComment.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(ActivityComment record) {
		return DASUtil.update(ActivityComment.class.getName(), "updateByPrimaryKey", record);
	}

}
