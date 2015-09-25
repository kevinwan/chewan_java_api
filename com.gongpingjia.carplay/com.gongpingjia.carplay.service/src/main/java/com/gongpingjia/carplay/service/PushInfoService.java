package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;

/**
 * Created by Administrator on 2015/9/24.
 */
public interface PushInfoService {

    public ResponseDo getPushInfo(String userId);
}
