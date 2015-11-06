package com.gongpingjia.carplay.dao.statistic.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.statistic.StatisticDriverAuthDao;
import com.gongpingjia.carplay.dao.util.DaoUtil;
import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2015/10/28.
 */
@Repository("statisticDriverAuthDao")
public class StatisticDriverAuthDaoImpl extends BaseDaoImpl<StatisticDriverAuth, String> implements StatisticDriverAuthDao {

    @Override
    public List<Map> statisticDriverAuth(Long start, Long end, int unit) {

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("createTime").gte(start).lt(end)),
                DaoUtil.buildGroupOperation(unit), Aggregation.sort(new Sort(DaoUtil.buildSortOrder(unit))));

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "statisticDriverAuth", Map.class);

        return results.getMappedResults();
    }


}
