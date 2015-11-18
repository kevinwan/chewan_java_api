package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import com.gongpingjia.carplay.service.impl.OfficialParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Created by licheng on 2015/9/28.
 * 官方审批操作
 */
@RestController
public class OfficialApproveController {

    private static Logger LOG = LoggerFactory.getLogger(OfficialApproveController.class);

    @Autowired
    private OfficialApproveService officialApproveService;

    @Autowired
    private OfficialParameterChecker parameterChecker;

    /**
     * 官方审批车主认证
     *
     * @param userId 审批人用户Id
     * @param token  审批人会话Token
     * @param json   请求的JSON实体对象
     * @return 返回审批结果
     */
    @RequestMapping(value = "/official/approve/driving", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo approveUserDrivingApply(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                              @RequestBody JSONObject json) {
        try {
            LOG.info("Begin approve user apply");
            parameterChecker.checkAdminUserInfo(userId, token);

            if (CommonUtil.isEmpty(json, Arrays.asList("applicationId", "accept", "license", "driver"))) {
                throw new ApiException("输入参数错误");
            }

            return officialApproveService.approveUserDrivingAuthentication(userId, json);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 官方认证信息查询
     *
     * @param userId 审批人用户Id
     * @param token  审批人会话Token
     * @return 返回审批结果
     */
    @RequestMapping(value = "/official/authentication/list", method = RequestMethod.GET)
    public ResponseDo approveList(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                  @RequestParam(value = "type") String type,
                                  @RequestParam(value = "phone", defaultValue = "") String phone,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "start", required = false) Long start,
                                  @RequestParam(value = "end", required = false) Long end) {

        try {
            LOG.info("Begin obtian approve list");
            parameterChecker.checkAdminUserInfo(userId, token);

            return officialApproveService.getAuthApplicationList(userId, type, status, start, end, phone);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取用户的认证申请的信息
     *
     * @param applicationId 申请Id
     * @param userId        管理员用户Id
     * @param token         管理员用户Token
     * @return
     */
    @RequestMapping(value = "/application/{applicationId}/info", method = RequestMethod.GET)
    public ResponseDo getApplicationInfo(@PathVariable("applicationId") String applicationId,
                                         @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.info("Begin query application info, applicationId:{}", applicationId);

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            return officialApproveService.getApplicationInfo(applicationId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 查看认证的Id信息
     *
     * @return
     */
    @RequestMapping(value = "/authentication/{authenticationId}/info", method = RequestMethod.GET)
    public ResponseDo getUserAuthenticationInfo(@PathVariable("authenticationId") String authenticationId,
                                                @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("Get user authentication infomation, authenticationId:{}", authenticationId);

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            return officialApproveService.getUserAuthenticationInfo(authenticationId, userId);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 更新用户的认证信息
     *
     * @param authenticationId
     * @param userId
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/authentication/{authenticationId}/update", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo updateUserAuthenticationInfo(@PathVariable("authenticationId") String authenticationId,
                                                   @RequestParam("userId") String userId, @RequestParam("token") String token,
                                                   @RequestBody JSONObject json) {
        LOG.debug("Update user authentication infomation, authenticationId:{}", authenticationId);

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            return officialApproveService.modifyUserAuthenticationInfo(authenticationId, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 用户头像认证审批
     *
     * @return
     */
    @RequestMapping(value = "/official/approve/userPhoto", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo approveUserPhotoAuthApply(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                                @RequestBody JSONObject json) {
        LOG.debug("Approved user photo authentication apply");

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("applicationId", "accept"))) {
                throw new ApiException("输入参数有误");
            }
            parameterChecker.checkAdminUserInfo(userId, token);

            return officialApproveService.approveUserPhotoAuthentication(userId, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取用户的认证申请里的审核历史信息
     *
     * @param authenticationId
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(value = "/authentication/{authenticationId}/historys", method = RequestMethod.GET)
    public ResponseDo getAuthHistory(@PathVariable("authenticationId") String authenticationId,
                                     @RequestParam("userId") String userId, @RequestParam("token") String token) {
        LOG.debug("Update user authentication infomation, authenticationId:{}", authenticationId);

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            return ResponseDo.buildSuccessResponse(officialApproveService.buildAuthHistory(authenticationId));
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 用户相册图片审核
     * @param userId
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/authentication/album", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo authUserAlbum(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                    @RequestBody JSONObject json) {
        LOG.debug("authUserAlbum called, authUserId:{}", userId);

        try {
            parameterChecker.checkAdminUserInfo(userId, token);

            if (CommonUtil.isEmpty(json, Arrays.asList("remark", "photoIds"))) {
                throw new ApiException("输入参数有误");
            }

            return officialApproveService.authUserAlbum(userId, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
