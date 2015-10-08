package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户相关信息,以及环信用户信息
 */
@Document
public class User {

    private String password;

    private Long registerTime;

    @Indexed(unique = true)
    private String phone;

    //图像认证图片,photo.jpg
    private String photo;

    //用户车辆信息,驾龄
    private Integer drivingYears;

    //驾驶证PhotoUrl
    private String driverLicense;
    //行驶证PhotoUrl
    private String drivingLicense;

    private DrivingLicense license;

    //用户身份证相关信息
    private String idCardPhoto;

    /**
     * 用户第三方登录信息
     */
    private String uid;
    /**
     * 第三方登录渠道:qq/wechat/sinaWeibo
     */
    private String channel;

    /**
     * 用户只有一个相册，存放多张相片
     */
    private List<Photo> album;

    /**
     * 表示是否处于空闲装态，true表示空闲，false表示忙，默认空闲
     */
    private Boolean idle;

    //仅用于计算距离，不存储到DB
    @Transient
    private Double distance;

    //仅用于返回到客户端，存储到数据库
    @Transient
    private String token;


    /**
     * 刷新user相关的photo的URL地址
     *
     * @param localPhotoServer  本地服务器
     * @param remotePhotoServer 远程服务器
     */
    public void refreshPhotoInfo(String localPhotoServer, String remotePhotoServer) {
        if (!StringUtils.isEmpty(this.photo)) {
            this.photo = localPhotoServer + this.photo;
        }
        if (!StringUtils.isEmpty(this.driverLicense)) {
            this.driverLicense = localPhotoServer + this.driverLicense;
        }
        if (!StringUtils.isEmpty(this.drivingLicense)) {
            this.drivingLicense = localPhotoServer + this.drivingLicense;
        }

        if (this.album != null) {
            for (Photo photo : album) {
                photo.setUrl(remotePhotoServer + photo.getKey());
            }
        }
    }

    public DrivingLicense getLicense() {
        return license;
    }

    public void setLicense(DrivingLicense license) {
        this.license = license;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getDrivingYears() {
        return drivingYears;
    }

    public void setDrivingYears(Integer drivingYears) {
        this.drivingYears = drivingYears;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(String idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

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

    public List<Photo> getAlbum() {
        return album;
    }

    public void setAlbum(List<Photo> album) {
        this.album = album;
    }


}
