package com.gongpingjia.carplay.entity.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/22.
 * 用户头像认证
 */
@Document
public class PhotoAuth {

    @Id
    private String id;

    @Indexed
    private String userId;

    /**
     * 认证中，认证通过，认证不通过
     */
    private String status;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long applyTime;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long authTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
