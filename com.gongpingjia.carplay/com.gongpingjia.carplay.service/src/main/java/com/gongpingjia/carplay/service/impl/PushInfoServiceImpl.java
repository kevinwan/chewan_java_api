package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.common.MessageDao;
import com.gongpingjia.carplay.dao.history.AlbumViewHistoryDao;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Message;
import com.gongpingjia.carplay.entity.history.AlbumViewHistory;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.PushInfoService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private MessageDao messageDao;

    /**
     * @param userId
     * step 1 获取感兴趣的人信息：  查询感兴趣的人中所有的创建的活动的 按照时间的 倒排；即离当前时间最近的 活动；
     *
     * step 2 查询 别人邀请我的/我邀请别人的 appointment 最近的一个 appointment
     *
     * step 3 有人看过我的照片 取最近的一条；
     *
     * step 4 谁关注我 取最近的一条；
     *
     * step 5 获取官方认证等信息；
     */
    @Override
    public ResponseDo getPushInfo(String userId) {
        LOG.debug("getPushInfo");
        JSONObject jsonObject = new JSONObject();
        getSubscribeUserInfo(jsonObject, userId);
        getAppointmentInfo(jsonObject, userId);
        getAlbumViewHistoryInfo(jsonObject, userId);
        getSubscriberInfo(jsonObject, userId);
        getOfficialInfo(jsonObject, userId);
        return ResponseDo.buildSuccessResponse(jsonObject);
    }


    //获取关注的所有的人创建的 所有的活动 按照 创建时间的 倒排序；
    private void getSubscribeUserInfo(JSONObject json, String userId) {
        List<Subscriber> subscriberList = subscriberDao.find(Query.query(Criteria.where("toUser").is(userId)));
        if (subscriberList == null || subscriberList.size() == 0) {
            //没有关注人
            return;
        }
        List<String> subUserIds = new ArrayList<>(subscriberList.size());

        for (Subscriber subscriber : subscriberList) {
            subUserIds.add(subscriber.getFromUser());
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.where("_id").in(subUserIds);
        query.addCriteria(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        List<Activity> activities = activityDao.find(query);
        if (activities == null || activities.size() == 0) {
            return;
        }
        Activity lastActivity = activities.get(0);
        User user = userDao.findById(lastActivity.getUserId());
        json.put("interests", user);
    }

    /**
     * @param json
     * @param userId
     * 获取时间最近的 第一个 约会/被约会信息；
     */
    private void getAppointmentInfo(JSONObject json, String userId) {
        //自己创建的约会需要是 已接受；别人创建的约会必须是申请中
        //TODO 暂时约定
        Criteria criteria = Criteria.where("applyUserId").is(userId).where("acceptInvited").is("1").orOperator(Criteria.where("invitedUserId").is(userId).and("acceptInvited").is("0"));
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        Appointment appointment = appointmentDao.findOne(query);
        json.put("appointments", appointment);
    }

    /**
     * @param json
     * @param userId
     * 获取相册访问记录最近的一条信息；
     */
    private void getAlbumViewHistoryInfo(JSONObject json, String userId) {
        User user = userDao.findById(userId);
        Criteria criteria = new Criteria();
//        criteria.where("albumId").in(user.getUserAlbum());
        AlbumViewHistory viewPhoto = albumViewHistoryDao.findOne(Query.query(criteria).with(new Sort(new Sort.Order(Sort.Direction.DESC, "viewTime"))).limit(10));
        json.put("viewPhoto", viewPhoto);
    }

    /**
     * 获取最近的关注我的 人 User 10条记录；
     * @param json
     * @param userId
     */
    private void getSubscriberInfo(JSONObject json, String userId) {
        Criteria criteria = new Criteria();
        criteria.where("toUser").is(userId);
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "subscribeTime")));
//        query.limit(10);
        Subscriber subscriber = subscriberDao.findOne(query);
        json.put("subscriber", subscriber);
    }

    //TODO
    //获取官方活动；
    //读取message中的信息；

    /**
     *@param json
     * @param userId
     */
    public void getOfficialInfo(JSONObject json, String userId) {
        Criteria criteria = Criteria.where("toUser").is(userId).where("checked").is(false);
        Query query = Query.query(criteria).with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        Message official = messageDao.findOne(query);
        json.put("official", official);
    }
}
