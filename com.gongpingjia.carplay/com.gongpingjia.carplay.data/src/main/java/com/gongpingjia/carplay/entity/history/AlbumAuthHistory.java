package com.gongpingjia.carplay.entity.history;

import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/11/12.
 * 相册审核历史
 */
@Document
public class AlbumAuthHistory {

    @Id
    private String id;

    private String applyUserId;

    private String authUserId;

    private long applyTime;

    private long authTime;

    private String remark;

    private List<Photo> photos;

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

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
