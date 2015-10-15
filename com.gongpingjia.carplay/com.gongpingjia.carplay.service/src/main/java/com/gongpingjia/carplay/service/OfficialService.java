package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Address;

public interface OfficialService {


    /**
     * 获取活动详情
     *
     * @param officialActivityId
     * @return
     */
    public ResponseDo getActivityInfo(String officialActivityId, String userId) throws ApiException;


    /**
     * 获取官方活动
     *
     * @return 返回活动信息
     */
    public ResponseDo getActivityList(Address address, int limit, int ignore);


    /**
     * @param activityId
     * @param userId
     * @return
     */
    public ResponseDo applyJoinActivity(String activityId, String userId) throws ApiException;


    public ResponseDo inviteUserTogether(String activityId, String fromUserId, String toUserId, boolean transfer) throws ApiException;
}
