package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.service.ActivityService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by heyongyu on 2015/9/22.
 */
@Controller
public class ActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);

    /**
     * 注册活动
     * 用户id
     * @param userId
     *
     * 用户token
     * @param token
     *
     * 活动信息对应的json
     * @param activity
     */
    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/activity/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    @ResponseBody
    public ResponseDo registerActivity(@RequestParam(value = "userId") String userId, @RequestParam(value = "token") String token, @RequestBody Activity activity) {
        LOG.debug("activity/register begin");
        try {
            return activityService.activityRegister(userId, token, activity);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取活动信息
     *
     * 活动主键
     * @param activityId
     *
     * @param  userId
     *
     * @param token
     *
     */
    @RequestMapping(value = "/activity/${activityId}/info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            return activityService.getActivityInfo(userId, token, activityId);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
