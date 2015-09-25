package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.SubscribeService;
import com.gongpingjia.carplay.util.DistanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/9/24.
 */
@Service
public class SubscribeServiceImpl implements SubscribeService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeServiceImpl.class);

    @Autowired
    private ParameterChecker checker;

    @Autowired
    private SubscriberDao subscriberDao;

    @Autowired
    private UserDao userDao;

    @Override
    public ResponseDo getUserSubscribeInfo(String userId, String token) throws ApiException {
        LOG.debug("getUserSubscribeInfo was called");

        checker.checkUserInfo(userId, token);

        User myself = userDao.findById(userId);

        List<Subscriber> mySubscribe = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        List<Subscriber> beSubscribed = subscriberDao.find(Query.query((Criteria.where("toUser").is(userId))));
        List<Subscriber> eachSubscribe = new ArrayList<Subscriber>();

        List<String> mySubUserIds = new ArrayList<String>();
        List<String> beSubedUserIds = new ArrayList<String>();
        List<String> eachSubUserIds = new ArrayList<String>();

        for (Subscriber mysub : mySubscribe) {
            for (Subscriber besub : beSubscribed) {
                if (mysub.getToUser().equals(besub.getFromUser())) {
                    eachSubUserIds.add(mysub.getToUser());
                } else {
                    beSubedUserIds.add(besub.getFromUser());
                }
            }
            if (!eachSubUserIds.contains(mysub.getToUser())) {
                mySubUserIds.add(mysub.getToUser());
            }
        }

        LOG.debug("query related users");
        List<User> mySubscribeUsers = userDao.find(Query.query(Criteria.where("userId").in(mySubUserIds)));
        List<User> eachSubscribeUsers = userDao.find(Query.query(Criteria.where("userId").in(eachSubUserIds)));
        List<User> beSubscribedUsers = userDao.find(Query.query(Criteria.where("userId").in(beSubedUserIds)));

        LOG.debug("refresh user infomation");
        refreshUserinfo(myself, mySubscribeUsers);
        refreshUserinfo(myself, eachSubscribeUsers);
        refreshUserinfo(myself, beSubscribedUsers);

        Map<String, Object> data = new HashMap<String, Object>(3, 1);
        data.put("eachSubscribe", eachSubscribeUsers);
        data.put("mySubscribe", mySubscribeUsers);
        data.put("beSubscribed", beSubscribedUsers);

        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo payAttention(Subscriber userSubscription, String token) throws ApiException {
        LOG.debug("Pay attention to other user");

        checker.checkUserInfo(userSubscription.getFromUser(), token);
        User user = userDao.findById(userSubscription.getToUser());
        if (user == null) {
            LOG.warn("User not exist");
            throw new ApiException("关注用户不存在");
        }
        // 是否已关注
        Subscriber userSub = subscriberDao.findOne(Query.query(Criteria.where("fromUser").is(userSubscription.getFromUser()).and("toUser").is(userSubscription.getToUser())));
        if (userSub != null) {
            LOG.warn("already listened to this person before");
            throw new ApiException("已关注该用户，请勿重复关注");
        }
        userSubscription.setSubscribeTime(DateUtil.getTime());
        // 关注
        subscriberDao.save(userSubscription);

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo unPayAttention(Subscriber userSubscription, String token) throws ApiException {
        LOG.debug("un pay attention to other user");
        
        checker.checkUserInfo(userSubscription.getFromUser(), token);
        // 是否已关注
        Subscriber userSub = subscriberDao.findOne(Query.query(Criteria.where("fromUser").is(userSubscription.getFromUser()).and("toUser").is(userSubscription.getToUser())));
        if (userSub == null) {
            LOG.warn("cannot unlisten as not listened before");
            throw new ApiException("没有关注该用户，不能取消关注");
        }
        subscriberDao.deleteById(userSub.getId());

        return ResponseDo.buildSuccessResponse();
    }

    private void refreshUserinfo(User myself, List<User> mySubscribeUsers) {
        String avatarServer = CommonUtil.getLocalPhotoServer();
        for (User user : mySubscribeUsers) {
            user.setDistance(DistanceUtil.getDistance(user.getLandmark().getLongitude(), user.getLandmark().getLatitude(),
                    myself.getLandmark().getLongitude(), myself.getLandmark().getLatitude()));
            user.setAvatar(avatarServer + user.getAvatar());
        }
    }

}
