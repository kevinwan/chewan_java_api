package com.gongpingjia.carplay.dao.activity.impl;

import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("activityDao")
public class ActivityDaoImpl extends BaseDaoImpl<Activity,String> implements ActivityDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Activity> getActivityListNear(Landmark landmark, Date nowTime) {
        Date maxTime = new Date(nowTime.getTime() - Activity.MAX_EXPIRE_TIME);
        mongoTemplate.geoNear(NearQuery.near(landmark.getLatitude(), landmark.getLongitude()), Activity.class);
        mongoTemplate.getCollection("activity").find();
        return null;
    }
}
