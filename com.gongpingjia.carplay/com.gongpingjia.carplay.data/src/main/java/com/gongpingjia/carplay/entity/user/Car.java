package com.gongpingjia.carplay.data.user;

/**
 * Created by licheng on 2015/9/21.
 */
public class Car {

    private String id;

    private String userId;

    private String brand;

    private String logo;

    private String model;

    private String color;

    private Double price;

    private Integer seat;

    private String extraInfo;

    private boolean invalid;

    private String slug;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", brand='" + brand + '\'' +
                ", logo='" + logo + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", price=" + price +
                ", seat=" + seat +
                ", extraInfo='" + extraInfo + '\'' +
                ", invalid=" + invalid +
                ", slug='" + slug + '\'' +
                '}';
    }
}
