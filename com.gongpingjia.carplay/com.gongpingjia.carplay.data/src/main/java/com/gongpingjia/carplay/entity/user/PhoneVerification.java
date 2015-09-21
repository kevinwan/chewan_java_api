package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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

    private Date expire;

    @Override
    public String toString() {
        return "PhoneVerification{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", code='" + code + '\'' +
                ", expire=" + expire +
                '}';
    }
}
