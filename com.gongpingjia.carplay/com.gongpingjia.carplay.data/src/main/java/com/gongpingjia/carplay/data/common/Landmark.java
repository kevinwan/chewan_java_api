package com.gongpingjia.carplay.data.common;

/**
 * Created by licheng on 2015/9/21.
 */
public class Landmark {

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

    @Override
    public String toString() {
        return "Landmark{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
