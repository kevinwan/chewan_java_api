package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 * 用户关注
 */
@Document
public class Subscriber {

    @Id
    private String id;

    @Indexed
    private String fromUser;

    @Indexed
    private String toUser;

    private Long subscribeTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Long getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Long subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id='" + id + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", subscribeTime=" + subscribeTime +
                '}';
    }
}
