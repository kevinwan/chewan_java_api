package com.gongpingjia.carplay.statistic.aop;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.statistic.StatisticDriverAuthDao;
import com.gongpingjia.carplay.dao.statistic.StatisticUserRegisterDao;
import com.gongpingjia.carplay.entity.statistic.StatisticDriverAuth;
import com.gongpingjia.carplay.entity.statistic.StatisticUserRegister;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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
        userRegister.setEvent(StatisticUserRegister.USER_REGISTER_SUCCESS);
        userRegisterDao.save(userRegister);
    }


    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.AuthenticationService.licenseAuthenticationApply(..))",
            returning = "returnValue")
    public void driverAuthentication(JoinPoint jp, Object returnValue) {
        if (!isReturnSuccess(returnValue)) {
            LOG.debug("Return not success");
            return;
        }

        LOG.info("== driverAuthentication AOP ");
        StatisticDriverAuth driverAuth = new StatisticDriverAuth();
        driverAuth.setCount(1);
        driverAuth.recordTime(DateUtil.getTime());
        driverAuth.setEvent(StatisticDriverAuth.AUTHENTICATION);

        if (jp.getArgs().length == 3) {
            //第三个参数为userId
            driverAuth.setUserId(jp.getArgs()[2].toString());
        }
        statisticDriverAuthDao.save(driverAuth);
    }

    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.UploadService.uploadDrivingLicensePhoto(..))",
            returning = "returnValue")
    public void drivingLicenseAuth(JoinPoint jp, Object returnValue) {
        if (!isReturnSuccess(returnValue)) {
            LOG.debug("Return not success");
            return;
        }

        LOG.debug("drivingLicenseAuth upload driving license");
        StatisticDriverAuth drivingLicense = new StatisticDriverAuth();
        drivingLicense.setCount(1);
        drivingLicense.recordTime(DateUtil.getTime());
        drivingLicense.setEvent(StatisticDriverAuth.DRIVING_LICENSE);
        if (jp.getArgs().length == 3) {
            //第一个参数为userId
            drivingLicense.setUserId(jp.getArgs()[0].toString());
        }
        statisticDriverAuthDao.save(drivingLicense);
    }

    @AfterReturning(value = "execution(* com.gongpingjia.carplay.service.UploadService.uploadDriverLicensePhoto(..))",
            returning = "returnValue")
    public void driverLicenseAuth(JoinPoint jp, Object returnValue) {
        if (!isReturnSuccess(returnValue)) {
            LOG.debug("Return not success");
            return;
        }

        LOG.debug("driverLicenseAuth upload driver license");
        StatisticDriverAuth driverLicense = new StatisticDriverAuth();
        driverLicense.setCount(1);
        driverLicense.recordTime(DateUtil.getTime());
        driverLicense.setEvent(StatisticDriverAuth.DRIVER_LICENSE);
        if (jp.getArgs().length == 3) {
            //第一个参数为userId
            driverLicense.setUserId(jp.getArgs()[0].toString());
        }
        statisticDriverAuthDao.save(driverLicense);
    }
}
