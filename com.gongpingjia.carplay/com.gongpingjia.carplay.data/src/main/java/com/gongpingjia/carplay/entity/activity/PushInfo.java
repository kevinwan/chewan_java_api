package com.gongpingjia.carplay.entity.activity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Licheng on 2015/10/26.
 * 创建活动，推送附近的记录
 */
@Document
public class PushInfo {

    @Id
    private String id;

    //推送消息发送人员ID
    @Indexed
    private String sendUserId;
    //推送消息接收人员
    @Indexed
    private String receivedUserId;

    //发送时间
    @Indexed
    private Long createTime;

    //是否删除标识
    private Boolean deleteFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getReceivedUserId() {
        return receivedUserId;
    }

    public void setReceivedUserId(String receivedUserId) {
        this.receivedUserId = receivedUserId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public String toString() {
        return "PushInfo{" +
                "id='" + id + '\'' +
                ", sendUserId='" + sendUserId + '\'' +
                ", receivedUserId='" + receivedUserId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
