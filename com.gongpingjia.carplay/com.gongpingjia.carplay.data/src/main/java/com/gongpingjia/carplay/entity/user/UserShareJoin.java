package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/11/16.
 * <p/>
 * 用户通过分享出去的页面，申请加入到官方活动，记录用户申请的信息
 */
@Document
public class UserShareJoin {

    @Id
    private String id;

    @Indexed
    private String phone;

    //官方活动Id或者个人活动Id
    private String activityId;

    //区分官方活动和个人活动
    private String activityType;

    private Long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
