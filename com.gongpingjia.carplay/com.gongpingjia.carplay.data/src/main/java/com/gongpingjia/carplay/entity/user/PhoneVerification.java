package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 */
@Document
public class PhoneVerification {

    @Id
    private String id;

    @Indexed(unique = true)
    private String phone;

    private String code;

    private Long expire;

    private Integer sendTimes;

    private Long modifyTime;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Integer getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(Integer sendTimes) {
        this.sendTimes = sendTimes;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "PhoneVerification{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", expire=" + expire +
                ", sendTimes=" + sendTimes +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
