package com.gongpingjia.carplay.entity.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/19.
 * 用户上传的个人图片
 */
@Document
public class Photo implements Comparable<Photo> {
    //photoId
    @Id
    private String id;

    //photoUrl key
    private String key;

    //url 不存储到DB中
    @Transient
    private String url;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long uploadTime;

    @Indexed
    private String userId;

    private int type;

    public Photo() {
    }

    public Photo(String id, String key, Long uploadTime) {
        this.id = id;
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

    @Override
    public int compareTo(Photo o) {
        return (int) (o.getUploadTime() - this.getUploadTime());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
