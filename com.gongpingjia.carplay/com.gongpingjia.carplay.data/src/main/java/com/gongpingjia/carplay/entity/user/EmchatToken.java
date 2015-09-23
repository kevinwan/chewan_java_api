package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 */
@Document
public class EmchatToken {

    @Id
    private String id;

    private String application;

    private String token;

    private Long expire;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "EmchatToken{" +
                "id='" + id + '\'' +
                ", application='" + application + '\'' +
                ", token='" + token + '\'' +
                ", expire=" + expire +
                '}';
    }
}
