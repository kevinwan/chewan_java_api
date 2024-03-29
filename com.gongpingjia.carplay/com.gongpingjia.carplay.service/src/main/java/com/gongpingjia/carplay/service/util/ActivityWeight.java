package com.gongpingjia.carplay.service.util;

import com.gongpingjia.carplay.entity.activity.Activity;

/**
 * Created by Administrator on 2015/9/23.
 */
public class ActivityWeight implements Comparable<ActivityWeight> {


    public static final long MAX_PUB_TIME = 60000L;

    //20km
    public static final double DEFAULT_MAX_DISTANCE = 20000;

    private Activity activity;

    private double distance;

    //车主认证；
    private boolean carOwnerFlag = false;

    //头像认证
    private boolean avatarFlag = false;

    //身份认证
    private boolean identityFlag = false;

    private double weight;


    public ActivityWeight(Activity activity) {
        this.activity = activity;
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isCarOwnerFlag() {
        return carOwnerFlag;
    }

    public void setCarOwnerFlag(boolean carOwnerFlag) {
        this.carOwnerFlag = carOwnerFlag;
    }

    public boolean isAvatarFlag() {
        return avatarFlag;
    }

    public void setAvatarFlag(boolean avatarFlag) {
        this.avatarFlag = avatarFlag;
    }

    public boolean isIdentityFlag() {
        return identityFlag;
    }

    public void setIdentityFlag(boolean identityFlag) {
        this.identityFlag = identityFlag;
    }


    //排序规则是 从大到小  weight 越大 越靠前
    @Override
    public int compareTo(ActivityWeight compare) {
        if (this.getWeight() > compare.getWeight()) {
            return -1;
        } else if (this.getWeight() == compare.getWeight()) {
            return 0;
        } else {
            return 1;
        }
    }
}
