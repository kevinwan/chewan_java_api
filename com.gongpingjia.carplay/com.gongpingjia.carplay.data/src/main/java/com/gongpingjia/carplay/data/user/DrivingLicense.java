package com.gongpingjia.carplay.data.user;

import java.util.Date;

/**
 * Created by licheng on 2015/9/19.
 * 用户行驶证
 */
public class DrivingLicense {
    private String name;
    private String plate;
    private String vehicleType;
    private String address;
    private String model;
    private String vehicleNumber;
    private String engineNumber;
    private Date registerTime;
    private Date issueTime;

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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }


    @Override
    public String toString() {
        return "DrivingLicense{" +
                "name='" + name + '\'' +
                ", plate='" + plate + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", address='" + address + '\'' +
                ", model='" + model + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", engineNumber='" + engineNumber + '\'' +
                ", registerTime=" + registerTime +
                ", issueTime=" + issueTime +
                '}';
    }
}
