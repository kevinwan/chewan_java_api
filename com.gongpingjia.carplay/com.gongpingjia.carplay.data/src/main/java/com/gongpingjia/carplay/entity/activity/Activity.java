package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity extends ActivityIntention {

    public static final String PAY_TYPE_TREAT = "请客";

    public static final String PAY_TYPE_INVITED = "请我";

    public static final String PAY_TYPE_AA = "AA";

    @Id
    private String activityId;
    //活动创建人员
    private String userId;

    @Transient
    private User organizer;

    private Long start;
    private Long end;

    //环信群组Id
    private String emchatGroupId;

    //活动创建地
    private Address establish;

    //活动创建地经纬度
    @GeoSpatialIndexed
    private Landmark estabPoint;

    //存放userID的列表信息
    private List<String> members;

    //对接大众点评活动businessID
    private String businessId;

    //活动创建时间
    private Long createTime;

    @Transient
    private Double distance;

    //活动类型，官方活动，普通活动
    private String category;

    //官方活动--------开始
    //官方活动封面图片
    private List<Photo> covers;
    //活动相册,普通活动，官方活动都会有
    private List<Photo> photos;

    //活动标题
    private String title;
    //活动描述信息
    private String description;

    //付费单价
    private Long price;
    //价格描述
    private String priceDesc;

    //官方活动流程
    private List<Flow> flows;

    private String instruction;
    //官方活动---------结束

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getEmchatGroupId() {
        return emchatGroupId;
    }

    public void setEmchatGroupId(String emchatGroupId) {
        this.emchatGroupId = emchatGroupId;
    }

    public Address getEstablish() {
        return establish;
    }

    public void setEstablish(Address establish) {
        this.establish = establish;
    }

    public Landmark getEstabPoint() {
        return estabPoint;
    }

    public void setEstabPoint(Landmark estabPoint) {
        this.estabPoint = estabPoint;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Photo> getCovers() {
        return covers;
    }

    public void setCovers(List<Photo> covers) {
        this.covers = covers;
    }
}
