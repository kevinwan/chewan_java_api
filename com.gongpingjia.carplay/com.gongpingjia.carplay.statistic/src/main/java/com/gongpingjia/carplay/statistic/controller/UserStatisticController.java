package com.gongpingjia.carplay.statistic.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.impl.OfficialParameterChecker;
import com.gongpingjia.carplay.statistic.service.UserStatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Created by 123 on 2015/11/5.
 */
@RestController
public class UserStatisticController {

    private static final Logger LOG = LoggerFactory.getLogger(UserStatisticController.class);

    @Autowired
    private OfficialParameterChecker parameterChecker;

    @Autowired
    private UserStatisticService userStatisticService;


    /**
     * 获取用户车主认证的统计信息
     *
     * @param userId
     * @param token
     * @param start
     * @param end
     * @return
     */
    public ResponseDo getUserLicenseAuthenticationStatistic(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                                            @RequestParam("start") Long start, @RequestParam("end") Long end,
                                                            @RequestParam(value = "unit", defaultValue = "3") int unit) {
        try {
            LOG.debug("Query authentication param: start:{}, end:{}, unit:{}", Arrays.asList(start, end, unit));

            parameterChecker.checkAdminUserInfo(userId, token);

            return ResponseDo.buildSuccessResponse(userStatisticService.getUserLicenseAuthenticationStatistic(start, end, unit));
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
