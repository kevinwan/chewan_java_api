package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户相关信息,以及环信用户信息
 */
@Document
public class User {
    @Id
    private String userId;

    //用户基本信息
    private String nickname;
    private String password;
    //个人头像,avatar.jpg
    private String avatar;
    private String gender;
    private Long birthday;
    private Long registerTime;
    private String role;
    private boolean invalid;
    private String phone;

    //图像认证图片,photo.jpg
    private String photo;
    private String photoAuthStatus;

    //注意环信约束
    private String emchatName;

    //用户位置信息
    private Address address;
    private Landmark landmark;

    //用户车辆信息,驾龄
    private Integer drivingYears;

    //驾驶证PhotoUrl
    private String driverLicense;
    //行驶证PhotoUrl
    private String drivingLicense;
    private String licenseAuthStatus;
    private DrivingLicense license;

    private Car car;

    //用户身份证相关信息
    private String idCardPhoto;
    private boolean idCardAuthorized;

    /**
     * 用户第三方登录信息
     */
    private List<SnsInfo> snsInfos;

    /**
     * 用户相册信息，相册ID列表
     */
    private List<String> userAlbum;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmchatName() {
        return emchatName;
    }

    public void setEmchatName(String emchatName) {
        this.emchatName = emchatName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(String idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

    public boolean isIdCardAuthorized() {
        return idCardAuthorized;
    }

    public void setIdCardAuthorized(boolean idCardAuthorized) {
        this.idCardAuthorized = idCardAuthorized;
    }

    public List<SnsInfo> getSnsInfos() {
        return snsInfos;
    }

    public void setSnsInfos(List<SnsInfo> snsInfos) {
        this.snsInfos = snsInfos;
    }

    public List<String> getUserAlbum() {
        return userAlbum;
    }

    public void setUserAlbum(List<String> userAlbum) {
        this.userAlbum = userAlbum;
    }

    public DrivingLicense getLicense() {
        return license;
    }

    public void setLicense(DrivingLicense license) {
        this.license = license;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getPhotoAuthStatus() {
        return photoAuthStatus;
    }

    public void setPhotoAuthStatus(String photoAuthStatus) {
        this.photoAuthStatus = photoAuthStatus;
    }

    public String getLicenseAuthStatus() {
        return licenseAuthStatus;
    }

    public void setLicenseAuthStatus(String licenseAuthStatus) {
        this.licenseAuthStatus = licenseAuthStatus;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
