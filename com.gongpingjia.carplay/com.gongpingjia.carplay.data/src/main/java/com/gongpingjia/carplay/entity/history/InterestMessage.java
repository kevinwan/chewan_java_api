package com.gongpingjia.carplay.entity.history;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/21.
 */
@Document
public class InterestMessage {

    //用户创建活动
    public static final Integer USER_ACTIVITY = 0;
    //用户上传照片
    public static final Integer USER_ALBUM = 1;

    @Id
    private String id;

    //关联的ID，如果是创建活动，则为活动Id
    private String relatedId;

    //操作员Id
    private String userId;

    //类型,0为创建活动，1为相册图像上传
    private Integer type;
    //如果为上传照片，显示上传照片的数量
    private Integer count;
    //创建该记录的时间
    private Long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
