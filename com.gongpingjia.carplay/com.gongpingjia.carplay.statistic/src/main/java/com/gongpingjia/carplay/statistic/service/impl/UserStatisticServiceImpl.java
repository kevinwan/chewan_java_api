package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.dao.statistic.StatisticDriverAuthDao;
import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;
import com.gongpingjia.carplay.statistic.service.UserStatisticService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by licheng on 2015/11/5.
 */
public class UserStatisticServiceImpl implements UserStatisticService {

    @Autowired
    private StatisticDriverAuthDao driverAuthDao;

    @Override
    public List<StatisticDriverAuth> getUserLicenseAuthenticationStatistic(Long start, Long end, int unit) {



        return null;
    }
}
