package com.gongpingjia.carplay.po;

public class SeatReservation {
    private String id;

    private String activityid;

    private String carid;

    private String userid;

    private Long createtime;

    private Long booktime;

    private Integer seatindex;

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

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Long getBooktime() {
        return booktime;
    }

    public void setBooktime(Long booktime) {
        this.booktime = booktime;
    }

    public Integer getSeatindex() {
        return seatindex;
    }

    public void setSeatindex(Integer seatindex) {
        this.seatindex = seatindex;
    }
}