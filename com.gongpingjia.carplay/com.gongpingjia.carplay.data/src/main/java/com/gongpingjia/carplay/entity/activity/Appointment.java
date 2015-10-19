package com.gongpingjia.carplay.entity.activity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/22.
 * 邀请加入活动的信息，ActivityIntention为applyUser的活动意向信息
 */
@Document
public class Appointment extends ActivityIntention {

    @Id
    private String appointmentId;

    private String activityId;

    private String activityCategory;

    //邀请申请人
    private String applyUserId;

    //应邀人员
    //创建该活动的人；
    private String invitedUserId;

    private Long createTime;

    private String status;

    private String remark;

    private Long modifyTime;

    @Transient
    private Object applicant;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(String invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Object getApplicant() {
        return applicant;
    }

    public void setApplicant(Object applicant) {
        this.applicant = applicant;
    }
}
