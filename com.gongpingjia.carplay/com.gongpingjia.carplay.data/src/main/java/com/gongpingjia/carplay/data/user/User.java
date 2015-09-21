package com.gongpingjia.carplay.data.user;

import com.gongpingjia.carplay.data.common.Address;

import java.util.Date;
import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户相关信息,以及环信用户信息
 */
public class User {
    private String id;
    private String nickname;
    private String password;
    private String phone;
    private String gender;
    private Date birthday;
    private String photo;

    private Address address;

    private int licenseYear;
    private String licensePhoto;
    private boolean licenseAuthorized;
    private DrivingLicense license;

    private Date createTime;
    private String role;
    private boolean invalid;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public int getLicenseYear() {
        return licenseYear;
    }

    public void setLicenseYear(int licenseYear) {
        this.licenseYear = licenseYear;
    }

    public String getLicensePhoto() {
        return licensePhoto;
    }

    public void setLicensePhoto(String licensePhoto) {
        this.licensePhoto = licensePhoto;
    }

    public boolean isLicenseAuthorized() {
        return licenseAuthorized;
    }

    public void setLicenseAuthorized(boolean licenseAuthorized) {
        this.licenseAuthorized = licenseAuthorized;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
}
