package com.gongpingjia.carplay.service.util;

import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2015/9/23.
 */
public class ActivityUtil {

    public static List<Activity> getSortResult(List<Activity> activities, Date currentTime, Landmark nowLandmark, double maxDistance,long maxPubTime
                                                    ,int skip,int limit) {
        List<ActivityWeight> activityWeights =    getPageInfo(sortActivityList(activities, currentTime, nowLandmark, maxDistance, maxPubTime), skip, limit);
        ArrayList<Activity> rltList = new ArrayList<>(activityWeights.size());
        for (ActivityWeight activityWeight : activityWeights) {
            rltList.add(activityWeight.getActivity());
        }
        return rltList;
    }


    private static List<ActivityWeight> sortActivityList(List<Activity> activities, Date currentTime, Landmark nowLandmark, double maxDistance,long maxPubTime) {
        ArrayList<ActivityWeight> awList = new ArrayList<>(activities.size());
        for (Activity activity : activities) {
            ActivityWeight aw = new ActivityWeight(activity);
            User user = activity.getOrganizer();
            //  车主认证；
            if (StringUtils.equals(user.getLicenseAuthStatus(), Constants.AuthStatus.ACCEPT)) {
                aw.setCarOwnerFlag(true);
            }
            //头像认证
            if (StringUtils.equals(user.getPhotoAuthStatus(), Constants.AuthStatus.ACCEPT)) {
                aw.setAvatarFlag(true);
            }
            //TODO
            //身份认证 现在没有提供身份认证接口

            //初始化权重
            initWeight(currentTime, nowLandmark, aw, maxDistance,maxPubTime);
            awList.add(aw);
        }
        Collections.sort(awList);
        return awList;
    }

    private  static void initWeight(Date currentTime, Landmark nowLandmark, ActivityWeight activityWeight, double maxDistance,long maxPubTime) {
        Activity activity = activityWeight.getActivity();
        double weight = 0;
        double distance = DistanceUtil.getDistance(nowLandmark.getLongitude(), nowLandmark.getLatitude(), activity.getDestPoint().getLongitude(), activity.getDestPoint().getLatitude());
        activityWeight.setDistance(distance);
        activityWeight.getActivity().setDistance(distance);
        double distanceRate = 1 - distance / maxDistance;
        weight += distanceRate * 0.2;
        double timeRate = 1 - ((currentTime.getTime() - activity.getStart()) / (1000 * 60)) / maxPubTime;
        weight += timeRate;
        //车主认证
        if (activityWeight.isCarOwnerFlag()) {
            weight += 0.15;
        }
        //头像认证
        if (activityWeight.isAvatarFlag()) {
            weight += 0.25;
        }
        //身份认证
        if (activityWeight.isIdentityFlag()) {
            weight += 0.1;
        }
        if (activity.getDestination() != null) {
            weight += 0.15;
        }
        if (activity.getStart() != null) {
            weight += 0.05;
        }
        activityWeight.setWeight(weight);
    }
    
    

    private static List<ActivityWeight> getPageInfo(List<ActivityWeight> awList, int skip, int limit) {
        //访问更高效点
        int offset = 0;
        int len = 0;
        if (skip > awList.size()) {
            return null;
        }
        if (skip + limit > awList.size()) {
            offset = awList.size();
        } else {
            offset = skip + limit;
        }
        len = offset - skip;
        ArrayList<ActivityWeight> toList = new ArrayList<>(len);
        Iterator<ActivityWeight> iterator = awList.iterator();
        int index = 0;
        do {
            if (index >= skip) {
                toList.add(iterator.next());
            } else {
                iterator.next();
            }

        } while (iterator.hasNext() && index < offset);
        return toList;
//        if (skip > activities.size()) {
//            return null;
//        }
//        for (int index = skip; index < activities.size() && index < skip + limit; index++) {
//            toList.add(activities.get(index));
//        }
//        return toList;
    }

}
