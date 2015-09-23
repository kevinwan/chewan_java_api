package com.gongpingjia.carplay.service;


import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by heyongyu on 2015/9/22.
 */
public interface ActivityService {

    /**
     * 注册活动
     */
    public ResponseDo activityRegister(String userId, String token, Activity activity) throws ApiException;


    /**
     * 获取活动信息
     */
    public ResponseDo getActivityInfo(String userId, String token, String activityId) throws ApiException;


    public ResponseDo getNearActivityList(Map<String, String> transParams, HttpServletRequest request) throws ApiException;


    public Query initQuery(HttpServletRequest request, Map<String, String> transMap);
}
