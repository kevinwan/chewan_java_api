package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticUserRegisterDao;
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

    @Before(value = "execution(* com.gongpingjia.carplay.controller.UserInfoController.register(..))")
    public void userRegister() {
        LOG.debug("====================Catch user register, return:{}=====", "123");

        StatisticUserRegister userRegister = new StatisticUserRegister();
        userRegister.setCount(1);
        userRegister.recordTime(DateUtil.getTime());
        userRegister.setEvent(StatisticConstants.UserStatistic.USER_REGISTER);
        userRegisterDao.save(userRegister);
        LOG.debug("Finished record user register");
    }
}
