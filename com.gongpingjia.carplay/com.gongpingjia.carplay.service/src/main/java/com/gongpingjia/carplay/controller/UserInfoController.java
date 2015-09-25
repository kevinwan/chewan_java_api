package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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
            if (CommonUtil.isEmpty(json, Arrays.asList("nickname", "gender", "birthday", "avatar"))) {
                throw new ApiException("输入参数错误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

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

    /**
     * 忘记密码
     *
     * @param json 参数列表
     * @return 忘记密码结果
     */
    @RequestMapping(value = "/user/password", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo forgetPassword(@RequestBody JSONObject json) {

        LOG.debug("forgetPassword is called, request parameter produce:");

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("phone", "code", "password"))) {
                throw new ApiException("输入参数有误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

            return userService.forgetPassword(user, json.getString("code"));
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 三方登录
     *
     * @return 返回登录结果
     */
    @RequestMapping(value = "/sns/login", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo snsLogin(@RequestBody JSONObject json) {
        LOG.info("snsLogin begin");

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("uid", "nickname", "url", "channel", "sign"))) {
                throw new ApiException("输入参数有误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

            return userService.snsLogin(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 个人详情，获取个人信息，当beViewUser与viewUser相同，表示查看自己的个人信息，否则为查看他的详情
     *
     * @param beViewedUser 被查看的用户
     * @param viewUser     当前查看的人
     * @param token        当前查看的人的会话Token
     * @return 返回个人详情结果
     */
    @RequestMapping(value = "/user/{beViewedUser}/info", method = RequestMethod.GET)
    public ResponseDo getUserInfo(@PathVariable("beViewedUser") String beViewedUser,
                                  @RequestParam("viewUser") String viewUser, @RequestParam("token") String token) {
        LOG.info("Begin get user information, user:{}", beViewedUser);

        try {
            return userService.getUserInfo(beViewedUser, viewUser, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取我的约会信息
     */
    @RequestMapping(value = "/user/{userId}/appointment", method = RequestMethod.GET)
    public ResponseDo getAppointment(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam(value = "status", defaultValue = "") String status,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                     @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        LOG.debug("/user/{}/appointment", userId);
        try {
            return userService.getAppointment(userId, token, status, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/user/{userId}/view/history")
    public ResponseDo getViewHistory(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestParam("limit") int limit, @RequestParam("ignore") int ignore) {
        LOG.debug("/user/{}/view/history", userId);
        try {
            return userService.getViewHistory(userId, token, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/user/{userId}/auth/history")
    public ResponseDo getAuthHistory(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam("limit") int limit, @RequestParam("ignore") int ignore) {

        LOG.debug("/user/{}/auth/history", userId);
        try {
            return userService.getAuthHistory(userId, token, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/user/{userId}/info", method = RequestMethod.POST)
    public ResponseDo alterUserInfo(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestBody User user) {
        LOG.debug("alter user information");

        try {
            return userService.alterUserInfo(userId, token, user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}