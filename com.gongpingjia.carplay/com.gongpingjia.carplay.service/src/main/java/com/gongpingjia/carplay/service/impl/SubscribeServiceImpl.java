package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.SubscribeService;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

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

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Override
    public ResponseDo getUserSubscribeInfo(String userId, String token) throws ApiException {
        LOG.debug("getUserSubscribeInfo was called");

        checker.checkUserInfo(userId, token);

        User myself = userDao.findById(userId);

        //我关注的人
        List<Subscriber> mySubscribe = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        Set<String> mySubscribeSet = new HashSet<>(mySubscribe.size());
        for (Subscriber item : mySubscribe) {
            mySubscribeSet.add(item.getToUser());
        }

        //关注我的人
        List<Subscriber> beSubscribed = subscriberDao.find(Query.query((Criteria.where("toUser").is(userId))));
        Set<String> beSubscribedSet = new HashSet<>(beSubscribed.size());
        for (Subscriber item : beSubscribed) {
            beSubscribedSet.add(item.getFromUser());
        }

        //相互关注的人
        Set<String> eachSubUserSet = new HashSet<String>();
        for (String item : mySubscribeSet) {
            if (beSubscribedSet.contains(item)) {
                eachSubUserSet.add(item);
                beSubscribedSet.remove(item);
            }
        }
        mySubscribeSet.removeAll(eachSubUserSet);

        LOG.debug("query related users");
        List<User> mySubscribeUsers = userDao.find(Query.query(Criteria.where("userId").in(mySubscribeSet)));
        List<User> eachSubscribeUsers = userDao.find(Query.query(Criteria.where("userId").in(eachSubUserSet)));
        List<User> beSubscribedUsers = userDao.find(Query.query(Criteria.where("userId").in(beSubscribedSet)));

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

        User toUser = userDao.findById(userSubscription.getToUser());
        if (toUser == null) {
            LOG.warn("toUser not exist");
            throw new ApiException("关注用户不存在");
        }

        User fromUser = userDao.findById(userSubscription.getFromUser());

        // 是否已关注
        Subscriber userSub = subscriberDao.findOne(Query.query(Criteria.where("fromUser").is(userSubscription.getFromUser())
                .and("toUser").is(userSubscription.getToUser())));
        if (userSub != null) {
            LOG.warn("already listened to this person before");
            throw new ApiException("已关注该用户，请勿重复关注");
        }

        userSubscription.setSubscribeTime(DateUtil.getTime());
        // 关注
        subscriberDao.save(userSubscription);

        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.subscribe", "{0}关注了我"),
                fromUser.getNickname());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.SUBSCRIBE,
                toUser.getEmchatName(), message);

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo unPayAttention(Subscriber userSubscription, String token) throws ApiException {
        LOG.debug("un pay attention to other user");

        checker.checkUserInfo(userSubscription.getFromUser(), token);
        // 是否已关注
        Subscriber userSub = subscriberDao.findOne(Query.query(Criteria.where("fromUser").is(userSubscription.getFromUser())
                .and("toUser").is(userSubscription.getToUser())));
        if (userSub == null) {
            LOG.warn("cannot unlisten as not listened before");
            throw new ApiException("没有关注该用户");
        }

        subscriberDao.deleteById(userSub.getId());

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getUserSubscribedHistory(String userId, String token, Integer limit, Integer ignore) throws ApiException {
        LOG.debug("getUserSubscribedHistory was called");

        checker.checkUserInfo(userId, token);
        User myself = userDao.findById(userId);

        List<Subscriber> beSubscribed = subscriberDao.find(Query.query((Criteria.where("toUser").is(userId)))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "subscribeTime")))
                .skip(ignore).limit(limit));

        List<String> subUserIdSet = new ArrayList<String>(beSubscribed.size());
        for (Subscriber sub : beSubscribed) {
            subUserIdSet.add(sub.getFromUser());
        }

        //获取关注我的用户的信息
        List<User> users = userDao.findByIds(subUserIdSet);

        refreshUserinfo(myself, users);

        return ResponseDo.buildSuccessResponse(users);
    }

    /**
     * 刷新用户信息
     *
     * @param myself
     * @param mySubscribeUsers
     */
    private void refreshUserinfo(User myself, List<User> mySubscribeUsers) {
        for (User user : mySubscribeUsers) {
            user.hideSecretInfo();
            user.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
            user.setDistance(DistanceUtil.getDistance(user.getLandmark().getLongitude(), user.getLandmark().getLatitude(),
                    myself.getLandmark().getLongitude(), myself.getLandmark().getLatitude()));
        }
    }

}
