package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.service.OfficialService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by licheng on 2015/9/28.
 * 官方活动相关操作
 */
@RestController
public class OfficialController {

    private static Logger LOG = Logger.getLogger(OfficialController.class);

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
                                   @RequestParam("userId") String userId, @RequestParam String token) {
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
     * @param activityId 活动Id
     * @param userId     用户Id
     * @param token      用户会话Token
     * @return 返回结果信息
     */
    @RequestMapping(value = "/official/activity/{activityId}/info", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("activityId") String activityId,
                                      @RequestParam("userId") String userId, @RequestParam("token") String token) {

        try {
            parameterChecker.checkUserInfo(userId, token);
            return officialService.getActivityInfo(activityId);
        } catch (Exception e) {
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
     * @param district 区
     * @param limit    查询条数
     * @param ignore   忽略条数
     * @return 返回查询结果
     */
    @RequestMapping(value = "/official/activity/list", method = RequestMethod.GET)
    public ResponseDo listActivities(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam("province") String province, @RequestParam("city") String city, @RequestParam("district") String district,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                     @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        try {
            parameterChecker.checkUserInfo(userId, token);
            Address address = new Address();
            address.setCity(city);
            address.setProvince(province);
            address.setDistrict(district);
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
            String invitedUserId = json.getString("invitedUserId");
            Boolean transfer = json.getBoolean("transfer");
            parameterChecker.checkUserInfo(userId, token);
            return officialService.inviteUserTogether(activityId, userId, invitedUserId, transfer);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
