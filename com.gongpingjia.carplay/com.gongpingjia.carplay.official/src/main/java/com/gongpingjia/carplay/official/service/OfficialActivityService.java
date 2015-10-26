package com.gongpingjia.carplay.official.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动接口
 */
public interface OfficialActivityService {

    /**
     * 创建官方活动
     *
     * @param json     活动对象封装的接送对象
     * @param userId   用户id
     * @return 返回创建结果信息
     * @throws ApiException
     */
    ResponseDo registerActivity( JSONObject json,String userId) throws ApiException;


    ResponseDo getActivityList(String   userId,JSONObject json) throws ApiException;

    ResponseDo changeActivityOnFlag(String officialActivityId)throws ApiException;

    ResponseDo updateActivity(String officialActivityId, JSONObject json,String userId)throws ApiException;

    ResponseDo getActivity(String officialActivityId) throws ApiException;

    ResponseDo deleteActivities(List<String> officialActivityIds) throws ApiException;


    ResponseDo updateActivityLimit(String activityId,JSONObject json) throws ApiException;
}
