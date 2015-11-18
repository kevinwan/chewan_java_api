package com.gongpingjia.carplay.entity.common;

import com.gongpingjia.carplay.common.util.CommonUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

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
    //0 用户相册照片
    private int type;
    //0 表示管理员没有check ， 1表示checked
    private int checked = 0;

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

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public Map<String, Object> buildBaseInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("url", CommonUtil.getThirdPhotoServer() + key);
        return map;
    }

    public Map<String, Object> buildCommonInfo() {
        Map<String, Object> map = buildBaseInfo();
        map.put("userId", userId);
        map.put("key", key);
        map.put("uploadTime", uploadTime);
        return map;
    }
}
