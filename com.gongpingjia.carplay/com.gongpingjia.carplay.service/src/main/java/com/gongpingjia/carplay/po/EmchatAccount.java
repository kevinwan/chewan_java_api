package com.gongpingjia.carplay.po;

public class EmchatAccount {
    private String userid;

    private String username;

    private String nickname;

    private String password;

    private Long registertime;

    private Long activatetime;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRegistertime() {
        return registertime;
    }

    public void setRegistertime(Long registertime) {
        this.registertime = registertime;
    }

    public Long getActivatetime() {
        return activatetime;
    }

    public void setActivatetime(Long activatetime) {
        this.activatetime = activatetime;
    }
}