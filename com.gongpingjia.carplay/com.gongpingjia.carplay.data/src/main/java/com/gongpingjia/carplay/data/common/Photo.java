package com.gongpingjia.carplay.data.common;

import java.util.Date;

/**
 * Created by licheng on 2015/9/19.
 * 用户上传的个人图片
 */
public class Photo {
    //photoId
    private String id;
    //photoUrl
    private String url;
    private Date uploadTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
}
