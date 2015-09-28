package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity extends ActivityIntention {

    public static final String ACTIVITY_TYPE_DINE = "吃饭";

    public static final String ACTIVITY_TYPE_FILM = "看电影";

    public static final String ACTIVITY_TYPE_SING = "唱歌";

    public static final String ACTIVITY_TYPE_TRAVEL = "旅行";

    public static final String ACTIVITY_TYPE_SPORT = "运动";

    public static final String ACTIVITY_TYPE_CAR_SHARING = "拼车";

    public static final String ACTIVITY_TYPE_PILOT = "代驾";


    public static final String PAY_TYPE_TREAT = "请客";

    public static final String PAY_TYPE_INVITED = "请我";

    public static final String PAY_TYPE_AA = "AA";


    @Id
    private String activityId;
    //活动创建人员
    private String userId;

    private Long start;
    private Long end;

    //环信群组Id
    private String emchatGroupId;

    //活动创建地
    private Address establish;

    //活动创建地经纬度
    @GeoSpatialIndexed
    private Landmark estabPoint;

    //存放userID的列表信息
    private List<String> members;

    //对接大众点评活动businessID
    private String businessId;

    private Long createTime;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getEmchatGroupId() {
        return emchatGroupId;
    }

    public void setEmchatGroupId(String emchatGroupId) {
        this.emchatGroupId = emchatGroupId;
    }

    public Address getEstablish() {
        return establish;
    }

    public void setEstablish(Address establish) {
        this.establish = establish;
    }

    public Landmark getEstabPoint() {
        return estabPoint;
    }

    public void setEstabPoint(Landmark estabPoint) {
        this.estabPoint = estabPoint;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
