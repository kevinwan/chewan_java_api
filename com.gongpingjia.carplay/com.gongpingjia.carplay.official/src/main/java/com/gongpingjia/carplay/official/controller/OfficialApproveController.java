package com.gongpingjia.carplay.official.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by licheng on 2015/9/28.
 * 官方审批操作
 */
@RestController
public class OfficialApproveController {

    @Autowired
    private OfficialApproveService service;

    /**
     * 审批个人图像认证
     *
     * @param userId 审批人用户Id
     * @param token  审批人会话Token
     * @param json   请求的JSON实体对象
     * @return 返回审批结果
     */
    @RequestMapping(value = "/official/approve/photo", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo approveUserPhoto(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 审批车主认证
     *
     * @param userId 审批人用户Id
     * @param token  审批人会话Token
     * @param json   请求JSON对象
     * @return 返回审批结果
     */
    @RequestMapping(value = "/official/approve/license", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo approveLicense(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {

        return ResponseDo.buildSuccessResponse();
    }
}
