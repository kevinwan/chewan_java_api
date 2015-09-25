package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/9/24.
 * <p/>
 * 关注相关的操作
 */
@RestController
public class SubscribeController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private SubscribeService service;

    @RequestMapping(value = "/user/{userId}/subscribe", method = RequestMethod.GET)
    public ResponseDo getUserSubscribes(@PathVariable("userId") String userId, @RequestParam("token") String token) {
        LOG.info("getUserSubscribes begin");

        try {
            return service.getUserSubscribeInfo(userId, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
