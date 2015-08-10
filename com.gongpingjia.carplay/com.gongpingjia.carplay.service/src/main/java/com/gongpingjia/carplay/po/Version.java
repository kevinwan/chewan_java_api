package com.gongpingjia.carplay.po;

public class Version {
    private String product;

    private String version;

    private Byte forceupgrade;

    private String url;

    private String remarks;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Byte getForceupgrade() {
        return forceupgrade;
    }

    public void setForceupgrade(Byte forceupgrade) {
        this.forceupgrade = forceupgrade;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}