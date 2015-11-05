package com.gongpingjia.carplay.dao.util;

import com.gongpingjia.carplay.common.util.Constants;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by licheng on 2015/11/5.
 */
public class DaoUtil {

    /**
     * 根据查询单位进行排序
     *
     * @param unit
     * @return
     */
    public static List<Sort.Order> buildSortOrder(int unit) {
        List<Sort.Order> orderList = new ArrayList<>();
        for (int index = 0; index <= unit; index++) {
            orderList.add(new Sort.Order(Sort.Direction.ASC, Constants.StatisticUnitString.STATISTIC_UNIT_LIST.get(index)));
        }
        return orderList;
    }

    /**
     * 根据查询单位进行构造分组
     *
     * @param unit
     * @return
     */
    public static GroupOperation buildGroupOperation(int unit) {
        GroupOperation groupOperation = null;
        if (unit <= Constants.StatisticUnit.YEAR) {
            groupOperation = Aggregation.group("event", Constants.StatisticUnitString.YEAR).count().as("count");
        } else if (unit <= Constants.StatisticUnit.MONTH) {
            groupOperation = Aggregation.group("event", Constants.StatisticUnitString.YEAR,
                    Constants.StatisticUnitString.MONTH).count().as("count");
        } else if (unit <= Constants.StatisticUnit.DAY) {
            groupOperation = Aggregation.group("event", Constants.StatisticUnitString.YEAR,
                    Constants.StatisticUnitString.MONTH, Constants.StatisticUnitString.DAY).count().as("count");
        } else if (unit <= Constants.StatisticUnit.HOUR) {
            groupOperation = Aggregation.group("event", Constants.StatisticUnitString.YEAR,
                    Constants.StatisticUnitString.MONTH, Constants.StatisticUnitString.DAY, Constants.StatisticUnitString.HOUR).count().as("count");
        } else {
            groupOperation = Aggregation.group("event", Constants.StatisticUnitString.YEAR,
                    Constants.StatisticUnitString.MONTH, Constants.StatisticUnitString.DAY, Constants.StatisticUnitString.HOUR,
                    Constants.StatisticUnitString.MINUTE).count().as("count");
        }
        return groupOperation;
    }
}
