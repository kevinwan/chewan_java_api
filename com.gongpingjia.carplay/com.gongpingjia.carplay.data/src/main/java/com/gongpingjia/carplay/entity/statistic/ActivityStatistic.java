package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/10/27.
 * 对活动的操作的统计信息
 */
@Document
public class ActivityStatistic {

    @Id
    private String id;

    //访问path
    private String path;

    //接口类型
    private String interfaceType;

    //活动大类
    private String majorType;

    //活动小类
    private String type;

    //记录时间
    private Long createTime;

    //日期字符串
    private String dayString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }
}
