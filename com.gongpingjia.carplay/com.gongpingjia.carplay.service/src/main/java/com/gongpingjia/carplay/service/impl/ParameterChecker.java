package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import com.gongpingjia.carplay.entity.user.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 */
@Service("parameterChecker")
public class ParameterChecker {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterChecker.class);

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PhoneVerificationDao phoneVerificationDao;

    /**
     * 检查传入的参数userID和token的合格性，以及token是否过期
     *
     * @param userId 用户ID
     * @param token  会话token
     * @throws ApiException 如果参数错误则抛出异常
     */
    public void checkUserInfo(String userId, String token) throws ApiException {
        UserToken userToken = cacheManager.getUserToken(userId);
        if (userToken == null) {
            LOG.error("No user token exist in the system, userId:{}", userId);
            throw new ApiException("用户不存在");
        }

        if (!userToken.getToken().equals(token)) {
            LOG.error("User token is not response to userId:{} in the system, token:{}", userId, token);
            throw new ApiException("会话失效, 请重新登录");
        }

        if (userToken.getExpire() < DateUtil.getTime()) {
            LOG.error("User token is out of date, userId: {}", userId);
            throw new ApiException("口令已过期，请重新登录获取新口令");
        }
    }

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 用户存在返回true， 用户不存在返回false
     */
    public boolean isUserExist(String userId) {
        UserToken userToken = cacheManager.getUserToken(userId);
        if (userToken == null) {
            LOG.warn("No user token exist in the system, userId:{}", userId);
            return false;
        }
        return true;
    }

    /**
     * 检查参数是否为空，为空抛出异常
     *
     * @param paramName  参数名称，主要用于记录日志
     * @param paramValue 参数值
     * @throws ApiException 参数为空抛出业务异常
     */
    public void checkParameterEmpty(String paramName, String paramValue) throws ApiException {
        if (StringUtils.isEmpty(paramValue)) {
            LOG.error("Parameter {} is empty", paramName);
            throw new ApiException("输入参数有误");
        }
    }

    /**
     * 检查参数是否为空，为空抛出异常
     *
     * @param paramName   参数名称，主要用于记录日志
     * @param paramValues 参数值
     * @throws ApiException 参数为空抛出业务异常
     */
    public void checkParameterEmpty(String paramName, String[] paramValues) throws ApiException {
        if (paramValues == null || paramValues.length == 0) {
            LOG.error("Parameter {} is empty", paramName);
            throw new ApiException("输入参数有误");
        }
    }

    /**
     * 检查参数是否为Long类型
     *
     * @param paramName  参数名称
     * @param paramValue 参数值
     * @throws ApiException 业务异常信息,参数不为Long类型
     */
    public void checkParameterLongType(String paramName, String paramValue) throws ApiException {
        try {
            Long.valueOf(paramValue);
        } catch (NumberFormatException e) {
            LOG.warn("Paramter [{}={}] is not Long type", paramName, paramValue);
            LOG.warn(e.getMessage(), e);
            throw new ApiException("输入参数有误");
        }
    }

    /**
     * 检查参数是否为Integer类型
     *
     * @param paramName  参数名称
     * @param paramValue 参数值
     * @throws ApiException 业务异常信息,参数不为Long类型
     */
    public void checkParameterIntegerType(String paramName, String paramValue) throws ApiException {
        try {
            Integer.valueOf(paramValue);
        } catch (NumberFormatException e) {
            LOG.warn("Paramter [{}={}] is not Integer type", paramName, paramValue);
            LOG.warn(e.getMessage(), e);
            throw new ApiException("输入参数有误");
        }
    }

    /**
     * 检查参数是否为UUID类型，如果不是抛出"输入参数有误异常"
     *
     * @param paramName 参数名称
     * @param value     参数值
     * @throws ApiException 业务异常信息
     */
    public void checkParameterUUID(String paramName, String value) throws ApiException {
        if ((!CommonUtil.isUUID(value))) {
            LOG.error("Parameter {} is not correct format UUID string, paramValue:{}", paramName, value);
            throw new ApiException("输入参数有误");
        }
    }

    /**
     * 验证手机号和验证码是否匹配
     *
     * @param phone 手机号
     * @param code  验证码
     * @throws ApiException 不匹配抛出异常
     */
    public void checkPhoneVerifyCode(String phone, String code) throws ApiException {
        PhoneVerification phoneVerify = phoneVerificationDao.findOne(Query.query(Criteria.where("phone").is(phone)));
        if (phoneVerify == null) {
            LOG.warn("Phone number is not exist in the phone verification table");
            throw new ApiException("未能获取该手机的验证码");
        }

        if (!code.equals(phoneVerify.getCode())) {
            LOG.warn("Phone verify code is not corrected");
            throw new ApiException("验证码有误");
        }

        if (phoneVerify.getExpire() < DateUtil.getTime()) {
            LOG.warn("Phone verify code is expired, please re acquisition");
            throw new ApiException("该验证码已过期，请重新获取验证码");
        }
    }


    /**
     * 检查type是否在 typeList中；
     * @param type
     * @param typeList
     * @throws ApiException
     */
    public void checkTypeIsIn(String type, List<String> typeList) throws ApiException {
        if (null == type || type.equals("")) {
            LOG.warn("type is empty");
            throw new ApiException("type is empty");
        }
        boolean isIn = false;
        for (String typeItem : typeList) {
            if (type.equals(typeItem)) {
                isIn = true;
            }
        }
        if (!isIn) {
            LOG.error("type {} is not in the typeList", type);
            throw new ApiException("this type is illegal");
        }
    }
}
