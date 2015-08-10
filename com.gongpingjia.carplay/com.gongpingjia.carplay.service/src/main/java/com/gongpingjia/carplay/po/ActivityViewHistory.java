package com.gongpingjia.carplay.po;

public class ActivityViewHistory {
    private String id;

    private String activityid;

    private String userid;

    private Long viewtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getViewtime() {
        return viewtime;
    }

    public void setViewtime(Long viewtime) {
        this.viewtime = viewtime;
    }
}