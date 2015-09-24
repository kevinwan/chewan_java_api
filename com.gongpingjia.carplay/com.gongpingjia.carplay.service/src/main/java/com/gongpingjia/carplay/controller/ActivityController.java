package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.service.ActivityService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by heyongyu on 2015/9/22.
 */
@RestController
public class ActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);

    /**
     * 注册活动
     * 用户id
     *
     * @param userId
     * <p>
     * 用户token
     * @param token
     * <p>
     * 活动信息对应的json
     * @param activity
     */
    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/activity/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam(value = "userId") String userId, @RequestParam(value = "token") String token, @RequestBody JSONObject jsonObject) {
        LOG.debug("activity/register begin");
        try {
            Activity activity = (Activity) JSONObject.toBean(jsonObject, Activity.class);
            return activityService.activityRegister(userId, token, activity);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            e.printStackTrace();
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取活动信息
     * <p>
     * 活动主键
     *
     * @param activityId
     * @param userId
     * @param token
     */
    @RequestMapping(value = "/activity/${activityId}/info", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            return activityService.getActivityInfo(userId, token, activityId);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public ResponseDo getNearByActivityList(HttpServletRequest request, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/list begin");
        try {
            return activityService.getNearActivityList(initTransParam(request), request);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * type=$type&pay=$pay&province=$province&city=$city&district=$district&street=$street&transfer=$transfer&ignore=$ignore&limit=$limit&longitude=$longitude&latitude=$latitude &maxDistance=$maxDistance
     */
    private Map<String, String> initTransParam(HttpServletRequest request) {
        Map<String, String> keyTranMap = new HashMap<>();
        keyTranMap.put("pay", "pay");
        keyTranMap.put("type", "type");
        keyTranMap.put("province", "establish.province");
        keyTranMap.put("city", "establish.city");
        keyTranMap.put("district", "establish.district");
        keyTranMap.put("street", "establish.street");
        return keyTranMap;
    }

    /**
     * 约她，发送约会申请
     */
    @RequestMapping(value = "/activity/{activityId}/appointment", method = RequestMethod.POST)
    public ResponseDo sendAppointment(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/ {} /join begin", activityId);
        try {
            return activityService.sendAppointment(activityId, userId, token);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}
