package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点2，统计动态中打电话和聊天事件
 */
@Document
public class StatisticActivityContact extends StatisticParent {


    public static final String ACTIVITY_DYNAMIC_CALL = "activityDynamicCall";

    public static final String ACTIVITY_DYNAMIC_CHAT = "activityDynamicChat";

    private String userId;

    private String appointmentId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
