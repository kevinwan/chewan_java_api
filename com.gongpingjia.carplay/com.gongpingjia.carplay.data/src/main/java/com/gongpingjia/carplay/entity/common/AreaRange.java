package com.gongpingjia.carplay.entity.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Administrator on 2015/11/11.
 */
@Document
public class AreaRange implements Comparable<AreaRange> {

    @Id
    private String id;

    @Indexed
    private Integer code;

    private Double maxLongitude;

    private Double minLongitude;

    private Double maxLatitude;

    private Double minLatitude;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Double getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(Double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public Double getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(Double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public Double getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(Double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public Double getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(Double minLatitude) {
        this.minLatitude = minLatitude;
    }


    @Override
    public int compareTo(AreaRange o) {
        if (o.getCode() > this.getCode()) {
            return  -1;
        }else {
            return  1;
        }
    }
}
