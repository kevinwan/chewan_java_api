package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import com.gongpingjia.carplay.service.util.ActivityQueryParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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
     * @param userId     <p/>
     *                   用户token
     * @param token      <p/>
     *                   活动信息对应的jsonn
     * @param jsonObject
     */
    @RequestMapping(value = "/activity/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam(value = "userId") String userId, @RequestParam(value = "token") String token,
                                       @RequestBody JSONObject jsonObject) {
        LOG.debug("activity/register begin, parameter:{}", jsonObject);
        try {
            if (CommonUtil.isEmpty(jsonObject, Arrays.asList("majorType", "type", "estabPoint", "establish", "transfer"))) {
                throw new ApiException("输入参数有误");
            }

            parameterChecker.checkUserInfo(userId, token);

            LOG.debug("json string is:" + jsonObject.toString());

            //检查 type pay 是否在合法的参数范围内
            //parameterChecker.checkTypeIsIn(jsonObject.getString("type"), Constants.ActivityType.TYPE_LIST);
            //parameterChecker.checkTypeIsIn(jsonObject.getString("pay"), Constants.ActivityPayType.TYPE_LIST);

            Activity activity = (Activity) JSONObject.toBean(jsonObject, Activity.class);

            Landmark landmark = activity.getEstabPoint();
            if (landmark == null || !landmark.correct()) {
                LOG.warn("Input parameter estabPoint error, landmark:{}", landmark);
                throw new ApiException("输入参数错误");
            }

            return activityService.activityRegister(userId, activity);
        } catch (ApiException e) {
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
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId,
                                      @RequestParam(value = "userId", required = false) String userId,
                                      @RequestParam(value = "token", required = false) String token,
                                      @RequestParam(value = "longitude", required = false) Double longitude,
                                      @RequestParam(value = "latitude", required = false) Double latitude) {
        LOG.debug("activity/{}/info begin", activityId);
        try {
            if (StringUtils.isNotEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }

            Landmark landmark = new Landmark();
            landmark.setLatitude(latitude);
            landmark.setLongitude(longitude);
            return activityService.getActivityInfo(userId, activityId, landmark);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取附近的活动, 匹配约会信息
     *
     * @return
     */
    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    public ResponseDo getNearByActivityList(HttpServletRequest request,
                                            @RequestParam(value = "userId", required = false) String userId,
                                            @RequestParam(value = "token", required = false) String token,
                                            @RequestParam(value = "majorType", required = false) String majorType,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "pay", required = false) String pay,
                                            @RequestParam(value = "gender", required = false) String gender,
                                            @RequestParam(value = "transfer", required = false) Boolean transfer,
                                            @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
                                            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                            @RequestParam(value = "longitude") Double longitude,
                                            @RequestParam(value = "latitude") Double latitude,
                                            @RequestParam(value = "province", required = false) String province,
                                            @RequestParam(value = "city", required = false) String city,
                                            @RequestParam(value = "district", required = false) String district) {
        LOG.info("Begin get nearby activities");

        ActivityQueryParam param = new ActivityQueryParam();
        param.setUserId(userId);
        param.setToken(token);
        param.setType(type);
        param.setMajorType(majorType);
        param.setPay(pay);
        param.setGender(gender);
        param.setTransfer(transfer);
        param.setIgnore(ignore);
        param.setLimit(limit);
        param.setLongitude(longitude);
        param.setLatitude(latitude);
        param.setProvince(province);
        param.setCity(city);
        param.setDistrict(district);

        try {
            if (!StringUtils.isEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }
            return activityService.getNearByActivityList(request, param);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }

    }

    /**
     * 获取附近的活动, 匹配约会的总数量，转圈等待
     *
     * @return
     */
    @RequestMapping(value = "/activity/count", method = RequestMethod.GET)
    public ResponseDo getNearByActivityCount(@RequestParam(value = "userId", required = false) String userId,
                                             @RequestParam(value = "token", required = false) String token,
                                             @RequestParam(value = "majorType", required = false) String majorType,
                                             @RequestParam(value = "type", required = false) String type,
                                             @RequestParam(value = "pay", required = false) String pay,
                                             @RequestParam(value = "gender", required = false) String gender,
                                             @RequestParam(value = "transfer", required = false) Boolean transfer,
                                             @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                             @RequestParam(value = "longitude") Double longitude,
                                             @RequestParam(value = "latitude") Double latitude,
                                             @RequestParam(value = "province", required = false) String province,
                                             @RequestParam(value = "city", required = false) String city,
                                             @RequestParam(value = "district", required = false) String district) {
        LOG.info("Begin get nearby activities");
        ActivityQueryParam param = new ActivityQueryParam();
        param.setUserId(userId);
        param.setToken(token);
        param.setType(type);
        param.setMajorType(majorType);
        param.setPay(pay);
        param.setGender(gender);
        param.setTransfer(transfer);
        param.setIgnore(ignore);
        param.setLimit(limit);
        param.setLongitude(longitude);
        param.setLatitude(latitude);
        param.setProvince(province);
        param.setCity(city);
        param.setDistrict(district);

        try {
            if (!StringUtils.isEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }
            return activityService.getNearByActivityCount(param);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取附近的活动, 匹配约会信息
     *
     * @return
     */
    @RequestMapping(value = "/activity/randomLook", method = RequestMethod.GET)
    public ResponseDo getRandomActivities(@RequestParam(value = "userId", required = false) String userId,
                                          @RequestParam(value = "token", required = false) String token,
                                          @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                          @RequestParam(value = "longitude") Double longitude,
                                          @RequestParam(value = "latitude") Double latitude) {
        LOG.info("Begin get nearby activities, longitude:{}, latitude:{}", longitude, latitude);
        ActivityQueryParam param = new ActivityQueryParam();
        param.setUserId(userId);
        param.setToken(token);
        param.setLimit(limit);
        param.setLongitude(longitude);
        param.setLatitude(latitude);

        try {
            if (!StringUtils.isEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }

            return activityService.getRandomActivities(param);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
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
        LOG.debug("activity/ {} /join begin, json:{}", activityId, json);
        try {
            parameterChecker.checkUserInfo(userId, token);
            Appointment appointment = (Appointment) JSONObject.toBean(json, Appointment.class);
            return activityService.sendAppointment(activityId, userId, appointment);
        } catch (ApiException e) {
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
            if (CommonUtil.isEmpty(json, "accept")) {
                throw new ApiException("输入参数错误");
            }
            parameterChecker.checkUserInfo(userId, token);
            boolean accept = json.getBoolean("accept");
            return activityService.processAppointment(appointmentId, userId, accept);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取当前用户的附近推送的消息列表
     *
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(value = "/activity/pushInfo", method = RequestMethod.GET)
    public ResponseDo getPushActivities(@RequestParam("userId") String userId, @RequestParam("token") String token, HttpServletRequest request,
                                        @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                        @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        LOG.debug("/activity/pushInfo, userId:{}", userId);
        try {
            parameterChecker.checkUserInfo(userId, token);

            return activityService.getActivityPushInfos(request, userId, limit, ignore);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
