package com.gongpingjia.carplay.entity.user;

/**
 * Created by licheng on 2015/10/10.
 */
public class DriverLicense {
    //证号
    private String code;
    //姓名
    private String name;
    //性别
    private String gender;
    //国籍
    private String nationality;
    //住址
    private String address;
    //出生日期
    private Long birthday;
    //初次领证日期
    private Long issueDate;
    //准驾车型
    private String drivingClass;
    //有效起始日期
    private Long validFrom;
    //有效期限
    private Integer validFor;
    //发证交警大队
    private String police;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public String getDrivingClass() {
        return drivingClass;
    }

    public void setDrivingClass(String drivingClass) {
        this.drivingClass = drivingClass;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Integer getValidFor() {
        return validFor;
    }

    public void setValidFor(Integer validFor) {
        this.validFor = validFor;
    }

    public String getPolice() {
        return police;
    }

    public void setPolice(String police) {
        this.police = police;
    }
}
