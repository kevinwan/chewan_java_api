package com.gongpingjia.carplay.entity.statistic;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/27.
 * 埋点3，对活动的操作的统计信息
 * 通过切面监控
 */
@Document
public class StatisticActivityMatch extends StatisticParent {


    public static final String ACTIVITY_TYPE_MATCH_COUNT = "activityTypeMatchCount";

    public static final String ACTIVITY_TYPE_RE_MATCH_COUNT = "activityTypeReMatchCount";

    public static final String ACTIVITY_MATCH_INVITED_COUNT = "activityMatchInvitedCount";

    public static final String ACTIVITY_MATCH_COUNT = "activityMatchCount";

    public static final String ACTIVITY_TYPE_CLICK = "activityTypeClick";

    //活动类型,吃饭，唱歌，KTV
    protected String type;

    private String majorType;

    //付费类型
    protected String pay;

    //活动目的地
    protected Address destination;
    //活动目的地经纬度
    protected Landmark destPoint;

    //是否包接送
    protected boolean transfer;

    private String userId;


    private String activityId;

    private String ip;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMajorType() {
        return majorType;
    }

    public void setMajorType(String majorType) {
        this.majorType = majorType;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Landmark getDestPoint() {
        return destPoint;
    }

    public void setDestPoint(Landmark destPoint) {
        this.destPoint = destPoint;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

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
