package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity extends ActivityIntention {

    public static final String PAY_TYPE_TREAT = "请客";

    public static final String PAY_TYPE_INVITED = "请我";

    public static final String PAY_TYPE_AA = "AA";


    @Id
    private String activityId;
    //活动创建人员
    private String userId;

    @Transient
    private User organizer;

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

    //活动创建时间
    private Long createTime;

    @Transient
    private Double distance;


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

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
