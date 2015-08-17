package com.gongpingjia.carplay.dao;

import java.util.Map;

import com.gongpingjia.carplay.po.UserSubscription;
import com.gongpingjia.carplay.po.UserSubscriptionKey;

public interface UserSubscriptionDao {
    int deleteByPrimaryKey(UserSubscriptionKey key);

    int insert(UserSubscription record);


    UserSubscription selectByPrimaryKey(UserSubscriptionKey key);


    int updateByPrimaryKey(UserSubscription record);
    
    int subscriptionCount (Map<String, Object> param);
}