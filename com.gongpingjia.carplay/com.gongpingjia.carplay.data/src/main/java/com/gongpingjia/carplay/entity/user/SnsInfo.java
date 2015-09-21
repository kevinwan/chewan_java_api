package com.gongpingjia.carplay.entity.user;

/**
 * Created by licheng on 2015/9/19.
 * <p/>
 * 第三方登录相关内容
 */
public class SnsInfo {
    private String uid;

    private String channel;

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

    @Override
    public String toString() {
        return "SnsInfo{" +
                "uid='" + uid + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
