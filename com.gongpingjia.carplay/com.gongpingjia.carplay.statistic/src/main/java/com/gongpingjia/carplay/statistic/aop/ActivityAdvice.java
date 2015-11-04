package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.IPFetchUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticActivityMatchDao;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.statistic.StatisticActivityMatch;
import com.gongpingjia.carplay.service.util.ActivityQueryParam;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/4 0004.
 */
@Aspect
@Service
public class ActivityAdvice {

    @Autowired
    private StatisticActivityMatchDao statisticActivityMatchDao;


    @Before(value = "execution(* com.gongpingjia.carplay.service.ActivityService.getNearByActivityList(..))")
    public void searchActivity(JoinPoint joinPoint) {
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
        ActivityQueryParam queryParam = (ActivityQueryParam) joinPoint.getArgs()[1];
        if (StringUtils.isNotEmpty(queryParam.getGender()) || StringUtils.isNotEmpty(queryParam.getProvince())
                || StringUtils.isNotEmpty(queryParam.getCity()) || StringUtils.isNotEmpty(queryParam.getPay())
                || queryParam.getTransfer() != null) {
            StatisticActivityMatch statisticActivityMatch = new StatisticActivityMatch();
            statisticActivityMatch.setType(queryParam.getType());
            statisticActivityMatch.setMajorType(queryParam.getMajorType());
            statisticActivityMatch.setPay(queryParam.getPay());
            Address address = new Address();
            address.setProvince(queryParam.getProvince());
            address.setCity(queryParam.getCity());
            address.setDistrict(queryParam.getDistrict());
            statisticActivityMatch.setDestination(address);
            Landmark landmark = new Landmark();
            landmark.setLongitude(queryParam.getLongitude());
            landmark.setLatitude(queryParam.getLatitude());
            statisticActivityMatch.setDestPoint(landmark);
            if (queryParam.getTransfer() != null) {
                statisticActivityMatch.setTransfer(queryParam.getTransfer());
            }
            if (null != queryParam.getUserId()) {
                statisticActivityMatch.setUserId(queryParam.getUserId());
            }
            statisticActivityMatch.setIp(IPFetchUtil.getIPAddress(request));
            statisticActivityMatch.setEvent(StatisticActivityMatch.ACTIVITY_TYPE_MATCH_COUNT);
            statisticActivityMatch.setCount(1);
            statisticActivityMatch.recordTime(DateUtil.getTime());

            statisticActivityMatchDao.save(statisticActivityMatch);
        }
    }
}
