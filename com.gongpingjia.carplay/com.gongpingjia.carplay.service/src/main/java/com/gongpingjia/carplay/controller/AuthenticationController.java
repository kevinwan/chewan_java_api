package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.service.AunthenticationService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Created by licheng on 2015/9/23.
 */
@RestController
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private AunthenticationService service;

    /**
     * 车主认证申请
     *
     * @param userId 申请人Id
     * @param token  会话token
     * @param json   请求接收对象
     * @return
     */
    @RequestMapping(value = "/user/{userId}/license/authentication", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo licenseAuthenticationApply(@PathVariable(value = "userId") String userId,
                                                 @RequestParam(value = "token") String token, @RequestBody JSONObject json) {
        LOG.debug("licenseAuthenticationApply is called, request parameter produce:{}", json);

        if (CommonUtil.isEmpty(json, Arrays.asList("brand", "model", "driverLicense", "drivingLicense"))) {
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        try {
            return service.licenseAuthenticationApply(json, token, userId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    @RequestMapping(value = "/user/{userId}/photo/authentication", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo photoAuthenticationApply(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                               @RequestBody JSONObject json) {
        LOG.debug("licenseAuthenticationApply is called, request parameter produce:{}", json);
        if (CommonUtil.isEmpty(json, "photoId")) {
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        try {
            return service.photoAuthenticationApply(userId, token, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
