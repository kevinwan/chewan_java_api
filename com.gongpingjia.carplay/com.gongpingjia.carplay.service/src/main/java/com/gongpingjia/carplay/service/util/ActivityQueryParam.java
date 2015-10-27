package com.gongpingjia.carplay.service.util;

import com.gongpingjia.carplay.common.util.PropertiesUtil;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

/**
 * Created by licheng on 2015/10/23.
 * 获取附近的活动查询参数
 */
public class ActivityQueryParam {
    private String userId;
    private String token;

    private String majorType;
    private String type;
    private String pay;
    private String gender;
    private Boolean transfer;
    private Integer ignore;
    private Integer limit;
    private Double longitude;
    private Double latitude;

    private String province;
    private String city;
    private String district;

    private Double maxDistance;
    //单位为毫秒
    private Long maxTimeLimit;

    public ActivityQueryParam() {
        this.maxDistance = Double.parseDouble(PropertiesUtil.getProperty("activity.defaultMaxDistance",
                String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));
        this.maxTimeLimit = Long.parseLong(PropertiesUtil.getProperty("activity.defaultMaxPubTime",
                String.valueOf(ActivityWeight.MAX_PUB_TIME))) * 60 * 1000;
        this.transfer = false;
    }

    /**
     * 根据请求参数构造查询条件,附近活动，活动匹配
     *
     * @return 返回查询条件
     */
    public Criteria buildCommonQueryParam() {

        //距离计算
        Double distance = maxDistance;
        if (distance == null) {
            distance = Double.parseDouble(PropertiesUtil.getProperty("activity.defaultMaxDistance",
                    String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));
            maxDistance = distance;
        }
        //查询未删除的
        Criteria criteria = Criteria.where("estabPoint").near(new Point(this.longitude, this.latitude))
                .maxDistance(distance * 180 / DistanceUtil.EARTH_RADIUS).and("deleteFlag").is(false);

        if (!StringUtils.isEmpty(userId)) {
            //排除活动创建人员
            criteria.and("userId").ne(userId);
        }

        if (!StringUtils.isEmpty(majorType)) {
            criteria.and("majorType").is(majorType);
        }

//        if (!StringUtils.isEmpty(type)) {
//            //类型
//            criteria.and("type").is(type);
//        }

        if (!StringUtils.isEmpty(pay)) {
            //付费
            criteria.and("pay").is(pay);
        }

//        if (!StringUtils.isEmpty(gender)) {
//            //性别
//            criteria.and("gender").is(gender);
//        }

        if (transfer != null && transfer) {
            //只有选择包接送才会执行这个
            criteria.and("transfer").is(transfer);
        }

        if (!StringUtils.isEmpty(province)) {
            //省
            criteria.and("destination.province").is(gender);
        }
        if (!StringUtils.isEmpty(city)) {
            //市
            criteria.and("destination.city").is(gender);
        }
        if (!StringUtils.isEmpty(district)) {
            //区
            criteria.and("destination.district").is(gender);
        }
        return criteria;
    }

    /**
     * 构造扩展查询条件，只有类型，距离，非当前用户，适用于活动匹配扩展查询
     *
     * @return 查询参数
     */
    public Criteria buildExpandQueryParam() {
        //距离计算
        Double distance = maxDistance;
        if (distance == null) {
            distance = Double.parseDouble(PropertiesUtil.getProperty("activity.defaultMaxDistance",
                    String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));
            maxDistance = distance;
        }

        Criteria criteria = Criteria.where("estabPoint").near(new Point(this.longitude, this.latitude))
                .maxDistance(distance * 180 / DistanceUtil.EARTH_RADIUS).and("deleteFlag").is(false);

        if (!StringUtils.isEmpty(majorType)) {
            criteria.and("majorType").is(majorType);
        }

        if (!StringUtils.isEmpty(type)) {
            //类型
            criteria.and("type").is(type);
        }

        if (!StringUtils.isEmpty(userId)) {
            //排除活动创建人员
            criteria.and("userId").ne(userId);
        }

        return criteria;
    }

    public boolean isUserEmpty() {
        return StringUtils.isEmpty(userId);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMajorType() {
        return majorType;
    }

    public void setMajorType(String majorType) {
        this.majorType = majorType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getTransfer() {
        return transfer;
    }

    public void setTransfer(Boolean transfer) {
        this.transfer = transfer;
    }

    public Integer getIgnore() {
        return ignore;
    }

    public void setIgnore(Integer ignore) {
        this.ignore = ignore;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Double maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public String toString() {
        return "ActivityQueryParam{" +
                "userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", pay='" + pay + '\'' +
                ", gender='" + gender + '\'' +
                ", transfer=" + transfer +
                ", ignore=" + ignore +
                ", limit=" + limit +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", maxDistance=" + maxDistance +
                ", maxTimeLimit=" + maxTimeLimit +
                '}';
    }

    public Long getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public void setMaxTimeLimit(Long maxTimeLimit) {
        this.maxTimeLimit = maxTimeLimit;
    }

}
