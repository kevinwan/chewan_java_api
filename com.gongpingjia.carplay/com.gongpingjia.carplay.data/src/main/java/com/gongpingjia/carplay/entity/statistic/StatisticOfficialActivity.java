package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/28.
 * 埋点4，统计官方活动相关的数据
 * <p/>
 * 统计活动点击获取详情和加入官方活动，通过切面来做
 */
@Document
public class StatisticOfficialActivity extends StatisticParent {

    public static final String TYPE_JOIN = "officialActivityJoin";
    public static final String TYPE_NO_JOIN_VIEW = "officialActivityNoJoinView";

    private String userId;

    private String officialActivityId;


    private String ip;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOfficialActivityId() {
        return officialActivityId;
    }

    public void setOfficialActivityId(String officialActivityId) {
        this.officialActivityId = officialActivityId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
