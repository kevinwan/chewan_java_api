package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.common.AreaDao;
import com.gongpingjia.carplay.dao.history.InterestMessageDao;
import com.gongpingjia.carplay.dao.statistic.StatisticUnRegisterDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.mongodb.DBCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.util.*;

public class TestEle extends BaseTest {

    private static Logger LOG = LoggerFactory.getLogger(TestEle.class);

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserTokenDao userTokenDao;


    @Autowired
    private OfficialActivityDao officialActivityDao;


    @Autowired
    private InterestMessageDao interestMessageDao;

    @Autowired
    private StatisticUnRegisterDao statisticUnRegisterDao;


    @Autowired
    private AreaDao areaDao;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void testAll() {
        try {
            Activity activity = new Activity();
            activity.setType("123");
            Class<?> superclass = activity.getClass().getSuperclass();
            Field[] declaredFields = superclass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                System.out.println(field.get(activity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testObj() {
        Activity obj = new Activity();
        int depth = 0;
        Class cls = obj.getClass();
        Class tempCls = cls;
        while (!tempCls.isInstance(Object.class)) {
            depth++;
            tempCls = tempCls.getSuperclass();
        }
        for (int i = 0; i < depth; i++) {
            Class valCls = getDepthClass(cls, i);
        }
        System.out.println(obj.getClass().getSuperclass().isInstance(Object.class));
    }

    private static Class getDepthClass(Class cls, int depth) {
        Class temp = cls;
        for (int i = 0; i < depth; i++) {
            temp = cls.getSuperclass();
        }
        return temp;
    }

    @Test
    public void dropIndex() {
        String[] collectionNames = {"activity", "appointment", "officialActivity", "pushInfo", "photoAuth", "message", "albumViewHistory", "authenticationHistory", "interestMessage", "subscriber", "user"};
        for (String name : collectionNames) {
            DBCollection tempCollection = mongoTemplate.getCollection(name);
            tempCollection.dropIndexes();
        }
    }

    @Test
    public void testGroup() {
        Date currentTime = new Date();
        long start = DateUtil.addTime(currentTime, Calendar.DATE, -5);
        long end = currentTime.getTime();
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("createTime").gte(start).lt(end)),
                Aggregation.group("year", "month", "day").sum("count").as("count")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "statisticUnRegister", Map.class);
        List<Map> resultList = results.getMappedResults();
        Iterator<Map> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            Map item = iterator.next();
            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder.append(item.get("year")).append("-").append(item.get("month")).append("-").append(item.get("day"));
            System.out.println(dateBuilder.toString() + ":" + item.get("count"));
        }
    }
}
