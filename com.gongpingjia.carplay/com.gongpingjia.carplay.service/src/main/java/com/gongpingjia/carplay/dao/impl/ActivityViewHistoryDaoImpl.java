package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityViewHistoryDao;
import com.gongpingjia.carplay.po.ActivityViewHistory;

@Service
public class ActivityViewHistoryDaoImpl implements ActivityViewHistoryDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(ActivityViewHistory.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ActivityViewHistory record) {
		return DASUtil.save(ActivityViewHistory.class.getName(), "insert", record);
	}

	@Override
	public ActivityViewHistory selectByPrimaryKey(String id) {
		return DASUtil.selectOne(ActivityViewHistory.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(ActivityViewHistory record) {
		return DASUtil.update(ActivityViewHistory.class.getName(), "updateByPrimaryKey", record);
	}

}
