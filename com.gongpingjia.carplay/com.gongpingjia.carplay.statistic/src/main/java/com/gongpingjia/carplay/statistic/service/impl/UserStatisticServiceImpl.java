package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.dao.statistic.StatisticDriverAuthDao;
import com.gongpingjia.carplay.statistic.service.UserStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/11/5.
 */
@Service
public class UserStatisticServiceImpl implements UserStatisticService {

    @Autowired
    private StatisticDriverAuthDao driverAuthDao;

    @Override
    public List<Map> getUserLicenseAuthenticationStatistic(Long start, Long end, int unit) {

        return driverAuthDao.statisticDriverAuth(start, end, unit);
    }
}
