package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity {
    @Id
    private String activityId;
    //活动创建人员
    private String userId;

    private String type;
    private String pay;

    //活动目的地
    private Address destination;
    //活动目的地经纬度
    private Landmark destPoint;

    private boolean transfer;
    private Long start;
    private Long end;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Landmark getDestPoint() {
        return destPoint;
    }

    public void setDestPoint(Landmark destPoint) {
        this.destPoint = destPoint;
    }

    public Landmark getEstabPoint() {
        return estabPoint;
    }

    public void setEstabPoint(Landmark estabPoint) {
        this.estabPoint = estabPoint;
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

    @Override
    public String toString() {
        return "Activity{" +
                "activityId='" + activityId + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", pay='" + pay + '\'' +
                ", destination=" + destination +
                ", destPoint=" + destPoint +
                ", transfer=" + transfer +
                ", start=" + start +
                ", end=" + end +
                ", emchatGroupId='" + emchatGroupId + '\'' +
                ", establish=" + establish +
                ", estabPoint=" + estabPoint +
                ", members=" + members +
                '}';
    }
}
