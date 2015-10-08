package com.gongpingjia.carplay.service;


import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import org.springframework.data.mongodb.core.query.Criteria;
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


    /**
     * 获取周边的活动列表
     */
    public ResponseDo getNearActivityList(Map<String, String> transParams, HttpServletRequest request,String userId) throws ApiException;


    /**
     * 获取周边的活动列表 初始化查询参数
     * transMap 定义了get/post过来的字段 变成 mongodb查选字段的 转换规则；
     */
    public Criteria initQuery(HttpServletRequest request, Map<String, String> transMap);

    /**
     * “约她”申请加入活动
     */
    public ResponseDo sendAppointment(String activityId, String userId, String token, Appointment appointment) throws ApiException;

    /**
     * 处理活动加入申请
     * @param appointmentId
     * @param userId
     * @param acceptFlag
     * @return
     * @throws ApiException
     */
    public ResponseDo applyJoinActivity(String appointmentId, String userId, boolean acceptFlag) throws ApiException;
}
