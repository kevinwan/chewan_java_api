package com.gongpingjia.carplay.statistic.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import com.gongpingjia.carplay.statistic.service.StatisticViewService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
@RestController
public class StatisticViewController {

    @Autowired
    private StatisticViewService statisticViewService;

    @Autowired
    private ParameterChecker parameterChecker;


    private static Logger logger = LoggerFactory.getLogger(StatisticViewController.class);

    @RequestMapping(value = "/statistic/unRegisterInfo", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo userRegisterInfo(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {

        try {
            parameterChecker.checkUserInfo(userId, token);
            return statisticViewService.getUnRegisterInfo(json);
        } catch (ApiException e) {
            logger.info(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
