package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.official.service.impl.OfficialParameterChecker;
import com.gongpingjia.carplay.service.ActivityService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;

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
     * 查询活动信息；
     *
     * @param userId
     * @param token
     * @param request
     * @return
     */
    @RequestMapping(value = "/official/userActivity/list", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, HttpServletRequest request) {
        LOG.debug("begin /official/userActivity/list userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return activityService.getUserActivityList(request);

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
     * @param json
     * @return
     */
    @RequestMapping(value = "/official/userActivity/deleteIds", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo deleteActivities(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONArray json) {
        LOG.debug("begin /official/userActivity/deleteIds userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);
            ArrayList<String> ids = new ArrayList<>();
            Iterator iterator = json.iterator();
            while (iterator.hasNext()) {
                ids.add((String) iterator.next());
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
