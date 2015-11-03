package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.IPFetchUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticOfficialActivityDao;
import com.gongpingjia.carplay.entity.statistic.StatisticOfficialActivity;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2015/11/2 0002.
 */
@Aspect
@Service
public class OfficialActivityAdvice {

    @Autowired
    private StatisticOfficialActivityDao statisticOfficialActivityDao;


    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.OfficialService.getActivityInfo(..))", returning = "responseDo")
    public void viewList(JoinPoint joinPoint, ResponseDo responseDo) {
        //应当记录 没有加入该官方活动的 查看次数
        if (responseDo.success()) {
            String ipAddress = IPFetchUtil.getIPAddress((HttpServletRequest) joinPoint.getArgs()[0]);
            String id = (String) joinPoint.getArgs()[1];
            Integer idType = (Integer) joinPoint.getArgs()[2];
            String userId = (String) joinPoint.getArgs()[3];

            if (idType.equals(1)) {
                //剔除掉通过环信查看的记录
                return;
            }

            JSONObject data = (JSONObject) responseDo.getData();
//            boolean isMember = data.getBoolean("isMember");
//            if (isMember) {
//                //剔除掉 是当前官方活动成员的查看历史记录
//                return;
//            }

            StatisticOfficialActivity statisticOfficialActivity = new StatisticOfficialActivity();
            //可以允许未登陆用户查看官方活动详情
            if (StringUtils.isNotEmpty(statisticOfficialActivity.getUserId())) {
                statisticOfficialActivity.setUserId(userId);
            } else {
                statisticOfficialActivity.setIp(ipAddress);
            }
            statisticOfficialActivity.setOfficialActivityId(id);
            statisticOfficialActivity.setEvent(StatisticOfficialActivity.OFFICIAL_ACTIVITY_COUNT);
            statisticOfficialActivity.setCount(1);
            statisticOfficialActivity.recordTime(DateUtil.getTime());
            statisticOfficialActivityDao.save(statisticOfficialActivity);
        }
    }

    /**
     * 成功加入到了官方活动
     *
     * @param joinPoint
     * @param responseDo
     */
    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.OfficialService.applyJoinActivity(..))", returning = "responseDo")
    public void joinSuccess(JoinPoint joinPoint, ResponseDo responseDo) {
        if (responseDo.success()) {
            String officialActivityId = (String) joinPoint.getArgs()[0];
            String userId = (String) joinPoint.getArgs()[1];

            StatisticOfficialActivity statisticOfficialActivity = new StatisticOfficialActivity();
            statisticOfficialActivity.setUserId(userId);
            statisticOfficialActivity.setOfficialActivityId(officialActivityId);
            statisticOfficialActivity.setEvent(StatisticOfficialActivity.OFFICIAL_ACTIVITY_JOIN);
            statisticOfficialActivity.setCount(1);
            statisticOfficialActivity.recordTime(DateUtil.getTime());
            statisticOfficialActivityDao.save(statisticOfficialActivity);
        }
    }
}
