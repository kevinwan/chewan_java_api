package com.gongpingjia.carplay.entity.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/10.
 */
@Document
public class UserAuthentication {
    @Id
    private String userId;

    //驾驶证PhotoUrl
    private String driverLicense;
    //驾驶证
    private DriverLicense driver;

    //行驶证PhotoUrl
    private String drivingLicense;
    //行驶证
    private DrivingLicense license;

    //用户身份证相关信息
    private String idCardPhoto;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public DriverLicense getDriver() {
        return driver;
    }

    public void setDriver(DriverLicense driver) {
        this.driver = driver;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public DrivingLicense getLicense() {
        return license;
    }

    public void setLicense(DrivingLicense license) {
        this.license = license;
    }

    public String getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(String idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

    public void refreshPhotoInfo(String photoServer) {
        if (this.driverLicense != null) {
            this.driverLicense = photoServer + this.driverLicense;
        }
        if (this.drivingLicense != null) {
            this.drivingLicense = photoServer + this.drivingLicense;
        }
    }
}
