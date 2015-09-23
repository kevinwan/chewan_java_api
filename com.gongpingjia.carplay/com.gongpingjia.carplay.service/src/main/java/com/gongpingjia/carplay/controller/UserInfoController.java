package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.user.SnsInfo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class UserInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param json 参数列表
     * @return 注册结果
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo register(@RequestBody JSONObject json) {

        LOG.debug("register is called, request parameter produce:");
        try {
            // 检查必须参数是否为空
            if (CommonUtil.isEmpty(json, Arrays.asList("phone", "nickname", "gender", "birthday", "avatar"))) {
                throw new ApiException("输入参数错误");
            }

            User user = new User();
            user.setPhone(json.getString("phone"));
            user.setNickname(json.getString("nickname"));
            user.setGender(json.getString("gender"));
            user.setBirthday(json.getLong("birthday"));
            user.setAvatar(json.getString("avatar"));

            userService.checkRegisterParameters(user, json);

            return userService.register(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 登录
     *
     * @param user 参数列表
     * @return 登录结果
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo loginUser(@RequestBody User user) {
        LOG.debug("login is called, request parameter produce:");

        try {
            if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPhone())) {
                LOG.warn("Input parameters password or phone is empty");
                throw new ApiException("输入参数有误");
            }

            return userService.loginUser(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}