package com.gongpingjia.carplay.dao.impl;

import java.util.LinkedHashMap;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(ActivitySubscription record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActivitySubscription selectByPrimaryKey(ActivitySubscriptionKey key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKey(ActivitySubscription record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<LinkedHashMap<String, Object>> selectByUserId(Map<String, Object> param) {
		return DASUtil.selectList(ActivitySubscription.class.getName(), "selectByUserId", param);
	}

}
