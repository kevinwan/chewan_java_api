package com.gongpingjia.carplay.service.impl;

import java.util.*;

import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import com.gongpingjia.carplay.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.phone.MessageService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

import com.gongpingjia.carplay.service.PhoneService;

@Service
public class PhoneServiceImpl implements PhoneService {

    private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PhoneVerificationDao phoneVerificationDao;

//    @Autowired
//    private ParameterChecker checker;

    @Override
    public ResponseDo sendVerification(String phone, Integer type) throws ApiException {

        if (!CommonUtil.isPhoneNumber(phone)) {
            LOG.warn("Phone number is not correct format");
            throw new ApiException("不是有效的手机号");
        }

        List<User> users = getUserList(phone);

        if (type == 0) {
            // 注册流程
            if (users.size() > 0) {
                // 用户已经存在，手机号不能重复注册
                LOG.warn("Phone number is already registed");
                throw new ApiException("手机号已被注册");
            }
        } else if (type == 1) {
            // 忘记密码流程
            if (users.size() == 0) {
                LOG.warn("User is not exist");
                throw new ApiException("用户不存在");
            }
        } else {
            LOG.warn("Request parameter type is not 1 or 0");
            throw new ApiException("参数错误");
        }

        PhoneVerification phoneVerification = savePhoneVerification(phone);

        int dayMaxSendTimes = PropertiesUtil.getProperty("message.send.day.max.times", 4);
        if (phoneVerification.getSendTimes() != null && phoneVerification.getSendTimes() >= dayMaxSendTimes) {
            LOG.warn("Send message times has over the day max send times");
            throw new ApiException("今天验证码发送次数已经用完");
        }

        // 重新计算发送次数
        refreshPhoneVerification(phoneVerification);

        boolean sendResult = MessageService.sendMessage(phone, phoneVerification.getCode(),
                phoneVerification.getSendTimes());
        if (sendResult) {
            return ResponseDo.buildSuccessResponse();
        } else {
            LOG.error("Send message failure, phone:{}, verifyCode:{}", phone, phoneVerification.getCode());
            return ResponseDo.buildFailureResponse("验证码发送失败");
        }
    }

    private void refreshPhoneVerification(PhoneVerification phoneVerification) {
        Calendar calModify = Calendar.getInstance();
        calModify.setTime(phoneVerification.getModifyTime());

        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(DateUtil.getDate());

        if (calModify.get(Calendar.DAY_OF_YEAR) == calCurrent.get(Calendar.DAY_OF_YEAR)
                && calModify.get(Calendar.YEAR) == calCurrent.get(Calendar.YEAR)) {
            // 表示是同一天
            phoneVerification.setSendTimes(phoneVerification.getSendTimes() + 1);
        } else {
            // 不是同一天
            phoneVerification.setSendTimes(1);
        }
        phoneVerification.setModifyTime(DateUtil.getDate());
        phoneVerificationDao.update(phoneVerification.getId(), phoneVerification);
    }

    @Override
    public ResponseDo verify(String phone, String code, Integer type) throws ApiException {
        if (!CommonUtil.isPhoneNumber(phone)) {
            LOG.warn("Phone number is not correct format");
            throw new ApiException("不是有效的手机号");
        }

        if (type == 0) {
            // 只有当注册的时候去校验手机号是否已经被注册过
            List<User> userList = getUserList(phone);
            if (userList.size() > 0) {
                LOG.warn("User with phone number is already registed, phone: {}", phone);
                throw new ApiException("该用户已注册");
            }
        }

        //checker.checkPhoneVerifyCode(phone, code);

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 通过手机号获取用户信息
     *
     * @param phone 手机号
     * @return 用户列表信息
     */
    private List<User> getUserList(String phone) {
        return userDao.find(Query.query(Criteria.where("phone").is(phone)));
    }

    /**
     * 保存手机验证码到数据库中
     *
     * @param phone
     */
    private PhoneVerification savePhoneVerification(String phone) {
        // 更新数据库记录
        PhoneVerification phoneVerify = phoneVerificationDao.findOne(Query.query(Criteria.where("phone").is(phone)));
        if (phoneVerify == null) {
            // 不存在验证码
            phoneVerify = new PhoneVerification();
            phoneVerify.setPhone(phone);
            phoneVerify.setCode(CodeGenerator.generatorVerifyCode());

            phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
                    PropertiesUtil.getProperty("message.effective.seconds", 7200)));
            phoneVerify.setSendTimes(0);
            phoneVerify.setModifyTime(DateUtil.getDate());

            phoneVerificationDao.save(phoneVerify);
        } else {
            // 已经存在验证码
            if (DateUtil.getDate().after(phoneVerify.getExpire())) {
                LOG.debug("Exist phone verifyCode is out of date");
                phoneVerify.setCode(CodeGenerator.generatorVerifyCode());
                phoneVerify.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.SECOND,
                        PropertiesUtil.getProperty("message.effective.seconds", 7200)));
                //过期了需要设置次数
                phoneVerify.setSendTimes(0);
                phoneVerify.setModifyTime(DateUtil.getDate());

                phoneVerificationDao.update(phoneVerify.getId(), phoneVerify);
            }
        }

        return phoneVerify;
    }

}
