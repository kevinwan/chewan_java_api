package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;

import javax.servlet.http.HttpServletRequest;

public interface OfficialService {


    /**
     * 获取活动详情
     *
     * @return
     */
    ResponseDo getActivityInfo(HttpServletRequest request, String id, Integer idType, String userId) throws ApiException;


    ResponseDo getActivityPageMemberInfo(String id, Integer idType, String userId, Integer ignore, Integer limit,Landmark landmark) throws ApiException;


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

    /**
     * 退出参加官方活动
     * @param activityId
     * @param userId
     * @return
     * @throws ApiException
     */
    ResponseDo quitJoinActivity(String activityId, String userId) throws ApiException;


    ResponseDo inviteUserTogether(String activityId, String fromUserId, String toUserId, boolean transfer, String message) throws ApiException;

    /**
     * 获取区域信息
     *
     * @param parentId
     * @return
     */
    ResponseDo getAreaList(Integer parentId);


    /**
     * 获取街道的区域经纬度范围
     */
    ResponseDo getAreaRangeInfo(Integer code) throws ApiException;

    /**
     * 未注册用户，报名参加官方活动，记录用户的手机号码
     *
     * @param phone
     * @return
     */
    ResponseDo unregisterRecordUserPhone(String officialActivityId, String phone);
}
