package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
@Document
public class OfficialActivity {

    @Id
    private String officialActivityId;

    //创建人 id
    @Indexed
    private String userId;

    //活动目的地
    private Address destination;


    //开始时间
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long start;

    //结束时间
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long end;

    //成员
    private List<String> members;

    //环信群组ID
    private String emchatGroupId;

    //第三方购票链接
    private String linkTicketUrl;


    //官方活动封面图片
    private Photo cover;

    //活动相册,普通活动，官方活动都会有
    private List<Photo> photos;

    //活动标题
    private String title;


    //活动介绍
    private String instruction;

    //活动内容
    private String description;

    //补充说明
    private String extraDesc;

    //付费单价
    private Double price;

    //补贴价格
    private Double subsidyPrice;

    //价格描述
    private String priceDesc;


    //限制类型；
    // 0:无限制 1：限制总人数 2：限制男女人数
    //Constant.OFFICIAL_ACTIVITY_LIMIT_TYPE 中的类型参数;
    private Integer limitType;


    //总限制人数
    private Integer totalLimit;

    //当前总人数
    private Integer nowJoinNum;

    //男性限制
    private Integer maleLimit;

    //当前男性数量
    private Integer maleNum;

    //女性限制
    private Integer femaleLimit;

    //当前女性数量
    private Integer femaleNum;

    //创建时间
    @Indexed
    private Long createTime;

    //上下架 标志 true 上架 false 下架   注意三种状态：
    //onFlag == false 未上架
    //onFlag == true && end > nowTime    上架中
    //noFlag == true && end < nowTime    已下架
    //使用 onFlag 跟 end 联合判断文档中的状态
    private Boolean onFlag;

    //删除标志位
    private Boolean deleteFlag = false;

    /**
     * 组织成员信息
     */
    @Transient
    private Map<String, Object> organizer;

    /**
     * 活动类型：官方活动,用于标识
     */
    @Transient
    private String activityCategory;

    @Transient
    private int status;

    /**
     * 封面信息
     */
    @Transient
    private String[] covers;

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

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
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

    public String getEmchatGroupId() {
        return emchatGroupId;
    }

    public void setEmchatGroupId(String emchatGroupId) {
        this.emchatGroupId = emchatGroupId;
    }

    public String getLinkTicketUrl() {
        return linkTicketUrl;
    }

    public void setLinkTicketUrl(String linkTicketUrl) {
        this.linkTicketUrl = linkTicketUrl;
    }

    public Photo getCover() {
        return cover;
    }

    public void setCover(Photo cover) {
        this.cover = cover;
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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getExtraDesc() {
        return extraDesc;
    }

    public void setExtraDesc(String extraDesc) {
        this.extraDesc = extraDesc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSubsidyPrice() {
        return subsidyPrice;
    }

    public void setSubsidyPrice(Double subsidyPrice) {
        this.subsidyPrice = subsidyPrice;
    }

    public String getPriceDesc() {
        return priceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        this.priceDesc = priceDesc;
    }

    public Integer getMaleLimit() {
        return maleLimit;
    }

    public void setMaleLimit(Integer maleLimit) {
        this.maleLimit = maleLimit;
    }

    public Integer getMaleNum() {
        return maleNum;
    }

    public void setMaleNum(Integer maleNum) {
        this.maleNum = maleNum;
    }

    public Integer getFemaleLimit() {
        return femaleLimit;
    }

    public void setFemaleLimit(Integer femaleLimit) {
        this.femaleLimit = femaleLimit;
    }

    public Integer getFemaleNum() {
        return femaleNum;
    }

    public void setFemaleNum(Integer femaleNum) {
        this.femaleNum = femaleNum;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getOnFlag() {
        return onFlag;
    }

    public void setOnFlag(Boolean onFlag) {
        this.onFlag = onFlag;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }

    public Integer getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Integer totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Integer getNowJoinNum() {
        return nowJoinNum;
    }

    public void setNowJoinNum(Integer nowJoinNum) {
        this.nowJoinNum = nowJoinNum;
    }

    public Map<String, Object> getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Map<String, Object> organizer) {
        this.organizer = organizer;
    }

    public String[] getCovers() {
        return covers;
    }

    public void setCovers(String[] covers) {
        this.covers = covers;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
