package com.gongpingjia.carplay.official.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;

/**
 * Created by licheng on 2015/9/28.
 * 官方审批服务
 */
public interface OfficialApproveService {

    public ResponseDo saveAuthApplication(String applicationId, String status);

    public ResponseDo getAuthApplicationList(String userId, String status, Long start, Long end, int ignore, int limit);
}
