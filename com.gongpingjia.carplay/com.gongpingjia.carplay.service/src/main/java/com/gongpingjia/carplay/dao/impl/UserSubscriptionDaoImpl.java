package com.gongpingjia.carplay.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.UserSubscriptionDao;
import com.gongpingjia.carplay.po.UserSubscription;
import com.gongpingjia.carplay.po.UserSubscriptionKey;

@Service
public class UserSubscriptionDaoImpl implements UserSubscriptionDao {

	@Override
	public int deleteByPrimaryKey(UserSubscriptionKey key) {
		return DASUtil.delete(UserSubscription.class.getName(), "deleteByPrimaryKey", key);
	}

	@Override
	public int insert(UserSubscription record) {
		return DASUtil.save(UserSubscription.class.getName(), "insert", record);
	}

	@Override
	public UserSubscription selectByPrimaryKey(UserSubscription key) {
		return DASUtil.selectOne(UserSubscription.class.getName(), "selectByPrimaryKey", key);
	}

	@Override
	public int updateByPrimaryKey(UserSubscription record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscriptionCount(Map<String, Object> param) {
		return DASUtil.selectOne(UserSubscription.class.getName(), "subscriptionCount", param);
	}

}
