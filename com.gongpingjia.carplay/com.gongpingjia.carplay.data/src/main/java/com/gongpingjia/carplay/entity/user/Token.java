package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by licheng on 2015/9/21.
 * 用户会话Token
 */
@Document
public class Token {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String token;

    private Date expire;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", expire=" + expire +
                '}';
    }
}
