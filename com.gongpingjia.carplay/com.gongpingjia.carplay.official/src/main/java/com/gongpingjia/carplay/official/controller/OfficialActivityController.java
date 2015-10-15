package com.gongpingjia.carplay.official.controller;

import com.alibaba.fastjson.JSONArray;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
            checker.checkUserInfo(userId, token);

            if (CommonUtil.isEmpty(json, Arrays.asList("title", "destination", "cover", "instruction", "description", "price", "subsidyPrice", "limitType"))) {
                throw new ApiException("参数输入不全 请检查参数");
            }

            OfficialActivity activity = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);
            activity.setOfficialActivityId(null);
            activity.setUserId(userId);
            activity.setDeleteFlag(false);
            activity.setNowJoinNum(0);
            activity.setMaleNum(0);
            activity.setFemaleNum(0);
            return officialActivityService.registerActivity(activity, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/official/activity/list", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo getActivityList(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        try {
            checker.checkUserInfo(userId, token);
            return officialActivityService.getActivityList(userId, json);
        } catch (ApiException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "official/activity/onFlag", method = RequestMethod.GET)
    public ResponseDo changeOnFlag(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId) {
        try {
            checker.checkUserInfo(userId, token);
            return officialActivityService.changeActivityOnFlag(officialActivityId);
        } catch (ApiException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/official/activity/update", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo updateActivity(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId, @RequestBody JSONObject json) {
        try {
            checker.checkUserInfo(userId, token);
            return officialActivityService.updateActivity(officialActivityId, json);
        } catch (ApiException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "official/activity/info", method = RequestMethod.GET)
    public ResponseDo getInfo(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestParam("officialActivityId") String officialActivityId) {
        try {
            checker.checkUserInfo(userId, token);
            return officialActivityService.getActivity(officialActivityId);
        } catch (ApiException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/official/activity/deleteIds", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo deleteOfficialActivities(@RequestParam("userId") String userId, @RequestParam("token") String token,@RequestBody JSONArray json) {
        try {
            checker.checkUserInfo(userId,token);
            if (json == null || json.size() == 0) {
                return ResponseDo.buildFailureResponse("请选择需要删除的项");
            }
            List<String> ids = new ArrayList<>(json.size());
            Iterator<Object> iterator = json.iterator();
            while (iterator.hasNext()) {
                ids.add((String)iterator.next());
            }
            return officialActivityService.deleteActivities(ids);
        } catch (ApiException e) {
            LOG.error(e.getLocalizedMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}
