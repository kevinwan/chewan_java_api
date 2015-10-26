package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
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
    private boolean deleteFlag;

    @Indexed
    private String phone;
    @Transient
    private Integer age;

    //头像认证图片,photo.jpg
    private String photo;
    private String photoAuthStatus;

    //注意环信约束
    private String emchatName;

    //用户位置信息
    private Address address;

    //用户经纬度地理位置索引
    @GeoSpatialIndexed
    private Landmark landmark;

    //用户车辆信息,驾龄
    private Integer drivingYears;
    private String licenseAuthStatus;

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
     * 用户只有一个相册，存放多张相片
     */
    private List<Photo> album;

    private Car car;

    /**
     * 表示是否处于空闲装态，true表示空闲，false表示忙，默认空闲
     */
    private Boolean idle;

    //用户信息完善程度，运行时计算
    @Transient
    private Integer completion;

    //仅用于计算距离，不存储到DB
    @Transient
    private Double distance;

    //仅用于返回到客户端，存储到数据库
    @Transient
    private String token;

    //关注标识，是否已经被我关注了
    @Transient
    private boolean subscribeFlag;

    //用户的认证的信息
    @Transient
    private UserAuthentication authentication;

    /**
     * 刷新user相关的photo的URL地址
     *
     * @param localPhotoServer  本地服务器
     * @param remotePhotoServer 远程服务器
     */
    public void refreshPhotoInfo(String localPhotoServer, String remotePhotoServer, String gpjLogoServer) {
        if (!StringUtils.isEmpty(this.avatar)) {
            this.avatar = localPhotoServer + this.avatar;
        }
        if (!StringUtils.isEmpty(this.photo)) {
            this.photo = localPhotoServer + this.photo;
        }

        if (this.album != null) {
            for (Photo photo : album) {
                photo.setUrl(remotePhotoServer + photo.getKey());
            }
        }

        if (this.car != null) {
            if (this.car.getLogo() != null) {
                this.car.refreshPhotoInfo(gpjLogoServer);
            }
        }
    }

    /**
     * 获取用户的展示封面，如果用户上传了照片，显示最新的照片，否则显示用户注册时候的头像
     *
     * @return 用户展示封面的url(带http前缀)
     */
    public String getCover() {
        if (this.album != null && !this.album.isEmpty()) {
            Photo latest = this.album.get(this.album.size() - 1);
            if (!StringUtils.isEmpty(latest.getUrl())) {
                return latest.getUrl();
            }
            return CommonUtil.getThirdPhotoServer() + latest.getKey();
        }

        if (this.avatar.startsWith("http://")) {
            return this.avatar;
        }

        return CommonUtil.getLocalPhotoServer() + this.avatar;
    }

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

    public Integer getAge() {
        return calculateAge(birthday);
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Boolean getIdle() {
        return idle;
    }

    public void setIdle(Boolean idle) {
        this.idle = idle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(UserAuthentication authentication) {
        this.authentication = authentication;
    }

    public Integer getCompletion() {
        return completion;
    }

    public void setCompletion(Integer completion) {
        this.completion = completion;
    }

    public boolean isSubscribeFlag() {
        return subscribeFlag;
    }

    private int calculateAge(Long birthday) {
        if (null == birthday) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());

        Calendar userCal = Calendar.getInstance();
        userCal.setTimeInMillis(birthday);

        return calendar.get(Calendar.YEAR) - userCal.get(Calendar.YEAR);
    }

    public boolean getSubscribeFlag() {
        return subscribeFlag;
    }

    public void setSubscribeFlag(boolean subscribeFlag) {
        this.subscribeFlag = subscribeFlag;
    }

    /**
     * 隐藏用户的隐私信息
     */
    public User hideSecretInfo() {
        this.token = null;
        this.password = null;
        return this;
    }
}
