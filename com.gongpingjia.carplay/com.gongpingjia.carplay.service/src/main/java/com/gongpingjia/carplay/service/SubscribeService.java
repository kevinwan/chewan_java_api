package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

/**
 * Created by licheng on 2015/9/24.
 * <p/>
 * 订购相关
 */
public interface SubscribeService {

    /**
     * 获取用户的关注信息
     *
     * @param userId 用户Id
     * @param token  用户会话token
     * @return 返回查询结果信息
     */
    ResponseDo getUserSubscribeInfo(String userId, String token) throws ApiException;
}
