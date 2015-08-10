package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.ActivitySubscription;
import com.gongpingjia.carplay.po.ActivitySubscriptionKey;

public interface ActivitySubscriptionDao {
    int deleteByPrimaryKey(ActivitySubscriptionKey key);

    int insert(ActivitySubscription record);


    ActivitySubscription selectByPrimaryKey(ActivitySubscriptionKey key);


    int updateByPrimaryKey(ActivitySubscription record);
}