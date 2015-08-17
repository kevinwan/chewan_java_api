package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.po.ActivitySubscription;
import com.gongpingjia.carplay.po.ActivitySubscriptionKey;

@Service
public class ActivitySubscriptionDaoImpl implements ActivitySubscriptionDao {

	@Override
	public int deleteByPrimaryKey(ActivitySubscriptionKey key) {
		return DASUtil.delete(ActivitySubscription.class.getName(), "deleteByPrimaryKey", key);
	}

	@Override
	public int insert(ActivitySubscription record) {
		return DASUtil.save(ActivitySubscription.class.getName(), "insert", record);
	}

	@Override
	public ActivitySubscription selectByPrimaryKey(ActivitySubscriptionKey key) {
		return DASUtil.selectOne(ActivitySubscription.class.getName(), "selectByPrimaryKey", key);
	}

	@Override
	public int updateByPrimaryKey(ActivitySubscription record) {
		return DASUtil.update(ActivitySubscription.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<Map<String, Object>> selectByUserId(Map<String, Object> param) {
		return DASUtil.selectList(ActivitySubscription.class.getName(), "selectByUserId", param);
	}

	@Override
	public Integer selectCountByParam(Map<String, Object> param) {
		return DASUtil.selectOne(ActivitySubscription.class.getName(), "selectCountByParam", param);
	}

}
