package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.service.SubscribeService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/9/24.
 * <p>
 * 关注相关的操作
 */
@RestController
public class SubscribeController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private SubscribeService service;

    /**
     * 获取用户的关注信息
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(value = "/user/{userId}/subscribe", method = RequestMethod.GET)
    public ResponseDo getUserSubscribes(@PathVariable("userId") String userId, @RequestParam("token") String token) {
        LOG.info("getUserSubscribes begin");

        try {
            return service.getUserSubscribeInfo(userId, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 关注其他用户
     */
    @RequestMapping(value = "user/{userId}/listen", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo payAttention(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.info("user/{}/payAttention", userId);
        if (CommonUtil.isEmpty(json, "targetUserId")) {
            LOG.warn("Input parameter targetUserId is empty");
            return ResponseDo.buildFailureResponse("输入参数错误");
        }

        Subscriber userSubscription = new Subscriber();
        userSubscription.setFromUser(userId);
        userSubscription.setToUser(json.getString("targetUserId"));

        try {
            return service.payAttention(userSubscription, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 取消关注其他用户
     */
    @RequestMapping(value = "/user/{userId}/unlisten", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo unPayAttention(@PathVariable(value = "userId") String userId,
                                     @RequestParam(value = "token") String token, @RequestBody JSONObject json) {

        LOG.debug("userListen is called, request parameter produce:");

        if (CommonUtil.isEmpty(json, "targetUserId")) {
            LOG.warn("Input parameter targetUserId is empty");
            return ResponseDo.buildFailureResponse("输入参数错误");
        }

        Subscriber userSubscription = new Subscriber();
        userSubscription.setFromUser(userId);
        userSubscription.setToUser(json.getString("targetUserId"));

        try {
            return service.unPayAttention(userSubscription, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 别人关注我的历史
     * */
    @RequestMapping(value = "/user/{userId}/subscribe/history", method = RequestMethod.GET)
    public ResponseDo getUserSubscribedHistory(@PathVariable("userId") String userId, @RequestParam("token") String token) {
        LOG.info("getUserSubscribes begin");

        try {
            return service.getUserSubscribedHistory(userId, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}
