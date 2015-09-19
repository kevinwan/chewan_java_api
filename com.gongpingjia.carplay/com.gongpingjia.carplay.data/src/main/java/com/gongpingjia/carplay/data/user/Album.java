package com.gongpingjia.carplay.data.user;

import java.util.Date;
import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * <p/>
 * 用户相册
 */
public class Album {
    private String id;
    private String userId;
    private String coverUrl;
    private Date createTime;

    private List<Photo> photos;

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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
