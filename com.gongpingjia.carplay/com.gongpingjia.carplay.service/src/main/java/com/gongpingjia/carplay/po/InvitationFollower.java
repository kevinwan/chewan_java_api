package com.gongpingjia.carplay.po;

public class InvitationFollower {
    private String id;

    private String invitationid;

    private String userid;

    private Long jointime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvitationid() {
        return invitationid;
    }

    public void setInvitationid(String invitationid) {
        this.invitationid = invitationid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getJointime() {
        return jointime;
    }

    public void setJointime(Long jointime) {
        this.jointime = jointime;
    }
}