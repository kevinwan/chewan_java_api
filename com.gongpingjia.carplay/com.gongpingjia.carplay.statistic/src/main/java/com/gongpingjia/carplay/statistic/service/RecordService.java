package com.gongpingjia.carplay.statistic.service;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by licheng on 2015/10/28.
 */
public interface RecordService {


    void recordAll(HttpServletRequest request,JSONObject jsonObject);

    /**
     * 未注册附近邀他事件
     * @param request
     */
    void unRegisterNearbyInvited(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 未注册匹配邀请事件
     * @param request
     */
    void unRegisterMatchInvited(HttpServletRequest request, JSONObject jsonObject);

    void userRegister(HttpServletRequest request, JSONObject jsonObject);


    /**
     * 未注册用户同意假数据
     * @param request
     */
    void unRegisterDynamicAccept(HttpServletRequest request, JSONObject jsonObject);


    /**
     * 未注册用户同意后立即注册事件
     * @param request
     */
    void dynamicAcceptRegister(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 活动动态打电话事件
     * @param request
     */
    void activityDynamicCall(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 活动动态聊天事件
     * @param request
     */
    void activityDynamicChat(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 活动类型点击次数
     * @param request
     * @param jsonObject
     */
    void activityTypeClick(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 活动匹配邀他事件
     * @param request
     */
    void activityMatchInvitedCount(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 匹配活动匹配点击事件
     * @param request
     */
    void activityMatchCount(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 官方活动购票事件
     * @param request
     */
    void officialActivityBuyTicket(HttpServletRequest request, JSONObject jsonObject);

    /**
     *官方活动加入群聊事件
     * @param request
     */
    void officialActivityChatJoin(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 打开APP事件
     * @param request
     */
    void appOpenCount(HttpServletRequest request, JSONObject jsonObject);

    /**
     * 进入动态邀他事件
     * @param request
     */
    void dynamicNearbyInvited(HttpServletRequest request, JSONObject jsonObject);


}
