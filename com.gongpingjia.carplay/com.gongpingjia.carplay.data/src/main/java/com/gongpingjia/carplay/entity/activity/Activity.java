package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity extends ActivityIntention implements Comparable<Activity> {


    @Id
    private String activityId;


    //活动创建人员
    @Indexed
    private String userId;

    @Transient
    private User organizer;

    @Indexed
    private Long start;

    @Indexed
    private Long end;

    //环信群组Id
    @Indexed
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
    @Indexed
    private Long createTime;

    private Boolean deleteFlag = false;

    @Transient
    private Double distance;

    private List<String> applyIds;


    @Transient
    private List<Appointment> appointmentList;

    //该字段仅用于计算排序权重
    @Transient
    private double sortFactor;

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

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<String> getApplyIds() {
        return applyIds;
    }

    public void setApplyIds(List<String> applyIds) {
        this.applyIds = applyIds;
    }


    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public double getSortFactor() {
        return sortFactor;
    }

    public void setSortFactor(double sortFactor) {
        this.sortFactor = sortFactor;
    }


    @Override
    public int compareTo(Activity o) {
        return (int) ((this.sortFactor - o.sortFactor) * 100);
    }
}
