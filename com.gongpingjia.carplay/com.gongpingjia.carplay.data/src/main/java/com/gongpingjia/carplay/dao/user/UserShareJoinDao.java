package com.gongpingjia.carplay.dao.user;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.entity.user.UserShareJoin;

/**
 * Created by 123 on 2015/11/16.
 */
public interface UserShareJoinDao extends BaseDao<UserShareJoin, String>{
    UserShareJoin findOne(String phone, String activityId, String activityType);
}
