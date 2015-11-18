package com.gongpingjia.carplay.entity.user;

import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by licheng on 2015/9/19.
 * 用户相关信息,以及环信用户信息
 */
@Document
public class User{
    @Id
    private String userId;

    //用户基本信息
    private String nickname;
    private String password;
    //个人头像,avatar.jpg
    private String avatar;
    private String gender;
    private Long birthday;
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long registerTime;
    private String role;
    private boolean deleteFlag = false;

    @Indexed
    private String phone;

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

    private Car car;

    /**
     * 三方登录
     */
    private List<SnsChannel> snsChannels;

    /**
     * 表示是否处于空闲装态，true表示空闲，false表示忙，默认空闲
     */
    private Boolean idle = true;

    private String deviceToken;

    @Transient
    private int matchTimes = 0;

    @Transient
    private double distance;

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


    public Boolean getIdle() {
        return idle;
    }

    public void setIdle(Boolean idle) {
        this.idle = idle;
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getMatchTimes() {
        return matchTimes;
    }

    public void setMatchTimes(int matchTimes) {
        this.matchTimes = matchTimes;
    }

    public List<SnsChannel> getSnsChannels() {
        return snsChannels;
    }

    public void setSnsChannels(List<SnsChannel> snsChannels) {
        this.snsChannels = snsChannels;
    }

    /**
     * 构造用户的基本信息的Map
     *
     * @return
     */
    public Map<String, Object> buildBaseUserMap() {
        Map<String, Object> map = new HashMap<>(32);
        map.put("avatar", CommonUtil.getLocalPhotoServer() + avatar);
        map.put("userId", userId);
        map.put("nickname", nickname);
        map.put("gender", gender);
        map.put("birthday", birthday);
        map.put("phone", phone);
        map.put("age", calculateAge(birthday));
        map.put("emchatName", emchatName);
        return map;
    }

    public Map<String, Object> buildCommonUserMap() {
        Map<String, Object> map = buildBaseUserMap();
        map.put("photoAuthStatus", photoAuthStatus);
        map.put("photo", CommonUtil.getLocalPhotoServer() + photo);

        map.put("licenseAuthStatus", licenseAuthStatus);
        if (car != null) {
            car.setLogo(CommonUtil.getGPJBrandLogoPrefix() + car.getLogo());
        }
        map.put("car", car);
        map.put("role", role);

        map.put("idle", idle);
        return map;
    }


    public Map<String, Object> buildFullUserMap() {
        Map<String, Object> map = buildCommonUserMap();
        map.put("snsChannels", snsChannels);
        map.put("deviceToken", deviceToken);
        map.put("landmark", landmark);
        return map;
    }

    /**
     * 计算用户的信息的完善程度
     *
     * @return
     */
    private int computeCompletion(long photoCount) {
        int completion = 0;
        if (Constants.AuthStatus.ACCEPT.equals(licenseAuthStatus)) {
            completion += 20;
        }
        if (Constants.AuthStatus.ACCEPT.equals(photoAuthStatus)) {
            completion += 20;
        }

        final int total = 6;  //总共需要填写6项
        int has = 0;
        if (!StringUtils.isEmpty(nickname)) {
            has++;
        }
        if (birthday != null) {
            has++;
        }
        if (!StringUtils.isEmpty(gender)) {
            has++;
        }
        if (!StringUtils.isEmpty(avatar)) {
            has++;
        }
        if (!StringUtils.isEmpty(phone)) {
            has++;
        }
        if (photoCount > 1) {
            has++;
        }

        if (has == total) {
            completion += 60;
        } else {
            completion += (has * 60) / total;
        }
        return completion;
    }

    public void appendCompute(Map<String, Object> userMap, long photoCount) {
        userMap.put("completion", computeCompletion(photoCount));
    }

    public static void appendToken(Map<String, Object> userMap, String token) {
        userMap.put("token", token);
    }

    public static void appendDistance(Map<String, Object> userMap, Double distance) {
        userMap.put("distance", distance);
    }

    public static void appendAlbum(Map<String, Object> userMap, List<Photo> album) {
        Collections.sort(album);
        List<Map<String, Object>> albumList = new ArrayList<>(album.size());
        for (Photo item : album) {
            albumList.add(item.buildBaseInfo());
        }
        userMap.put("album", albumList);
    }

    public static void appendCover(Map<String, Object> userMap, String cover) {
        userMap.put("cover", cover);
    }

    public static void appendSubscribeFlag(Map<String, Object> userMap, Boolean subscribeFlag) {
        userMap.put("subscribeFlag", subscribeFlag);
    }

    public static void appendDriverLicense(Map<String, Object> userMap, String driverLicenseKey) {
        userMap.put("driverLicense", CommonUtil.getLocalPhotoServer() + driverLicenseKey);
    }

    public static void appendDrivingLicense(Map<String, Object> userMap, String drivingLicenseKey) {
        userMap.put("drivingLicense", CommonUtil.getLocalPhotoServer() + drivingLicenseKey);
    }

    public static void appendAuthentication(Map<String, Object> userMap, UserAuthentication authentication) {
        userMap.put("authentication", authentication);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //    private List<Photo> album;
//
//    public List<Photo> getAlbum() {
//        return album;
//    }
//
//    public void setAlbum(List<Photo> album) {
//        this.album = album;
//    }
}
