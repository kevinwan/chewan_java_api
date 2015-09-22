package com.gongpingjia.carplay.entity.auth;

import com.gongpingjia.carplay.entity.common.Car;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/22.
 * 车主认证
 */
@Document
public class LicenseAuth {

    private String id;

    /**
     * 申请人
     */
    private String userId;

    /**
     * 认证中，认证通过，认证不通过
     */
    private String status;

    private String remark;

    private Car car;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
