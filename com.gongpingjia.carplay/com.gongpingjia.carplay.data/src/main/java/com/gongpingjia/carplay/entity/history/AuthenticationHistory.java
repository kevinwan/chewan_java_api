package com.gongpingjia.carplay.entity.history;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by licheng on 2015/9/21.
 */
@Document
public class AuthenticationHistory {
    @Id
    private String id;

    private String applicationId;

    private String status;

    private String remark;

    private Long authTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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
