package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import com.gongpingjia.carplay.official.service.impl.OfficialParameterChecker;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动
 */
@RestController
public class OfficialActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialActivityController.class);

    @Autowired
    private OfficialActivityService officialActivityService;

    @Autowired
    private ParameterChecker checker;

    @Autowired
    private OfficialParameterChecker officialParameterChecker;

    /**
     * 创建官方活动
     *
     * @param userId 用户Id
     * @param token  用户会话token
     * @param json   创建活动的JSON对象
     * @return 返回创建结果
     */
    @RequestMapping(value = "/official/activity/register", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo registerActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.debug("begin registerActivity by official operation, userId:{}", userId);

        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return officialActivityService.registerActivity(json, userId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/official/activity/list", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo getActivityList(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        try {
            LOG.debug("official/activity/list");
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return officialActivityService.getActivityList(userId, json);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "official/activity/onFlag", method = RequestMethod.GET)
    public ResponseDo changeOnFlag(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId) {
        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return officialActivityService.changeActivityOnFlag(officialActivityId);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/official/activity/update", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo updateActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId, @RequestBody JSONObject json) {
        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return officialActivityService.updateActivity(officialActivityId, json, userId);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "official/activity/info", method = RequestMethod.GET)
    public ResponseDo getInfo(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId) {
        try {
            officialParameterChecker.checkAdminUserInfo(userId, token);

            return officialActivityService.getActivity(officialActivityId);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/official/activity/deleteIds", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo deleteOfficialActivities(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody ArrayList<String> ids) {
        try {

            officialParameterChecker.checkAdminUserInfo(userId, token);

            if (ids == null || ids.size() == 0) {
                return ResponseDo.buildFailureResponse("请选择需要删除的项");
            }

            return officialActivityService.deleteActivities(ids);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
