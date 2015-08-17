package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.po.ActivityApplication;

@Service
public class ActivityApplicationDaoImpl implements ActivityApplicationDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(ActivityApplication.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ActivityApplication record) {
		return DASUtil.save(ActivityApplication.class.getName(), "insert", record);
	}

	@Override
	public ActivityApplication selectByPrimaryKey(String id) {
		return DASUtil.selectOne(ActivityApplication.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(ActivityApplication record) {
		return DASUtil.update(ActivityApplication.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<ActivityApplication> selectByParam(Object param) {
		return DASUtil.selectList(ActivityApplication.class.getName(), "selectByParam", param);
	}

	public List<Map<String, Long>> selectByCountOfActivityUserAndStatus(Map<String, Object> param) {
		return DASUtil.selectList(ActivityApplication.class.getName(), "selectByCountOfActivityUserAndStatus", param);
	}

	@Override
	public List<Map<String, Object>> selectByOrganizer(Map<String, Object> param) {
		return DASUtil.selectList(ActivityApplication.class.getName(), "selectByOrganizer", param);
	}

}
