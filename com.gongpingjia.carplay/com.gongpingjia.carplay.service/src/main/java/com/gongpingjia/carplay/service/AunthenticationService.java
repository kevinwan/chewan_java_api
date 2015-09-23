package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import net.sf.json.JSONObject;

/**
 * Created by licheng on 2015/9/23.
 */
public interface AunthenticationService {

    /**
     * 驾驶证，行驶证认证申请
     *
     * @param json   入参
     * @param token  会话token
     * @param userId 用户Id
     * @return 返回申请结果
     */
    ResponseDo licenseAuthenticationApply(JSONObject json, String token, String userId) throws ApiException;

}
