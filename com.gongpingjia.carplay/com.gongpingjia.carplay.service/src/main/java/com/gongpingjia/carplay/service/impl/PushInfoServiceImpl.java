package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.history.AlbumViewHistoryDao;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.history.AlbumViewHistory;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.PushInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/24.
 */
@Service("pushInfoService")
public class PushInfoServiceImpl implements PushInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(PushInfoServiceImpl.class);

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AlbumViewHistoryDao albumViewHistoryDao;

    @Autowired
    private SubscriberDao subscriberDao;

    @Autowired
    private AuthenticationHistoryDao authenticationHistoryDao;

    /**
     * @param userId step 1 获取感兴趣的人信息：  查询感兴趣的人中所有的创建的活动的 按照时间的 倒排；即离当前时间最近的 活动；
     *               <p/>
     *               step 2 查询 别人邀请我的/我邀请别人的 appointment 最近的一个 appointment
     *               <p/>
     *               step 3 有人看过我的照片 取最近的一条；
     *               <p/>
     *               step 4 谁关注我 取最近的一条；
     *               <p/>
     *               step 5 获取官方认证等信息；
     */
    @Override
    public ResponseDo getPushInfo(String userId) {
        LOG.debug("getPushInfo");
        Map<String, Object> resultMap = new HashMap<>();
        getSubscribeUserInfo(resultMap, userId);
        getAppointmentInfo(resultMap, userId);
        getAlbumViewHistoryInfo(resultMap, userId);
        getSubscriberInfo(resultMap, userId);
        getOfficialInfo(resultMap, userId);
        return ResponseDo.buildSuccessResponse(resultMap);
    }


    //获取关注的所有的人创建的 所有的活动 按照 创建时间的 倒排序；
    private void getSubscribeUserInfo(Map<String, Object> json, String userId) {
        //获取所有的关注人；
        List<Subscriber> subscriberList = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        if (subscriberList.isEmpty()) {
            LOG.info("No subscriber users");
            //没有关注人
            return;
        }
        List<String> subUserIds = new ArrayList<>(subscriberList.size());
        for (Subscriber subscriber : subscriberList) {
            subUserIds.add(subscriber.getToUser());
        }

        //查询所有的关注人中创建时间最近的一条活动信息；
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").in(subUserIds);
        query.addCriteria(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        Activity lastActivity = activityDao.findOne(query);

        //获取该活动的创建者的信息；
        if (lastActivity == null) {
            json.put("interests", "");
        } else {
            User user = userDao.findById(lastActivity.getUserId());
            if (null == user) {
                json.put("interests", "");
            }
            json.put("interests", user);
        }
    }

    /**
     * @param json
     * @param userId 获取时间最近的 第一个 约会/被约会信息；
     */
    private void getAppointmentInfo(Map<String, Object> json, String userId) {
        // userId = invitedUserId 即 这个活动是自己创建的， 别人可以申请加入 状态应该是    申请中； 别人申请；
        // userId = applyUserId 即这个活动 是别人创建的，我是申请的， 需要看到 状态是  接受 或者是 拒绝；
        Criteria criteria = Criteria.where("applyUserId").is(userId).and("acceptInvited")
                .in(Constants.AppointmentStatus.ACCEPT, Constants.AppointmentStatus.REJECT)
                .orOperator(Criteria.where("invitedUserId").is(userId).and("acceptInvited").is(Constants.AppointmentStatus.APPLYING));
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        Appointment appointment = appointmentDao.findOne(query);
        if (appointment == null) {
            json.put("appointments", "");
        } else {
            json.put("appointments", appointment);
        }
    }

    /**
     * @param json
     * @param userId 获取相册访问记录最近的一条信息；
     */
    private void getAlbumViewHistoryInfo(Map<String, Object> json, String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "viewTime")));
        AlbumViewHistory viewPhoto = albumViewHistoryDao.findOne(query);
        if (viewPhoto == null) {
            json.put("viewPhoto", "");
        } else {
            json.put("viewPhoto", viewPhoto);
        }
    }

    /**
     * 获取最近的关注我的 人 最近的一条记录；
     *
     * @param json
     * @param userId
     */
    private void getSubscriberInfo(Map<String, Object> json, String userId) {
        Criteria criteria = Criteria.where("toUser").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "subscribeTime")));
        Subscriber subscriber = subscriberDao.findOne(query);
        if (subscriber == null) {
            json.put("subscriber", "");
        } else {
            json.put("subscriber", subscriber);
        }
    }

    //获取官方活动；

    /**
     * @param json
     * @param userId
     */
    public void getOfficialInfo(Map<String, Object> json, String userId) {
        Criteria criteria = Criteria.where("applyUserId").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "authTime")));
        AuthenticationHistory official = authenticationHistoryDao.findOne(query);
        if (official == null) {
            json.put("official", "");
        } else {
            json.put("official", official);
        }
    }
}
