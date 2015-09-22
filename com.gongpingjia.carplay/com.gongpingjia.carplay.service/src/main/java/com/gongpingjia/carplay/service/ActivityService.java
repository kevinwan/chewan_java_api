package com.gongpingjia.carplay.service;


import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;

public interface ActivityService {

    public ResponseDo activityRegister(String userId, String token, Activity activity) throws ApiException;

    public ResponseDo getActivityInfo(String userId, String token, String activityId) throws ApiException;

}
