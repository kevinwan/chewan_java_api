package com.gongpingjia.carplay.dao.activity.impl;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.activity.PushInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by licheng on 2015/10/26.
 */
@Repository("pushInfoDao")
public class PushInfoDaoImpl extends BaseDaoImpl<PushInfo, String> implements PushInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(PushInfoDaoImpl.class);

    public Set<String> groupByReceivedUsers(Collection<String> receivedUserIds, Integer maxReceived) {
        LOG.debug("Received user ids:{}", receivedUserIds);

        Date current = DateUtil.getDate();
        Long start = DateUtil.getZeroTime(current);
        Long end = DateUtil.getNextZeroTime(current);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("createTime").gte(start).lt(end)),
                Aggregation.group("receivedUserId").count().as("count"),
                Aggregation.match(Criteria.where("_id").in(receivedUserIds).and("count").gte(maxReceived)));
        LOG.debug("Aggregation parameters:{}", aggregation.toString());

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "pushInfo", Map.class);
        List<Map> resultList = results.getMappedResults();
        Set<String> userIds = new HashSet<>(resultList.size());
        for (Map item : resultList) {
            userIds.add(String.valueOf(item.get("_id")));
        }
        return userIds;
    }
}
