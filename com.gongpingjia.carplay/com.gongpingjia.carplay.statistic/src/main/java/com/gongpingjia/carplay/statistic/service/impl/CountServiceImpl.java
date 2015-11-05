package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.statistic.service.CountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
@Service("countService")
public class CountServiceImpl implements CountService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Logger logger = LoggerFactory.getLogger(CountServiceImpl.class);

    public List<Map> getCountMap(long startTime, long endTime, String collectionName, String eventType, String... timeFieldNames) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("createTime").gte(startTime).lt(endTime).and("event").is(eventType)),
                Aggregation.group(timeFieldNames).sum("count").as("count")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, collectionName, Map.class);
        return results.getMappedResults();
    }

    @Override
    public Map<String, Integer> getCountByDay(String startStr, String endStr, String collectionName, String eventType) throws ApiException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long startTime = null;
        Long endTime = null;
        try {
            startTime = dateFormat.parse(startStr).getTime();
            //endTime + 1天的时间     当天的数据也需要返回
            endTime = DateUtil.addTime(dateFormat.parse(endStr), Calendar.DATE, 1);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new ApiException("时间转换出错");
        }

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("createTime").gte(startTime).lt(endTime).and("event").is(eventType)),
                Aggregation.group("year", "month", "day").sum("count").as("count")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, collectionName, Map.class);
        List<Map> mappedResults = results.getMappedResults();
        Map<String, Integer> countMap = new HashMap<>(mappedResults.size());
        for (Map item : mappedResults) {
            StringBuilder timeStr = new StringBuilder();
            String month = String.valueOf(item.get("month"));
            if (month.length() == 1) {
                month = "0" + month;
            }
            String day = String.valueOf(item.get("day"));
            if (day.length() == 1) {
                day = "0" + day;
            }
            timeStr.append(item.get("year")).append("-").append(month).append("-").append(day);
            countMap.put(timeStr.toString(), (Integer) item.get("count"));
        }
        return countMap;
    }
}
