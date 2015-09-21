package com.gongpingjia.carplay.data.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by licheng on 2015/9/21.
 */
@Document
public class Version {

    @Id
    private String id;

    @Indexed(unique = true)
    private String product;

    private String url;

    private Integer forceUpdate;

    private String remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Integer forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Version{" +
                "id='" + id + '\'' +
                ", product='" + product + '\'' +
                ", url='" + url + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
