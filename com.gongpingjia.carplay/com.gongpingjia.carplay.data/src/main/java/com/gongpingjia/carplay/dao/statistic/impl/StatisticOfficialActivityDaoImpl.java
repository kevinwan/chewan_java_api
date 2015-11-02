package com.gongpingjia.carplay.dao.statistic.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.statistic.StatisticOfficialActivityDao;
import com.gongpingjia.carplay.entity.statistic.StatisticActivityMatch;
import com.gongpingjia.carplay.entity.statistic.StatisticOfficialActivity;
import org.springframework.stereotype.Repository;

/**
 * Created by licheng on 2015/10/27.
 */
@Repository("statisticOfficialActivityDao")
public class StatisticOfficialActivityDaoImpl extends BaseDaoImpl<StatisticOfficialActivity, String> implements StatisticOfficialActivityDao {
}
