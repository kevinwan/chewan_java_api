package com.gongpingjia.carplay.dao.statistic.impl;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.statistic.StatisticActivityContactDao;
import com.gongpingjia.carplay.entity.statistic.StatisticActivityContact;
import org.springframework.stereotype.Repository;

/**
 * Created by 123 on 2015/10/28.
 */
@Repository("statisticActivityContactDao")
public class StatisticActivityContactDaoImpl extends BaseDaoImpl<StatisticActivityContact, String> implements StatisticActivityContactDao {
}
