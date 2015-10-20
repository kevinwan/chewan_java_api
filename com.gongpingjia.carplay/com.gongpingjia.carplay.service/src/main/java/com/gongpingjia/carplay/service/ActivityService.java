package com.gongpingjia.carplay.service;


import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import net.sf.json.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

/**
 * Created by heyongyu on 2015/9/22.
 */
public interface ActivityService {

    /**
     * 注册活动
     */
    public ResponseDo activityRegister(String userId, Activity activity) throws ApiException;


    /**
     * 获取活动信息
     */
    public ResponseDo getActivityInfo(String userId, String activityId) throws ApiException;


    /**
     * 获取周边的活动列表
     */
    public ResponseDo getNearActivityList( HttpServletRequest request,String userId) throws ApiException;



    /**
     * “约她”申请加入活动
     */
    public ResponseDo sendAppointment(String activityId, String userId, Appointment appointment) throws ApiException;

    /**
     * 处理活动加入申请
     * @param appointmentId
     * @param userId
     * @param acceptFlag
     * @return
     * @throws ApiException
     */
    public ResponseDo processAppointment(String appointmentId, String userId, boolean acceptFlag) throws ApiException;


    /**
     * 管理后台 查询  用户创建的活动
     * @param json
     * @return
     */
    public ResponseDo getUserActivityList(JSONObject json,String userId)throws ApiException;


    public ResponseDo updateUserActivity(JSONObject json,String activityId)throws ApiException;

    public ResponseDo viewUserActivity(String activityId) throws ApiException;

    public ResponseDo deleteUserActivities(Collection ids)throws ApiException;
}
