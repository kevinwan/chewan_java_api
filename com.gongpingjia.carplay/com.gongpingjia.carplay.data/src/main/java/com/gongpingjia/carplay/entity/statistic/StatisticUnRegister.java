package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点1,7
 */
@Document
public class StatisticUnRegister extends StatisticParent {

    public static final String USER_REGISTER_SUCCESS = "userRegisterSuccess";//用户注册事件

    public static final String UN_REGISTER_NEARBY_INVITED = "unRegisterNearbyInvited";

    public static final String UN_REGISTER_MATCH_INVITED = "unRegisterMatchInvited";

    //埋点7
    public static final String UN_REGISTER_DYNAMIC_ACCEPT = "unRegisterDynamicAccept";

    public static final String DYNAMIC_ACCEPT_REGISTER = "dynamicAcceptRegister";


    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
