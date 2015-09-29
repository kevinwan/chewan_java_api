package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动相关操作
 */
public class OfficialController {
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
        return ResponseDo.buildSuccessResponse();
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
                                      @RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "token", required = false) String token) {
        return ResponseDo.buildSuccessResponse();
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
    public ResponseDo listActivities(@RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "token", required = false) String token,
                                     @RequestParam("province") String province, @RequestParam("city") String city, @RequestParam("district") String district,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                     @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        return ResponseDo.buildSuccessResponse();
    }
}
