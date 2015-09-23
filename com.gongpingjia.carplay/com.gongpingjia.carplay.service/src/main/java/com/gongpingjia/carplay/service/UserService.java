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
     * @param user
     * @return
     * @throws ApiException
     */
    @Transactional(rollbackFor = Exception.class)
    ResponseDo register(User user) throws ApiException;

    /**
     * 检查注册用户的参数是否正确
     *
     * @param user    用户信息
     * @param request 请求参数
     * @throws ApiException 业务异常
     */
    void checkRegisterParameters(User user, JSONObject request) throws ApiException;
}