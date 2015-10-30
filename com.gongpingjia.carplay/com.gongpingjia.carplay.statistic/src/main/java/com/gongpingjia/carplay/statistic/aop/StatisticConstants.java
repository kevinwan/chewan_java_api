package com.gongpingjia.carplay.statistic.aop;

/**
 * Created by 123 on 2015/10/29.
 * 埋点名称
 */
public class StatisticConstants {

    public interface UserStatistic {
        String UNREGISTER_NEARBY_INVITED = "unRegisterNearbyInvited";// 未注册附近邀他事件
        String UNREGISTER_MATCH_INVITED = "unRegisterMatchInvited";// 未注册匹配邀请事件
        String USER_REGISTER = "userRegister";//用户注册事件
        String USER_REGISTER_SUCCESS = "userRegisterSuccess";//用户注册事件

        String UNREGISTER_DYNAMIC_ACCEPT = "unRegisterDynamicAccept";// 未注册用户同意假数据
        String DYNAMIC_ACCEPT_REGISTER = "dynamicAcceptRegister";// 未注册用户同意后立即注册事件
    }

    public interface ActivityStatistic {

        String DYNAMIC_CALL = "activityDynamicCall";// 活动动态打电话事件
        String DYNAMIC_CHAT = "activityDynamicChat";// 活动动态聊天事件
        String TYPE_MATCH_COUNT = "activityTypeMatchCount";// 活动类型精确匹配事件
        String MATCH_INVITED_COUNT = "activityMatchInvitedCount";// 活动匹配邀他事件
        String MATCH_COUNT = "activityMatchCount";//匹配活动匹配点击事件
    }

    public interface OfficialActivityStatistic {
        String COUNT = "officialActivityCount";// 推荐活动点击事件
        String JOIN = "officialActivityJoin";// 加入官方活动事件
        String BUY_TICKET = "officialActivityBuyTicket";//官方活动购票事件
        String CHAT_JOIN = "officialActivityChatJoin";//官方活动加入群聊事件
    }

    public interface DynamicStatistic {
        String APP_OPEN_COUNT = "appOpenCount";// 打开APP事件
        String NEARBY_CLICK = "dynamicNearbyClick";// 进入动态附近事件
        String NEARBY_INVITED = "dynamicNearbyInvited";// 进入动态邀他事件
    }

    public interface Authentication {
        String AUTHENTICATION = "driverAuthentication";// 车主认证点击事件
        String DRIVING_LICENSE = "drivingLicenseAuth";// 行驶证上传事件
        String DRIVER_LICENSE = "driverLicenseAuth";// 驾驶证上传事件
    }

}
