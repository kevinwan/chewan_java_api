package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/9/28.
 * 官方审批操作
 */
@RestController
public class OfficialApproveController {

    private static Logger LOG = Logger.getLogger(OfficialApproveController.class);

    @Autowired
    private OfficialApproveService officialApproveService;

    @Autowired
    private ParameterChecker parameterChecker;

    /**
     * 官方认证审批
     *
     * @param userId 审批人用户Id
     * @param token  审批人会话Token
     * @param json   请求的JSON实体对象
     * @return 返回审批结果
     */
    @RequestMapping(value = "/official/approve", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo approveUserApply(@RequestParam("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        try {
            LOG.info("Begin approve user apply");
            parameterChecker.checkUserInfo(userId, token);
            String applicationId = json.getString("applicationId");
            String status = json.getString("status");
            return officialApproveService.saveAuthApplication(applicationId, status);
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
    @RequestMapping(value = "/official/approve/list", method = RequestMethod.GET)
    public ResponseDo approveList(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                  @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                  @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam("start") Long start, @RequestParam("end") Long end) {
        try {
            LOG.info("Begin obtian approve list");
            parameterChecker.checkUserInfo(userId, token);

            return officialApproveService.getAuthApplicationList(userId, status, start, end, ignore, limit);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
