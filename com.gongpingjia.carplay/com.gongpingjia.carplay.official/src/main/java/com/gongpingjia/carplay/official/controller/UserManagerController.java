package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.official.service.UserManagerService;
import com.gongpingjia.carplay.service.impl.OfficialParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/10/19.
 * 运营后台提供用户信息管理相关的接口
 */
@RestController
public class UserManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerController.class);

    @Autowired
    private UserManagerService service;

    @Autowired
    private OfficialParameterChecker parameterChecker;

    /**
     * 按照查询条件获取用户的列表信息
     *
     * @param userId            管理员用户Id
     * @param token             管理员会话Token
     * @param phone             查询的手机号码
     * @param nickname          查询的昵称
     * @param licenseAuthStatus 车主认证状态
     * @param photoAuthStatus   头像认证状态
     * @param start             启动时间
     * @param end               结束时间
     * @param limit             查询限制行数
     * @param ignore            查询忽略行数
     * @return 返回查询结果
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public ResponseDo listUsers(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "nickname", required = false) String nickname,
                                @RequestParam(value = "licenseAuthStatus", required = false) String licenseAuthStatus,
                                @RequestParam(value = "photoAuthStatus", required = false) String photoAuthStatus,
                                @RequestParam("start") Long start, @RequestParam("end") Long end,
                                @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        LOG.info("listUsers was called");

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            return service.listUsers(phone, nickname, licenseAuthStatus, photoAuthStatus, start, end, limit, ignore);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取用户的详细信息
     *
     * @param userId      用户Id
     * @param adminUserId 查看的管理员用户Id
     * @param token       管理员会话Token
     * @return 返回用户详细信息
     */
    @RequestMapping(value = "/user/{userId}/detail", method = RequestMethod.GET)
    public ResponseDo getUserDetail(@PathVariable("userId") String userId,
                                    @RequestParam("userId") String adminUserId, @RequestParam("token") String token) {
        LOG.info("getUserDetail was called");
        try {
            parameterChecker.checkAdminUserInfo(adminUserId, token);

            return service.viewUserDetail(userId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 更新用户的个人信息
     *
     * @param userId      用户Id
     * @param adminUserId 管理员用户Id
     * @param token       管理员用户会话Token
     * @param json        请求体
     * @return 返回更新结果
     */
    @RequestMapping(value = "/user/{userId}/update", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo updateUserDetail(@PathVariable("userId") String userId,
                                       @RequestParam("userId") String adminUserId, @RequestParam("token") String token,
                                       @RequestBody JSONObject json) {
        LOG.info("updateUserDetail was called by user:{}", adminUserId);

        try {
            parameterChecker.checkAdminUserInfo(adminUserId, token);

            return service.updateUserDetail(userId, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
