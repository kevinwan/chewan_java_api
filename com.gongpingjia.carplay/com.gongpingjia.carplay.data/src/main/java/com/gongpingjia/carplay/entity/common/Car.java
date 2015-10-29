package com.gongpingjia.carplay.entity.common;

import org.springframework.util.StringUtils;

/**
 * Created by licheng on 2015/9/21.
 */
public class Car {

    private String brand;

    private String logo;

    private String model;

    /**
     * 给logo添加服务器前缀
     *
     * @param photoServerUrl 服务器前缀
     */
    public void refreshPhotoInfo(String photoServerUrl) {
        if (!StringUtils.isEmpty(this.logo)) {
            if (!photoServerUrl.startsWith("http://")) {
                this.logo = photoServerUrl + this.logo;
            }
        }
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
}
