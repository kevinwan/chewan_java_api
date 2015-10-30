package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticDriverAuthDao;
import com.gongpingjia.carplay.dao.statistic.StatisticUserRegisterDao;
import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;
import com.gongpingjia.carplay.entity.statistic.StatisticUserRegister;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 123 on 2015/10/29.
 * user 相关的切面
 */
@Aspect
@Service
public class UserAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(UserAdvice.class);

    @Autowired
    private StatisticUserRegisterDao userRegisterDao;

    @Autowired
    private StatisticDriverAuthDao statisticDriverAuthDao;

    /**
     * 检查返回对象是否返回成功
     *
     * @param returnValue 返回对象
     * @return 成功返回true，否则返回false
     */
    private boolean isReturnSuccess(Object returnValue) {
        if (returnValue instanceof ResponseDo) {
            ResponseDo result = (ResponseDo) returnValue;
            return result.success();
        }
        return false;
    }

    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.UserService.register(..))", returning = "returnValue")
    public void userRegisterSuccess(Object returnValue) {
        LOG.info("== userRegisterSuccess AOP");
        if (!isReturnSuccess(returnValue)) {
            LOG.debug("Return not success");
            return;
        }

        LOG.debug("Record user register when success");
        StatisticUserRegister userRegister = new StatisticUserRegister();
        userRegister.setCount(1);
        userRegister.recordTime(DateUtil.getTime());
        userRegister.setEvent(StatisticConstants.UserStatistic.USER_REGISTER_SUCCESS);
        userRegisterDao.save(userRegister);
    }


//    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.AunthenticationService.licenseAuthenticationApply(..)) && args(json, token, userId)",
//            argNames = "userId", returning = "returnValue")
//    public void driverAuthentication(Object returnValue, String userId) {
//        if (!isReturnSuccess(returnValue)) {
//            LOG.debug("Return not success");
//            return;
//        }
//
//        LOG.info("== driverAuthentication AOP ");
//        StatisticDriverAuth driverAuth = new StatisticDriverAuth();
//        driverAuth.setCount(1);
//        driverAuth.recordTime(DateUtil.getTime());
//        driverAuth.setUserId(userId);
//        statisticDriverAuthDao.save(driverAuth);
//    }


}
