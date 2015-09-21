package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * <p/>
 * 用户相册
 */
@Document
public class Album {
    @Id
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

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", createTime=" + createTime +
                ", photos=" + photos +
                '}';
    }
}
