package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动
 */
@RestController
public class OfficialActivityController {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialActivityController.class);

    @Autowired
    private OfficialActivityService service;

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

            CommonUtil.isEmpty(json, Arrays.asList("destPoint", "destination", "estabPoint", "estabPoint", "start", "transfer", "covers", "title",
                    "description", "price", "priceDesc", "instruction", "maleLimit", "femaleLimit"));

            OfficialActivity activity = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);
            activity.setOfficialActivityId(null);
            activity.setUserId(userId);

            return service.registerActivity(activity, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

}
