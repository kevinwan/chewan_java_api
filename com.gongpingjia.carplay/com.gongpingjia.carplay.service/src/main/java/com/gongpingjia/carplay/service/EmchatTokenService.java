package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.exception.ApiException;

/**
 * Created by Administrator on 2015/9/22.
 */
public interface EmchatTokenService {

    public String getToken() throws ApiException;
}
