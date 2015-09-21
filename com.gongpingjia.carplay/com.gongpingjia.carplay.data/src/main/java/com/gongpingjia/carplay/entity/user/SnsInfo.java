package com.gongpingjia.carplay.entity.user;

/**
 * Created by licheng on 2015/9/19.
 * <p/>
 * 第三方登录相关内容
 */
public class SnsInfo {
    private String uid;

    private String name;

    private String photo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "SnsInfo{" +
                "id='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
