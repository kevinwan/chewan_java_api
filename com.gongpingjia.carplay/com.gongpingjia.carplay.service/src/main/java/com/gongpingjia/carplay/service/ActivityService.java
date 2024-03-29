package com.gongpingjia.carplay.service;


import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.service.util.ActivityQueryParam;
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
    ResponseDo activityRegister(String userId, Activity activity) throws ApiException;


    /**
     * 获取活动信息
     */
    ResponseDo getActivityInfo(String userId, String activityId, Landmark landmark) throws ApiException;


    /**
     * “约她”申请加入活动
     */
    ResponseDo sendAppointment(String activityId, String userId, Appointment appointment) throws ApiException;

    /**
     * 处理活动加入申请
     *
     * @param appointmentId
     * @param userId
     * @param acceptFlag
     * @return
     * @throws ApiException
     */
    ResponseDo processAppointment(String appointmentId, String userId, boolean acceptFlag) throws ApiException;


    /**
     * 管理后台 查询  用户创建的活动
     *
     * @param json
     * @return
     */
    public ResponseDo getUserActivityList(JSONObject json, String userId) throws ApiException;


    /**
     * 后台 更新用户的某一个活动；
     *
     * @param json
     * @param activityId
     * @return
     * @throws ApiException
     */
    public ResponseDo updateUserActivity(JSONObject json, String activityId) throws ApiException;

    /**
     * 后台查看用户的活动
     *
     * @param activityId
     * @return
     * @throws ApiException
     */
    public ResponseDo viewUserActivity(String activityId) throws ApiException;

    /**
     * 后台 删除用户发布的活动
     *
     * @param ids
     * @return
     * @throws ApiException
     */
    public ResponseDo deleteUserActivities(Collection ids) throws ApiException;

    /**
     * 根据param参数获取附近的活动信息
     *
     * @param param 请求参数
     * @return 返回结果信息
     */
    ResponseDo getNearByActivityList(HttpServletRequest request,ActivityQueryParam param);

    /**
     * 随便看看接口
     *
     * @param param 查询参数
     * @return
     */
    ResponseDo getRandomActivities(ActivityQueryParam param);

    /**
     * 获取附近的匹配活动的数量
     *
     * @param param 查询参数
     * @return 返回结果信息
     */
    ResponseDo getNearByActivityCount(ActivityQueryParam param);

    /**
     * 获取当前用户的附近的推送消息
     * @param userId
     * @param limit
     *@param ignore @return
     */
    ResponseDo getActivityPushInfos(HttpServletRequest request, String userId, Integer limit, Integer ignore);


    ResponseDo registerUserActivity(String phone,String userId,Activity activity) throws ApiException;
}
