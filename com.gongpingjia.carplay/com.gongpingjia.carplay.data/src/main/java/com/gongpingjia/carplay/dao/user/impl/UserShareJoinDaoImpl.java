package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserShareJoinDao;
import com.gongpingjia.carplay.entity.user.UserShareJoin;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by 123 on 2015/11/16.
 */
@Repository("userShareJoinDao")
public class UserShareJoinDaoImpl extends BaseDaoImpl<UserShareJoin, String> implements UserShareJoinDao {

    @Override
    public UserShareJoin findOne(String phone, String activityId, String activityType) {
        return findOne(Query.query(Criteria.where("phone").is(phone).and("activityId").is(activityId).and("activityType").is(activityType)));
    }
}
