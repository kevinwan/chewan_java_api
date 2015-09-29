package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.service.OfficialService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
    private ParameterChecker parameterChecker;

    @Autowired
    private OfficialService officialService;



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
        } catch (ApiException e) {
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
     * @param district 区
     * @param limit    查询条数
     * @param ignore   忽略条数
     * @return 返回查询结果
     */
    public ResponseDo listActivities(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam("province") String province, @RequestParam("city") String city, @RequestParam("district") String district,
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                     @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        try {
            parameterChecker.checkUserInfo(userId, token);
            if (limit == 0) {
                limit = 10;
            }
            Address address = checkInfo(province, city, district);
            return officialService.getActivityList(address, limit, ignore);

        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    private Address checkInfo(String province, String city, String district) throws ApiException {
        if (StringUtils.isEmpty(province)) {
            throw new ApiException("province 是空");
        }
        if (StringUtils.isEmpty(city)) {
            throw new ApiException("city 是空");
        }
        if (StringUtils.isEmpty(district)) {
            throw new ApiException("district 为空");
        }
        Address address = new Address();
        address.setProvince(province);
        address.setCity(city);
        address.setDistrict(district);
        return address;
    }
}
