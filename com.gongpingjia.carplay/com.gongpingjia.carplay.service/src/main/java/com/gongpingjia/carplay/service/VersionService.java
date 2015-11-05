package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface VersionService {
    /**
     * 获取版本信息
     *
     * @param product
     * @return
     * @throws ApiException
     */
    ResponseDo getVersion(String product) throws ApiException;
}
