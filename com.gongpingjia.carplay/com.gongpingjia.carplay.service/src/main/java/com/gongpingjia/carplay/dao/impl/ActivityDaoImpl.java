package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.po.Activity;

@Service
public class ActivityDaoImpl implements ActivityDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(Activity.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(Activity record) {
		return DASUtil.save(Activity.class.getName(), "insert", record);
	}

	@Override
	public Activity selectByPrimaryKey(String id) {
		return DASUtil.selectOne(Activity.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(Activity record) {
		return DASUtil.update(Activity.class.getName(), "updateByPrimaryKey", record);
	}

}
