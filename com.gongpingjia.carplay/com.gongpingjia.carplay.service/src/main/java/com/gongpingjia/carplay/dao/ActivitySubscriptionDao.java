package com.gongpingjia.carplay.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivitySubscription;
import com.gongpingjia.carplay.po.ActivitySubscriptionKey;

public interface ActivitySubscriptionDao {
    int deleteByPrimaryKey(ActivitySubscriptionKey key);

    int insert(ActivitySubscription record);


    ActivitySubscription selectByPrimaryKey(ActivitySubscriptionKey key);


    int updateByPrimaryKey(ActivitySubscription record);

	List<LinkedHashMap<String, Object>> selectByUserId(Map<String, Object> param);
}