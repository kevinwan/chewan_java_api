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
     *
     * 活动主键
     * @param activityId
     *
     * @param  userId
     *
     * @param token
     *
     */
    @RequestMapping(value = "/activity/{activityId}/info", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId, @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            return activityService.getActivityInfo(userId, token, activityId);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * @param request     request 中需要封装在initTransListParam中的基本信息
     *                    pay 不是必填    请我 我请客 AA制度；                   需要进行转换；//TODO
     *                    type 类型；
     *                    province
     *                    city
     *                    district
     *                    street
     *
     *@param userId 用户ID
     *@param token 用户token
     */
    @RequestMapping(value = "/activity/list",method = RequestMethod.GET)
    public ResponseDo getNearByActivityList(HttpServletRequest request,@RequestParam("userId")String userId,@RequestParam("token")String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            return activityService.getNearActivityList(initTransListParam(request), request);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/activity/{activityId}/join",method = RequestMethod.POST,headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo joinActivity(@PathVariable("activityId")String activityId,@RequestParam("userId")String userId,@RequestParam("token")String token){
        LOG.debug("activity/{activityId}/info begin");
        return null;
    }

    /**
     * type=$type&pay=$pay&province=$province&city=$city&district=$district&street=$street&transfer=$transfer&ignore=$ignore&limit=$limit&longitude=$longitude&latitude=$latitude &maxDistance=$maxDistance
     */
    private Map<String, String> initTransListParam(HttpServletRequest request) {
        Map<String, String> keyTranMap = new HashMap<>();
        keyTranMap.put("pay","pay");
        keyTranMap.put("type","type");
        keyTranMap.put("province", "establish.province");
        keyTranMap.put("city", "establish.city");
        keyTranMap.put("district", "establish.district");
        keyTranMap.put("street", "establish.street");
        return keyTranMap;
    }

}
