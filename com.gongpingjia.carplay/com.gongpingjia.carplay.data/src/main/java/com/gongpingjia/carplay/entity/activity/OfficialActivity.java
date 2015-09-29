package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by Administrator on 2015/9/29.
 */
public class OfficialActivity {

    @Id
    private String officialActivityId;

    private String userId;

    private Landmark destPoint;

    private Address destination;

    private Landmark estabPoint;

    private Address establish;

    private Long start;

    private Long end;

    private List<String> members;


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

    private Integer maleLimit;

    private Integer femaleLimit;

    private Long createTime;

    public String getOfficialActivityId() {
        return officialActivityId;
    }

    public void setOfficialActivityId(String officialActivityId) {
        this.officialActivityId = officialActivityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Landmark getDestPoint() {
        return destPoint;
    }

    public void setDestPoint(Landmark destPoint) {
        this.destPoint = destPoint;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Landmark getEstabPoint() {
        return estabPoint;
    }

    public void setEstabPoint(Landmark estabPoint) {
        this.estabPoint = estabPoint;
    }

    public Address getEstablish() {
        return establish;
    }

    public void setEstablish(Address establish) {
        this.establish = establish;
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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<Photo> getCovers() {
        return covers;
    }

    public void setCovers(List<Photo> covers) {
        this.covers = covers;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
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

    public Integer getMaleLimit() {
        return maleLimit;
    }

    public void setMaleLimit(Integer maleLimit) {
        this.maleLimit = maleLimit;
    }

    public Integer getFemaleLimit() {
        return femaleLimit;
    }

    public void setFemaleLimit(Integer femaleLimit) {
        this.femaleLimit = femaleLimit;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
