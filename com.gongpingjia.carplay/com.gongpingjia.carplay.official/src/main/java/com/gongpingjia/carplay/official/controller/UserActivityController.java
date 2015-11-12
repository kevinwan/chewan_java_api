package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.service.impl.OfficialParameterChecker;
import com.gongpingjia.carplay.service.ActivityService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 官方后台查看用户发布的活动信息
 */
@RestController
public class UserActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OfficialParameterChecker officialParameterChecker;


    /**
     * 后台 发布用户活动
     */
    @RequestMapping(value = "/official/userActivity/register", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json, HttpServletRequest request) {
        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("majorType", "type", "estabPoint", "establish", "transfer"))) {
                throw new ApiException("输入参数有误");
            }
            officialParameterChecker.checkAdminUserInfo(userId,token);

            Activity activity =  (Activity)JSONObject.toBean(json, Activity.class);

            Landmark landmark = activity.getEstabPoint();
            if (landmark == null || !landmark.correct()) {
                LOG.warn("Input parameter estabPoint error, landmark:{}", landmark);
                throw new ApiException("输入参数错误");
            }
            if (null != activity.getDestination()) {
                if (!activity.getDestPoint().correct()) {
                    LOG.warn("Input parameter destPoint error, landmark:{}", landmark);
                    throw new ApiException("输入参数错误");
                }
            }
            return activityService.activityRegister(userId,activity);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 查询活动信息；
     *
     * @param userId
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/official/userActivity/list", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo getActivityList(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.debug("begin /official/userActivity/list userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return activityService.getUserActivityList(json,userId);

        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 删除用户创建的 一些 非法 活动； 传入值 是 activityId 的 jsonArray;
     *
     * @param userId
     * @param token
     * @param ids
     * @return
     */
    @RequestMapping(value = "/official/userActivity/deleteIds", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo deleteActivities(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody ArrayList<String> ids)  {
        LOG.debug("begin /official/userActivity/deleteIds userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);
            if (null == ids || ids.isEmpty()) {
                throw new ApiException("请至少传一个需要删除的id");
            }
            return activityService.deleteUserActivities(ids);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 更新用户的 活动信息 ；
     *
     * @param userId
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/official/userActivity/update", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo updateActivity(@RequestParam("userId") String userId, @RequestParam("token") String token,@RequestParam("activityId")String activityId, @RequestBody JSONObject json) {
        LOG.debug("begin /official/userActivity/deleteIds userId:{}", userId);

        try {

            officialParameterChecker.checkAdminUserInfo(userId, token);
            return activityService.updateUserActivity(json, activityId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 查看用户活动信息
     */

    @RequestMapping(value = "/official/userActivity/view", method = RequestMethod.GET)
    public ResponseDo viewActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("activityId") String activityId) {

        LOG.debug("begin /official/userActivity/deleteIds userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);
            return activityService.viewUserActivity(activityId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}
