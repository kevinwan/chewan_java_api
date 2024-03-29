package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/9/22.
 */
@Document
public class AuthApplication {
    @Id
    private String applicationId;

    @Indexed
    private String applyUserId;

    @Indexed
    private String authUserId;

    /**
     * 认证申请名称 如 车主认证；
     */
    private String type;

    private String status;

    @Indexed
    private Long applyTime;

    @Indexed
    private Long authTime;

    private String remarks;

    @Transient
    private Map<String, Object> applyUser;

    @Transient
    private UserAuthentication authentication;

    @Transient
    private List<AuthenticationHistory> authHistorys;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
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

    public Long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Long applyTime) {
        this.applyTime = applyTime;
    }

    public Long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Long authTime) {
        this.authTime = authTime;
    }

    public Map<String, Object> getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(Map<String, Object> applyUser) {
        this.applyUser = applyUser;
    }

    public UserAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(UserAuthentication authentication) {
        this.authentication = authentication;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<AuthenticationHistory> getAuthHistorys() {
        return authHistorys;
    }

    public void setAuthHistorys(List<AuthenticationHistory> authHistorys) {
        this.authHistorys = authHistorys;
    }
}
