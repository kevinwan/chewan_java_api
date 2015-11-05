package com.gongpingjia.carplay.statistic.service;

import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;

import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/11/5.
 */
public interface UserStatisticService {

    /**
     * 获取用户的注册
     * @param start
     * @param end
     * @param unit
     * @return
     */
    List<Map> getUserLicenseAuthenticationStatistic(Long start, Long end, int unit);
}
