package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import net.sf.json.JSONObject;

public interface VersionService {
    /**
     * 获取版本信息
     *
     * @param product
     * @return
     * @throws ApiException
     */
    ResponseDo getVersion(String product) throws ApiException;


    /**
     * 提交反馈意见
     * @param json
     * @return
     */
    ResponseDo feedback(JSONObject json);
}
