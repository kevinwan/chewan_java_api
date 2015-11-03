package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点5，统计APP的启动和动态中附近的操作
 */
@Document
public class StatisticDynamicNearby extends StatisticParent {

    public static final String APP_OPEN_COUNT = "appOpenCount";

    //TODO 服务端切面
    public static final String DYNAMIC_NEARBY_CLICK = "dynamicNearbyClick";

    public static final String DYNAMIC_NEARBY_INVITED = "dynamicNearbyInvited";



    private String userId;

    private String ip;

    private String activityId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
