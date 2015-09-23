package com.gongpingjia.carplay.service;

import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.user.User;

@Service
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return 注册结果
     * @throws ApiException
     */
    ResponseDo register(User user) throws ApiException;


    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 登录结果
     * @throws ApiException
     */
    ResponseDo loginUser(User user) throws ApiException;


    /**
     * 检查注册用户的参数是否正确
     *
     * @param user    用户信息
     * @param request 请求参数
     * @throws ApiException 业务异常
     */
    void checkRegisterParameters(User user, JSONObject request) throws ApiException;

    /**
     * 忘记密码
     *
     * @param user 用户信息
     * @param code 手机验证码
     * @return 业务结果
     * @throws ApiException
     */
    @Transactional(rollbackFor = Exception.class)
    ResponseDo forgetPassword(User user, String code) throws ApiException;

}