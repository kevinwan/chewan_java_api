package com.gongpingjia.carplay.entity.history;

import com.gongpingjia.carplay.entity.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by licheng on 2015/9/21.
 */
@Document
public class AuthenticationHistory {
    @Id
    private String id;

    private String applyUserId;

    private String authId;

    @Transient
    private User authUser;

    private String applicationId;

    private String type;

    private String status;

    private String remark;

    private Long authTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Long authTime) {
        this.authTime = authTime;
    }

    @Override
    public String toString() {
        return "AuthenticationHistory{" +
                "id='" + id + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", authTime=" + authTime +
                '}';
    }
}
