package com.gongpingjia.carplay.entity.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by licheng on 2015/9/21.
 * 反馈信息
 */
@Document
public class FeedBack {
    @Id
    private String id;

    private String userId;

    private List<Photo> photos;

    private String content;

    private Date createTime;

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

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "FeedBack{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", photos=" + photos +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
