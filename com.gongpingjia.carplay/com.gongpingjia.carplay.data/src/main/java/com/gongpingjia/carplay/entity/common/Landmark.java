package com.gongpingjia.carplay.entity.common;

/**
 * Created by licheng on 2015/9/21.
 * 经纬度信息
 */
public class Landmark {

    private static final Double LONGITUDE_LIMIT = 180D;
    private static final Double LATITUDE_LIMIT = 90D;

    private Double longitude;

    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public Landmark(){

    }

    public Landmark(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 检查经纬度信息是否正确
     *
     * @return
     */
    public boolean correct() {
        if (this.latitude == null || this.latitude == null) {
            return false;
        }

        if (this.longitude <= -LONGITUDE_LIMIT || this.longitude >= LONGITUDE_LIMIT) {
            return false;
        }

        if (this.latitude <= -LATITUDE_LIMIT || this.latitude >= LATITUDE_LIMIT) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Landmark{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
