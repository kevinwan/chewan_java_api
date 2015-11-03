package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点6，统计用户的审核的信息
 */
@Document
public class StatisticDriverAuth extends StatisticParent {
    public static final String DRIVER_AUTHENTICATION = "driverAuthentication";// 车主认证点击事件
    public static final String DRIVING_LICENSE_AUTH = "drivingLicenseAuth";// 行驶证上传事件
    public static final String DRIVER_LICENSE_AUTH = "driverLicenseAuth";// 驾驶证上传事件


    private String userId;

    private String ip;

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
}
