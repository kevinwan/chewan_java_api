package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.IPFetchUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticDynamicNearbyDao;
import com.gongpingjia.carplay.entity.statistic.StatisticDynamicNearby;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2015/11/3 0003.
 */
@Aspect
@Service
public class ActivityPushInfoAdvice {

    @Autowired
    private StatisticDynamicNearbyDao statisticDynamicNearbyDao;

    /**
     * 成功加入到了官方活动
     *
     * @param joinPoint
     * @param responseDo
     */
    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.ActivityService.getActivityPushInfos(..))", returning = "responseDo")
    public void joinSuccess(JoinPoint joinPoint, ResponseDo responseDo) {
        if (responseDo.success()) {
            String userId = (String) joinPoint.getArgs()[1];
            HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
            StatisticDynamicNearby saveItem = new StatisticDynamicNearby();
            saveItem.setUserId(userId);
            saveItem.setIp(IPFetchUtil.getIPAddress(request));
            saveItem.setEvent(StatisticDynamicNearby.DYNAMIC_NEARBY_CLICK);
            saveItem.setCount(1);
            saveItem.recordTime(DateUtil.getTime());

            statisticDynamicNearbyDao.save(saveItem);
        }
    }
}
