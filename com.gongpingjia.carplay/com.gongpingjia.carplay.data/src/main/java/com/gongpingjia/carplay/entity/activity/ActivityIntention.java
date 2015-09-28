package com.gongpingjia.carplay.entity.activity;

import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;

/**
 * Created by licheng on 2015/9/25.
 */
public class ActivityIntention {

    //活动类型,吃饭，唱歌，KTV
    protected String type;
    //付费类型
    protected String pay;

    //活动目的地
    protected Address destination;
    //活动目的地经纬度
    protected Landmark destPoint;

    //是否包接送
    protected boolean transfer;

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

    public Landmark getDestPoint() {
        return destPoint;
    }

    public void setDestPoint(Landmark destPoint) {
        this.destPoint = destPoint;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }
}
