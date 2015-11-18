package com.gongpingjia.carplay.controller.datafix;

import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.entity.statistic.StatisticParent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/11/17.
 */
public class FixTool extends BaseTest {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 修复 statistic 数据中 没有保存 周 信息；
     */
    @Test
    public void fixStatisticWithoutWeek() {
        Query noExistsQuery = Query.query(Criteria.where("week").exists(false));
        List<String> collectionNameList = Arrays.asList("statisticActivityContact", "statisticActivityMatch", "statisticDriverAuth", "statisticDynamicNearby", "statisticOfficialActivity", "statisticUnRegister");
        for (String collection : collectionNameList) {
            List<StatisticParent> statisticParentList = mongoTemplate.find(noExistsQuery, StatisticParent.class, collection);
            for (StatisticParent statisticParent : statisticParentList) {
                if (null != statisticParent.getCreateTime()) {
                    mongoTemplate.updateMulti(Query.query(Criteria.where("_id").is(statisticParent.getId())), Update.update("week", getWeekFromTime(statisticParent.getCreateTime())), collection);
                }
            }
        }

    }


    private int getWeekFromTime(long timeLong) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
