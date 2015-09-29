package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.ActivityIntention;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyongyu on 2015/9/22.
 */
@RestController
public class ActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ParameterChecker parameterChecker;

    @Autowired
    private ActivityService activityService;

    /**
     * 发布意向活动，注册活动
     * 用户id
     *
     * @param userId
     * <p/>
     * 用户token
     * @param token
     * <p/>
     * 活动信息对应的json
     * @param jsonObject
     */
    @RequestMapping(value = "/activity/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam(value = "userId") String userId, @RequestParam(value = "token") String token,
                                       @RequestBody JSONObject jsonObject) {
        LOG.debug("activity/register begin");
        try {
            Activity activity = (Activity) JSONObject.toBean(jsonObject, Activity.class);
            return activityService.activityRegister(userId, token, activity);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取活动信息
     * <p/>
     * 活动主键
     *
     * @param activityId
     * @param userId
     * @param token
     */
    @RequestMapping(value = "/activity/{activityId}/info", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            ResponseDo responseDo = activityService.getActivityInfo(userId, token, activityId);
            return responseDo;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取附近的活动
     * @param request
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public ResponseDo getNearByActivityList(HttpServletRequest request, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            return activityService.getNearActivityList(initTransListParam(request), request);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 约她 申请加入活动
     *
     * @param activityId 活动Id
     * @param userId     申请人
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/activity/{activityId}/join", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo sendAppointment(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId,
                                      @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.debug("activity/ {} /join begin", activityId);
        try {
            ActivityIntention activityIntention = (ActivityIntention) JSONObject.toBean(json, Activity.class);
            return activityService.sendAppointment(activityId, userId, token, activityIntention);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 处理用户 邀请加入
     *
     * @param appointmentId 申请Id
     * @param userId        用户Id
     * @param token         用户会话Token
     * @param json          请求参数
     * @return 返回处理结果
     */
    @RequestMapping(value = "/application/{appointmentId}/process", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo processJoinApplication(@PathVariable("appointmentId") String appointmentId, @RequestParam("userId") String userId,
                                             @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.debug("/application/{applicationId}/process");
        try {
            boolean accept = (Boolean) json.get("accept");
            parameterChecker.checkUserInfo(userId, token);
            return activityService.applyJoinActivity(appointmentId, userId, accept);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
        }
        return ResponseDo.buildSuccessResponse();
    }

    /**
     * type=$type&pay=$pay&province=$province&city=$city&district=$district&street=$street&transfer=$transfer&ignore=$ignore&limit=$limit&longitude=$longitude&latitude=$latitude &maxDistance=$maxDistance
     */
    private Map<String, String> initTransListParam(HttpServletRequest request) {
        Map<String, String> keyTranMap = new HashMap<>();
        keyTranMap.put("pay", "pay");
        keyTranMap.put("type", "type");
        keyTranMap.put("province", "establish.province");
        keyTranMap.put("city", "establish.city");
        keyTranMap.put("district", "establish.district");
        keyTranMap.put("street", "establish.street");
        return keyTranMap;
    }


}
