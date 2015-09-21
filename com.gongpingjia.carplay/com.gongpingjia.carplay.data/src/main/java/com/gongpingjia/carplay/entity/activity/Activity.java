package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
@Document
public class Activity {
    @Id
    private String id;
    private String organizer;

    private String type;
    private String pay;

    //活动目的地
    private Address destination;
    //活动目的地经纬度
    private Landmark destPoint;

    private boolean transfer;
    private Date start;
    private Date end;

    private String emchatGroupId;

    //活动创建地
    private Address establish;
    //活动创建地经纬度
    private Landmark estabPoint;

    //存放userID的列表信息
    private List<String> members;

    //对接大众点评活动businessID
    private String businessId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Landmark getDestPoint() {
        return destPoint;
    }

    public void setDestPoint(Landmark destPoint) {
        this.destPoint = destPoint;
    }

    public Landmark getEstabPoint() {
        return estabPoint;
    }

    public void setEstabPoint(Landmark estabPoint) {
        this.estabPoint = estabPoint;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", organizer='" + organizer + '\'' +
                ", type='" + type + '\'' +
                ", pay='" + pay + '\'' +
                ", destination=" + destination +
                ", destPoint=" + destPoint +
                ", transfer=" + transfer +
                ", start=" + start +
                ", end=" + end +
                ", emchatGroupId='" + emchatGroupId + '\'' +
                ", establish=" + establish +
                ", estabPoint=" + estabPoint +
                ", members=" + members +
                '}';
    }
}
