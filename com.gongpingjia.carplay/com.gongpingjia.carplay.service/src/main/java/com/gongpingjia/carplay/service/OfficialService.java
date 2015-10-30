package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Address;

public interface OfficialService {


    /**
     * 获取活动详情
     *
     * @return
     */
    ResponseDo getActivityInfo(String id,Integer idType, String userId) throws ApiException;


    ResponseDo getActivityPageMemberInfo(String officialActivityId, String userId, Integer ignore, Integer limit) throws ApiException;


    /**
     * 获取官方活动
     *
     * @return 返回活动信息
     */
    ResponseDo getActivityList(Address address, int limit, int ignore);


    /**
     * @param activityId
     * @param userId
     * @return
     */
    ResponseDo applyJoinActivity(String activityId, String userId) throws ApiException;


    ResponseDo inviteUserTogether(String activityId, String fromUserId, String toUserId, boolean transfer,String message) throws ApiException;

    /**
     * 获取区域信息
     *
     * @param parentId
     * @return
     */
    ResponseDo getAreaList(Integer parentId);
}
