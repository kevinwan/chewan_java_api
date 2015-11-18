package com.gongpingjia.carplay.entity.statistic;

import org.springframework.data.annotation.Id;

import java.util.Calendar;

/**
 * Created by 123 on 2015/10/28.
 */
public class StatisticParent {

    @Id
    private String id;

    //埋点事件名称(Android / IOS传递)
    private String event;

    private Integer count;

    private Long createTime;

    //日期字符串YYYYMMDD
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;

    private Integer week;

    public void recordTime(Long current) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        this.createTime = current;
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.week = calendar.getWeeksInWeekYear();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }
}
