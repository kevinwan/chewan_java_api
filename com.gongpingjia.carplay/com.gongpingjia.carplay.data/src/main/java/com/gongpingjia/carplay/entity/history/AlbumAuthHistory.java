package com.gongpingjia.carplay.entity.history;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/11/12.
 * 相册审核历史
 */
@Document
public class AlbumAuthHistory {

    @Id
    private String id;

    private String authUserId;

    private long authTime;

    private String remark;

    private String[] deleteUsers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String[] getDeleteUsers() {
        return deleteUsers;
    }

    public void setDeleteUsers(String[] deleteUsers) {
        this.deleteUsers = deleteUsers;
    }
}
