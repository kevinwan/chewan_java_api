package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动
 */
@RestController
public class OfficialActivityController {

    @Autowired
    private OfficialActivityService service;

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

        return ResponseDo.buildSuccessResponse();
    }



}
