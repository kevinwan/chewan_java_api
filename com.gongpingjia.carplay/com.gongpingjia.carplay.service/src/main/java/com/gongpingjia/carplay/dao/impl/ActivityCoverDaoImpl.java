package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.po.ActivityCover;

@Service
public class ActivityCoverDaoImpl implements ActivityCoverDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(ActivityCover.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ActivityCover record) {
		return DASUtil.save(ActivityCover.class.getName(), "insert", record);
	}

	@Override
	public int insert(List<ActivityCover> recordList) {
		return DASUtil.saveList(ActivityCover.class.getName(), "insert", recordList);
	}

	@Override
	public ActivityCover selectByPrimaryKey(String id) {
		return DASUtil.selectOne(ActivityCover.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(ActivityCover record) {
		return DASUtil.update(ActivityCover.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<Map<String, String>> selectByActivity(Map<String, Object> param) {
		return DASUtil.selectList(ActivityCover.class.getName(),"selectByActivity", param);
	}

	@Override
	public int deleteByActivityId(String activityId) {
		return DASUtil.delete(ActivityCover.class.getName(), "deleteByActivityId", activityId);
	}

}
