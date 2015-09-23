package com.gongpingjia.carplay.entity.history;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 * 相册查看历史
 */
@Document
public class AlbumViewHistory {
    @Id
    private String id;

    private String albumId;

    //userId
    private String viewUser;

    private Long viewTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getViewUser() {
        return viewUser;
    }

    public void setViewUser(String viewUser) {
        this.viewUser = viewUser;
    }

    public Long getViewTime() {
        return viewTime;
    }

    public void setViewTime(Long viewTime) {
        this.viewTime = viewTime;
    }

    @Override
    public String toString() {
        return "AlbumViewHistory{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", viewUser='" + viewUser + '\'' +
                ", viewTime=" + viewTime +
                '}';
    }
}
