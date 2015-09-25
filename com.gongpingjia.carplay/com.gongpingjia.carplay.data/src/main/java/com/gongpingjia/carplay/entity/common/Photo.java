package com.gongpingjia.carplay.entity.common;

import org.springframework.data.annotation.Transient;

/**
 * Created by licheng on 2015/9/19.
 * 用户上传的个人图片
 */
public class Photo {
    //photoId
    private String id;
    //photoUrl key
    private String key;

    //url 不存储到DB中
    @Transient
    private String url;

    private Long uploadTime;

    public Photo() {
    }

    public Photo(String key, Long uploadTime) {
        this.key = key;
        this.uploadTime = uploadTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
