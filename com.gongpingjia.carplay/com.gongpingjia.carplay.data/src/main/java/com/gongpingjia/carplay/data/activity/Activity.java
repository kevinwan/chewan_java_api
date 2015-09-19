package com.gongpingjia.carplay.data.activity;

import com.gongpingjia.carplay.data.common.Address;
import org.springframework.data.geo.Point;

import java.util.Date;

/**
 * Created by licheng on 2015/9/19.
 * 用户创建的活动信息
 */
public class Activity {
    private String id;
    private String organizer;

    private String type;
    private String pay;

    private Address address;
    private Point point;

    private boolean transfer;
    private Date start;
    private Date end;

}
