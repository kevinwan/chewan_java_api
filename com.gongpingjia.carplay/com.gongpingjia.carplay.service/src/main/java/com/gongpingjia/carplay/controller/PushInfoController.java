package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.service.PushInfoService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by heyongyu on 2015/9/24.
 */

/**
 * 最新动态
 */
@RestController
public class PushInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(PushInfoController.class);

    @Autowired
    private PushInfoService pushInfoService;

    @Autowired
    private ParameterChecker parameterChecker;


    /**
     * 获取动态信息
     *@param userId
     *@param token
     */
    @RequestMapping(value = "/user/{userId}/pushInfo", method = RequestMethod.GET)
    public ResponseDo getActivityInfo(@PathVariable("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("activity/{activityId}/info begin");
        try {
            parameterChecker.checkUserInfo(userId, token);
            return pushInfoService.getPushInfo(userId);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
