package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.service.OfficialService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * Created by licheng on 2015/9/28.
 * 官方活动相关操作
 */
@RestController
public class OfficialController {

    private static Logger LOG = LoggerFactory.getLogger(OfficialController.class);

    @Autowired
    private OfficialService officialService;

    @Autowired
    private ParameterChecker parameterChecker;

    /**
     * 申请加入到官方活动中
     *
     * @param activityId 官方活动活动Id
     * @param userId     参加人的Id
     * @param token      会话Token
     * @return 返回加入结果信息
     */
    @RequestMapping(value = "/official/activity/{activityId}/join", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo joinActivity(@PathVariable("activityId") String activityId,
                                   @RequestParam("userId") String userId, @RequestParam("token") String token) {
        try {
            parameterChecker.checkUserInfo(userId, token);

            return officialService.applyJoinActivity(activityId, userId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/official/activity/{activityId}/quitJoin", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo quitJoinActivity(@PathVariable("activityId") String activityId,
                                       @RequestParam("userId") String userId, @RequestParam("token") String token) {
        try {
            parameterChecker.checkUserInfo(userId, token);

            return officialService.applyJoinActivity(activityId, userId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取官方活动详细信息
     *
     * @param id
     * @param userId 用户Id
     * @param token  用户会话Token
     * @return 返回结果信息
     */
    @RequestMapping(value = "/official/activity/{id}/info", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("id") String id, @RequestParam(value = "idType", required = false, defaultValue = "0") Integer idType,
                                      @RequestParam(value = "userId", required = false, defaultValue = "") String userId, @RequestParam(value = "token", required = false) String token, HttpServletRequest request,
                                      @RequestParam(value = "longitude", required = false, defaultValue = "") String longitude, @RequestParam(value = "latitude", required = false, defaultValue = "") String latitude) {

        try {
            if (StringUtils.isNotEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }
            return officialService.getActivityInfo(request, id, idType, userId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    @RequestMapping(value = "/official/activity/{id}/members", method = RequestMethod.GET)
    public ResponseDo getActivityMembersInfo(@PathVariable("id") String id, @RequestParam(value = "idType", required = false, defaultValue = "0") Integer idType,
                                             @RequestParam(value = "userId", required = false, defaultValue = "") String userId, @RequestParam(value = "token", required = false) String token,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                             @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
                                             @RequestParam(value = "longitude", defaultValue = "-1") Double longitude,
                                             @RequestParam(value = "latitude", defaultValue = "-1") Double latitude) {
        try {
            LOG.info("Input id:{}, idType:{}", id, idType);
            if (StringUtils.isNotEmpty(userId)) {
                parameterChecker.checkUserInfo(userId, token);
            }
            Landmark landmark = null;
            if (longitude != -1D && latitude != -1D) {
                landmark = new Landmark();
                landmark.setLatitude(latitude);
                landmark.setLongitude(longitude);
                if (!landmark.correct()) {
                    LOG.warn("Input parameter landmark error, latitude:{}, longitude:{}", latitude, longitude);
                    throw new ApiException("输入参数错误");
                }
            }
            return officialService.getActivityPageMemberInfo(id, idType, userId, ignore, limit, landmark);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取官方活动列表
     *
     * @param userId   用户Id
     * @param token    用户会话Token
     * @param province 省份
     * @param city     市
     * @param limit    查询条数
     * @param ignore   忽略条数
     * @return 返回查询结果
     */
    @RequestMapping(value = "/official/activity/list", method = RequestMethod.GET)
    public ResponseDo listActivities(@RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "token", required = false) String token,
                                     @RequestParam(value = "province", required = false) String province, @RequestParam(value = "city", required = false) String city,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                     @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        try {
//            parameterChecker.checkUserInfo(userId, token);

            Address address = new Address();
            address.setCity(city);
            address.setProvince(province);

            return officialService.getActivityList(address, limit, ignore);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 约她同去参加官方活动
     *
     * @param activityId
     * @param userId
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/official/activity/{activityId}/invite", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo inviteUserTogether(@PathVariable("activityId") String activityId,
                                         @RequestParam("userId") String userId, @RequestParam("token") String token,
                                         @RequestBody JSONObject json) {

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("invitedUserId", "transfer"))) {
                throw new ApiException("输入参数有误");
            }

            parameterChecker.checkUserInfo(userId, token);

            String invitedUserId = json.getString("invitedUserId");
            Boolean transfer = json.getBoolean("transfer");
            String message = "";
            if (json.containsKey("message")) {
                message = json.getString("message");
            }
            parameterChecker.isUserExist(invitedUserId);

            return officialService.inviteUserTogether(activityId, userId, invitedUserId, transfer, message);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取省市区信息接口
     *
     * @param parentId 父信息的Id， 如果获取省份信息，传0
     * @return 返回父ID对应的所有子区域的信息
     */
    @RequestMapping(value = "/area/list", method = RequestMethod.GET)
    public ResponseDo getAreaInfos(@RequestParam Integer parentId) {
        LOG.info("Query area list information, parentId:{}", parentId);

        return officialService.getAreaList(parentId);
    }

    @RequestMapping(value = "/areaRange/info", method = RequestMethod.GET)
    public ResponseDo getAreaRangeInfo(@RequestParam Integer code) {
        LOG.info("get area range info");

        try {
            return officialService.getAreaRangeInfo(code);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/official/activity/{activityId}/unregisterJoin", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo unregisterJoin(@PathVariable("activityId") String activityId, @RequestBody JSONObject json) {
        LOG.info("/official/activity/{}/unregisterJoin was called", activityId);
        if (CommonUtil.isEmpty(json, "phone")) {
            LOG.warn("User input phone number is empty, ignore");
            return ResponseDo.buildFailureResponse("请输入手机号");
        }

        String phone = json.getString("phone");
        if (!CommonUtil.isPhoneNumber(phone)) {
            LOG.warn("input phone number:{} is error", phone);
            return ResponseDo.buildFailureResponse("请输入正确的手机号码");
        }
        return officialService.unregisterRecordUserPhone(activityId, phone);
    }
}
