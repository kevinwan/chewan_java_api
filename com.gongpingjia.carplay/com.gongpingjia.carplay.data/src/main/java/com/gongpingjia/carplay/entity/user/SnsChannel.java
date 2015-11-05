package com.gongpingjia.carplay.entity.user;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by 123 on 2015/11/5.
 */
public class SnsChannel {
    /**
     * 用户第三方登录信息
     */
    @Indexed
    private String uid;
    /**
     * 第三方登录渠道:qq/wechat/sinaWeibo
     */
    private String channel;

    /**
     * 三方登录的密码
     */
    private String password;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SnsChannel{" +
                "uid='" + uid + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
