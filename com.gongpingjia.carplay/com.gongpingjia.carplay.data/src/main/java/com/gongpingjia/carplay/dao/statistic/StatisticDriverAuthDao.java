package com.gongpingjia.carplay.dao.statistic;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;

import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2015/10/28.
 */
public interface StatisticDriverAuthDao extends BaseDao<StatisticDriverAuth, String> {


    /**
     * 统计用户的车主认证
     *
     * @param start
     * @param end
     * @param unit
     * @return
     */
    List<Map> statisticDriverAuth(Long start, Long end, int unit);
}
