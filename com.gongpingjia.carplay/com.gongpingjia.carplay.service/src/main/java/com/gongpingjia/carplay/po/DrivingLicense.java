package com.gongpingjia.carplay.po;

public class DrivingLicense {
    private String userid;

    private String name;

    private String plate;

    private String vehicletype;

    private String address;

    private String model;

    private String vehiclenumber;

    private String enginenumber;

    private Long registertime;

    private Long issuetime;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehiclenumber() {
        return vehiclenumber;
    }

    public void setVehiclenumber(String vehiclenumber) {
        this.vehiclenumber = vehiclenumber;
    }

    public String getEnginenumber() {
        return enginenumber;
    }

    public void setEnginenumber(String enginenumber) {
        this.enginenumber = enginenumber;
    }

    public Long getRegistertime() {
        return registertime;
    }

    public void setRegistertime(Long registertime) {
        this.registertime = registertime;
    }

    public Long getIssuetime() {
        return issuetime;
    }

    public void setIssuetime(Long issuetime) {
        this.issuetime = issuetime;
    }
}