package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import com.gongpingjia.carplay.dao.history.InterestMessageDao;
import com.gongpingjia.carplay.dao.statistic.StatisticActivityMatchDao;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.PushInfo;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.history.InterestMessage;
import com.gongpingjia.carplay.entity.statistic.StatisticActivityMatch;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by heyongyu on 2015/9/22.
 */
@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private UserDao userDao;


    @Autowired
    private ActivityDao activityDao;


    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private SubscriberDao subscriberDao;

    @Autowired
    private InterestMessageDao interestMessageDao;

    @Autowired
    private PushInfoDao pushInfoDao;

    @Autowired
    private StatisticActivityMatchDao statisticActivityMatchDao;


    /**
     * 注册 活动；
     *
     * @param userId
     * @param activity
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo activityRegister(String userId, Activity activity) throws ApiException {
        LOG.debug("activityRegister");

        User user = userDao.findById(userId);

        Long current = DateUtil.getTime();

        saveUserActivity(userId, activity, current);

        saveInterestMessage(userId, activity, current);

        //向关注我的人发送感兴趣的信息
        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.interest", "{0}想找人一起{1}"),
                user.getNickname(), activity.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.INTEREST,
                buildUserSubscribers(userId), message, ext);

        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.NEARBY,
                buildNearByUsers(user, activity.getActivityId()), message, ext);

        return ResponseDo.buildSuccessResponse(activity.getActivityId());
    }

    private void saveInterestMessage(String userId, Activity activity, Long current) {
        InterestMessage oldMessage = interestMessageDao.findOne(Query.query(Criteria.where("relatedId").is(activity.getActivityId())));
        if (oldMessage == null) {
            LOG.debug("Old message is not exist, with activityId:{}", activity.getActivityId());
            InterestMessage interestMessage = new InterestMessage();
            interestMessage.setUserId(userId);
            interestMessage.setType(InterestMessage.USER_ACTIVITY);
            interestMessage.setRelatedId(activity.getActivityId());
            interestMessage.setCreateTime(current);
            interestMessageDao.save(interestMessage);
        } else {
            LOG.debug("Old message is exist, with activityId:{}, update create time", activity.getActivityId());
            interestMessageDao.update(Query.query(Criteria.where("relatedId").is(activity.getActivityId())), Update.update("createTime", current));
        }
    }

    private void saveUserActivity(String userId, Activity activity, Long current) {
        Activity oldActivity = activityDao.findOne(Query.query(Criteria.where("userId").is(userId).and("deleteFlag").is(false)
                .and("majorType").is(activity.getMajorType()).and("type").is(activity.getType()))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"))));

        if (oldActivity == null) {
            LOG.debug("Old activity is not exist, create a new");
            //如果对应的人员的活动不存在，则重新建立活动
            activity.setActivityId(null);
            //设置活动的创建人ID
            activity.setUserId(userId);
            List<String> memberIds = new ArrayList<String>(1);
            //创建人默认加入到活动成员列表中
            memberIds.add(userId);
            activity.setMembers(memberIds);
            activity.setCreateTime(current);
            activity.setDeleteFlag(false);

            activityDao.save(activity);
        } else {
            LOG.debug("Old activity is exist, update activity info, activityId:{}", activity.getActivityId());
            //如果对应的人员的活动已经存在，仅更新新的信息
            Update update = new Update();
            update.set("pay", activity.getPay());
            update.set("destPoint", activity.getDestPoint());
            update.set("destination", activity.getDestination());
            update.set("estabPoint", activity.getEstabPoint());
            update.set("establish", activity.getEstablish());
            update.set("transfer", activity.isTransfer());
            update.set("createTime", current);
            update.set("applyIds", new ArrayList<>(0));
            activityDao.update(Query.query(Criteria.where("activityId").is(oldActivity.getActivityId())), update);

            activity.setActivityId(oldActivity.getActivityId());
        }
    }

    private List<String> buildNearByUsers(User user, String activityId) {
        LOG.debug("Send nearby user message, sendUser:{}", user.getUserId());
        PushInfo pushInfo = pushInfoDao.findOne(Query.query(Criteria.where("sendUserId").is(user.getUserId())));
        if (pushInfo != null) {            //用户的活动今天已经推送过一次
            LOG.info("User:{} has already send push once today", user.getUserId());
            return new ArrayList<>(0);
        }

        //用户今天没有发送过
        Double distance = Double.parseDouble(PropertiesUtil.getProperty("carplay.nearby.distance.limit", "6000")); //查找附近用户距离限制
        Set<String> subUserIdSet = getSubscribeUsers(user);
        LOG.debug("Get nearby users");
        List<User> nearbyUserList = userDao.find(Query.query(
                Criteria.where("landmark").near(new Point(user.getLandmark().getLongitude(), user.getLandmark().getLatitude()))
                        .maxDistance(distance * 180 / DistanceUtil.EARTH_RADIUS).and("deleteFlag").is(false)));
        Map<String, User> userMap = new HashMap<>(nearbyUserList.size());
        for (User item : nearbyUserList) {
            if (item.getUserId().equals(user.getUserId())) {
                continue;//忽略自己
            }
            if (subUserIdSet.contains(item.getUserId())) {
                continue;//关注我的人已经推送过感兴趣的，不能重复推送
            }
            userMap.put(item.getUserId(), item);
        }

        return buildEmchatNames(user, nearbyUserList, userMap, activityId);
    }

    /**
     * @param user           消息发送人员
     * @param nearbyUserList
     * @param userMap
     * @return
     */
    private List<String> buildEmchatNames(User user, List<User> nearbyUserList, Map<String, User> userMap, String activityId) {
        LOG.debug("Get already pushed message users");
        Long current = DateUtil.getTime();

        Integer pushLimit = Integer.parseInt(PropertiesUtil.getProperty("carplay.nearby.push.limit", "3"));// 每个用户推送的限制
        Set<String> pushedUsers = pushInfoDao.groupByReceivedUsers(userMap.keySet(), pushLimit); // 已经推送的信息

        Integer userLimit = Integer.parseInt(PropertiesUtil.getProperty("carplay.nearby.users.limit", "3"));//附近用户的限制
        List<String> emchatNames = new ArrayList<>(userLimit);//环信ID群组
        int count = 0;
        for (User item : nearbyUserList) {
            if (count >= userLimit) {
                break;   //已经达到了推送的上限，退出
            }
            if (!userMap.containsKey(item.getUserId())) {
                continue;//如果用户不在限制的userMap中，就走下一个循环
            }
            if (pushedUsers.contains(item.getUserId())) {
                continue;  //如果推送的消息已满的用户在其中，才发送
            }
            PushInfo info = new PushInfo();
            info.setSendUserId(user.getUserId());
            info.setReceivedUserId(item.getUserId());
            info.setCreateTime(current);
            info.setActivityId(activityId);
            pushInfoDao.save(info);

            emchatNames.add(item.getEmchatName());
            count++;
        }
        LOG.debug("Finished build push users");
        return emchatNames;
    }

    private Set<String> getSubscribeUsers(User user) {
        LOG.debug("Get subscribed users");
        List<Subscriber> subscriberList = subscriberDao.find(Query.query(Criteria.where("toUser").is(user.getUserId())));
        Set<String> subUserIdSet = new HashSet<>(subscriberList.size());
        for (Subscriber item : subscriberList) {
            subUserIdSet.add(item.getFromUser());
        }
        return subUserIdSet;
    }

    /**
     * 获取关注我的人的用户的Id列表
     *
     * @param userId 我的Id
     * @return 返回关注了我的Id的列表
     */
    private List<String> buildUserSubscribers(String userId) {
        //获取关注我的用户的信息列表
        List<Subscriber> subscribers = subscriberDao.find(Query.query(Criteria.where("toUser").is(userId)));
        List<String> userIds = new ArrayList<>(subscribers.size());
        for (Subscriber subscriber : subscribers) {
            userIds.add(subscriber.getFromUser());
        }

        List<User> users = userDao.find(Query.query(Criteria.where("userId").in(userIds)));
        List<String> emchatNames = new ArrayList<>(users.size());
        for (User user : users) {
            emchatNames.add(user.getEmchatName());
        }
        return emchatNames;
    }

    @Override
    public ResponseDo getActivityInfo(String userId, String activityId, Landmark landmark) throws ApiException {
        LOG.debug("getActivityInfo");

        Criteria criteria = Criteria.where("activityId").is(activityId);
        criteria.and("deleteFlag").is(false);
        Activity activity = activityDao.findOne(Query.query(criteria));
        if (null == activity) {
            LOG.warn("activity not exist");
            throw new ApiException("活动id 找不到对应的活动");
        }
        User organizer = userDao.findById(activity.getUserId());
        if (null == organizer) {
            LOG.error("the activity {} cannot found the organizer user {}", activityId, userId);
            throw new ApiException("该活动找不到对应的 User");
        }
        organizer.hideSecretInfo();
        organizer.setAlbum(new ArrayList<Photo>(0));
        organizer.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());

        activity.setOrganizer(organizer);
        if (landmark.getLatitude() != null && landmark.getLongitude() != null) {
            activity.setDistance(DistanceUtil.getDistance(landmark, activity.getEstabPoint()));
        }
        return ResponseDo.buildSuccessResponse(activity);
    }


    @Override
    public ResponseDo sendAppointment(String activityId, String userId, Appointment appointment) throws ApiException {
        Activity activity = activityDao.findOne(Query.query(Criteria.where("activityId").is(activityId)));
        if (activity == null) {
            LOG.warn("No activity exist : {}", activityId);
            throw new ApiException("未找到该活动");
        }

        List<String> members = activity.getMembers();
        for (String member : members) {
            if (member.equals(userId)) {
                LOG.warn("Already be a member");
                throw new ApiException("已是成员，不能重复申请加入活动");
            }
        }

        Appointment appointmentData = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId)
                .and("applyUserId").is(userId)
                .and("status").is(Constants.AppointmentStatus.APPLYING)));
        if (appointmentData != null) {
            LOG.warn("already applying for this activity");
            throw new ApiException("该活动已处于申请中，请勿重复申请");
        }

        User user = userDao.findById(userId);

        Long current = DateUtil.getTime();
        appointment.setActivityId(activity.getActivityId());
        appointment.setApplyUserId(userId);
        appointment.setInvitedUserId(activity.getUserId());
        appointment.setCreateTime(current);
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setModifyTime(current);
        appointment.setActivityCategory(Constants.ActivityCatalog.COMMON);
        //appointment的 destination为 activity的 destination  destPoint 为 活动的 estabPoint
        //appointment 的 estabPoint 为 applyUserId 当前的 landmark
        appointment.setDestPoint(activity.getEstabPoint());
        appointment.setDestination(activity.getDestination());
        appointment.setEstabPoint(user.getLandmark());
        appointment.setDistance(DistanceUtil.getDistance(appointment.getEstabPoint(), appointment.getDestPoint()));
        appointmentDao.save(appointment);

        //将当前用户ID加入到申请人列表中
        Update update = new Update();
        update.addToSet("applyIds", userId);
        activityDao.update(activityId, update);

        //发送环信推送消息
        User organizer = userDao.findById(activity.getUserId());
        User applier = userDao.findById(userId);
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.activity.invite", "{0}邀请您{1}"),
                applier.getNickname(), activity.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.ACTIVITY_STATE,
                organizer.getEmchatName(), message);

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 申请加入活动
     *
     * @param appointmentId
     * @param userId
     * @param acceptFlag
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo processAppointment(String appointmentId, String userId, boolean acceptFlag) throws ApiException {
        LOG.debug("applyJoinActivity, appointmentId:{}, accept:{}", appointmentId, acceptFlag);
        Appointment appointment = checkAppointment(appointmentId, userId);

//        Activity activity = checkAppointmentActivity(appointment);
        //判断申请用户是否存在
        User applyUser = userDao.findById(appointment.getApplyUserId());
        if (applyUser == null) {
            LOG.warn("Apply user is not exist");
            throw new ApiException("申请用户不存在");
        }

        int status = Constants.AppointmentStatus.ACCEPT;
        if (!acceptFlag) {
            //不同意
            status = Constants.AppointmentStatus.REJECT;
        }
        appointmentDao.update(appointmentId, Update.update("status", status).set("modifyTime", DateUtil.getTime()));

//        //同意
//        //添加到环信群组中；
//        try {
//            chatThirdPartyService.addUserToChatGroup(emchatTokenService.getToken(), activity.getEmchatGroupId(), applyUser.getEmchatName());
//        } catch (ApiException e) {
//            LOG.warn(e.getMessage(), e);
//            //添加环信用户失败；
//            throw new ApiException("添加到聊天群组失败");
//        }

//        //activity 中members 中添加该用户;
//        Update activityUpdate = new Update();
//        activityUpdate.addToSet("members", appointment.getApplyUserId());
//        activityDao.update(activity.getActivityId(), activityUpdate);

        User user = userDao.findById(userId);

        //接收了别人的请求，需要发送环信消息,只发送接受的
        if (acceptFlag) {
            String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.activity.state", "{0}{1}了您的{2}邀请"),
                    user.getNickname(), "接受", appointment.getType());
            chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.ACTIVITY_STATE,
                    applyUser.getEmchatName(), message);
        }

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 检查邀请的申请对应的活动信息
     *
     * @param appointment
     * @return
     * @throws ApiException
     */
    private Activity checkAppointmentActivity(Appointment appointment) throws ApiException {
        //判断活动是否存在
        Activity activity = activityDao.findById(appointment.getActivityId());
        if (activity == null) {
            LOG.warn("activity not exist");
            throw new ApiException("活动不存在");
        }

        for (String memberId : activity.getMembers()) {
            if (memberId.equals(appointment.getApplyUserId())) {
                LOG.warn("user has in the activity");
                throw new ApiException("用户已经在当前活动中");
            }
        }

        return activity;
    }

    /**
     * 检查appointment对象与user是否相符，如果不相符就不能操作
     *
     * @param appointmentId
     * @param userId
     * @return
     * @throws ApiException
     */
    private Appointment checkAppointment(String appointmentId, String userId) throws ApiException {
        //判断申请appointment 是否存在
        Appointment appointment = appointmentDao.findById(appointmentId);
        if (appointment == null) {
            LOG.warn("appoint not exist");
            throw new ApiException("该邀请不存在");
        }

        if (!userId.equals(appointment.getInvitedUserId())) {
            LOG.warn("appoint not belong this user");
            throw new ApiException("输入参数有误");
        }

        if (Constants.AppointmentStatus.APPLYING != appointment.getStatus()) {
            LOG.warn("appoint is applying");
            throw new ApiException("活动邀请已处理");
        }

        return appointment;
    }


    /**
     * 将activityList中的User信息从数据库中查询出来，并初始化
     *
     * @param activityList
     * @throws ApiException
     */
    private void initOrganizer(List<Activity> activityList, List<String> subscribeIds) throws ApiException {
        Set<String> userIdSet = new HashSet<>(activityList.size());
        for (Activity activity : activityList) {
            userIdSet.add(activity.getUserId());
        }
        List<User> users = userDao.findByIds(userIdSet);
        Map<String, User> userMap = new HashMap<>(users.size(), 1);
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }
        for (Activity activity : activityList) {
            User organizer = userMap.get(activity.getUserId());
            if (null == organizer) {
                throw new ApiException("数据非法 该Activity没有找到对应的Organizer");
            }
            organizer.setSubscribeFlag(subscribeIds.contains(organizer.getUserId()));
            activity.setOrganizer(organizer);
        }
    }


    /**
     *
     * 管理后台功能
     *
     *
     */


    /**
     * 管理后台用户 查找 用户活动 列表
     * <p/>
     * criteria:
     * city   对应 estabPoint.city
     * phone 根据 phone 查出对应的user  根据 userId 查出 对应的 activity
     * fromDate  toDate          对应 createTime
     * pay  -1 对应不限制              我请客 请我吧 AA制
     * type -1 全部           type值 对应着 type
     * transfer  -1 全部      true  false
     *
     * @param json
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo getUserActivityList(JSONObject json, String userId) throws ApiException {
        //TODO重构
        int draw = json.getInt("draw");
        int start = json.getInt("start");
        int length = json.getInt("length");

        JSONObject resultJson = new JSONObject();

        Query query = new Query();
        Criteria criteria = new Criteria();

        String startTimeStr = json.getString("fromTime");
        String endTimeStr = json.getString("toTime");
        if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {
            long startTime = TypeConverUtil.convertToLong("fromTime", startTimeStr, true);
            long endTime = TypeConverUtil.convertToLong("toTime", endTimeStr, true) + 24 * 60 * 60 * 1000;
            criteria.and("createTime").gte(startTime).lte(endTime);
        }

        String phone = json.getString("phone");
        if (StringUtils.isNotEmpty(phone)) {
            User user = userDao.findOne(Query.query(Criteria.where("phone").is(phone)));
            if (null == user || StringUtils.isEmpty(user.getUserId())) {
                throw new ApiException("未找到该号码发布的活动");
            }
            criteria.and("userId").is(user.getUserId());
        }

        criteria.and("deleteFlag").is(false);

        String province = json.getString("province");
        if (StringUtils.isNotEmpty(province)) {
            criteria.and("destination.province").is(province);
        }

        String city = json.getString("city");
        if (StringUtils.isNotEmpty(city)) {
            criteria.and("destination.city").is(city);
        }

        String majorType = json.getString("majorType");
        if (StringUtils.isNotEmpty(majorType)) {
            criteria.and("majorType").is(majorType);
        }

        String type = json.getString("type");
        if (StringUtils.isNotEmpty(type) && !StringUtils.equals(type, "-1")) {
            criteria.and("type").is(type);
        }

        String pay = json.getString("pay");
        if (StringUtils.isNotEmpty(pay) && !StringUtils.equals(pay, "-1")) {
            criteria.and("pay").is(pay);
        }

        String transferStr = json.getString("transfer");
        if (StringUtils.isNotEmpty(transferStr) && !StringUtils.equals(transferStr, "-1")) {
            criteria.and("transfer").is(TypeConverUtil.convertToBoolean("transfer", transferStr, true));
        }

        query.addCriteria(criteria);

        long totalNum = activityDao.count(query);

        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.skip(start).limit(length);
        List<Activity> activityList = activityDao.find(query);

        resultJson.put("draw", draw);
        resultJson.put("recordsFiltered", totalNum);
        resultJson.put("recordsTotal", totalNum);

        if (null == activityList || activityList.isEmpty()) {
            return ResponseDo.buildSuccessResponse(resultJson);
        }

        Set<String> userIdSet = new HashSet<>(activityList.size());
        for (Activity activity : activityList) {
            userIdSet.add(activity.getUserId());
        }

        List<User> userList = userDao.findByIds(userIdSet);

        Map<String, User> userMap = new HashMap<>(userList.size());
        if (null == userList || userList.isEmpty()) {
            throw new ApiException("用户列表为空 数据参数异常");
        }
        for (User user : userList) {
            userMap.put(user.getUserId(), user);
        }

        JSONArray jsonArray = new JSONArray();
        for (Activity activity : activityList) {
            JSONObject item = new JSONObject();
            item.put("activityId", activity.getActivityId());
            item.put("nickname", userMap.get(activity.getUserId()).getNickname());
            item.put("phone", userMap.get(activity.getUserId()).getPhone());
            item.put("establish", activity.getEstablish());
            item.put("destination", activity.getDestination());
            item.put("type", activity.getType());
            item.put("pay", activity.getPay());
            item.put("transfer", activity.isTransfer());
            item.put("createTime", activity.getCreateTime());
            jsonArray.add(item);
        }
        resultJson.put("activityList", jsonArray);

        return ResponseDo.buildSuccessResponse(resultJson);
    }


    @Override
    public ResponseDo updateUserActivity(JSONObject json, String activityId) throws ApiException {

        Update update = new Update();
        try {

            if (json.containsKey("destination") && StringUtils.isNotEmpty(json.getString("destination"))) {
                update.set("destination", JSONObject.toBean(json.getJSONObject("establish"), Address.class));
            }
            if (json.containsKey("destPoint") && StringUtils.isNotEmpty(json.getString("destination"))) {
                update.set("destPoint", JSONObject.toBean(json.getJSONObject("destPoint"), Landmark.class));
            }
            update.set("establish", JSONObject.toBean(json.getJSONObject("establish"), Address.class));
            update.set("estabPoint", JSONObject.toBean(json.getJSONObject("estabPoint"), Landmark.class));
            update.set("majorType", json.getString("majorType"));
            update.set("type", json.getString("type"));
            update.set("pay", json.getString("pay"));
            update.set("transfer", json.getBoolean("transfer"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ApiException("活动转换时出错");
        }
        Activity toFind = activityDao.findById(activityId);
        if (null == toFind) {
            throw new ApiException("该id对应的活动不存在");
        }
        activityDao.update(activityId, update);
        return ResponseDo.buildSuccessResponse();

    }

    @Override
    public ResponseDo viewUserActivity(String activityId) throws ApiException {
        Activity activity = activityDao.findById(activityId);
        if (null == activity) {
            throw new ApiException("该活动不存在");
        }
        User organizer = userDao.findById(activity.getUserId());
        if (null == organizer || StringUtils.isEmpty(organizer.getUserId())) {
            throw new ApiException("该活动没有组织者");
        }
        activity.setOrganizer(organizer);

        Criteria criteria = new Criteria();
        criteria.and("activityId").is(activity.getActivityId());
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        List<Appointment> appointmentList = appointmentDao.find(query);
        if (null != appointmentList && !appointmentList.isEmpty()) {
            activity.setAppointmentList(appointmentList);
        }


        return ResponseDo.buildSuccessResponse(activity);
    }

    @Override
    public ResponseDo deleteUserActivities(Collection ids) throws ApiException {
        if (null == ids || ids.isEmpty()) {
            throw new ApiException("未传入具体值");
        }
        List<Activity> byIds = activityDao.findByIds(ids);
        if (null == byIds || ids.isEmpty()) {
            throw new ApiException("id中含有不存在的值");
        }
        if (byIds.size() != ids.size()) {
            throw new ApiException("id中含有不存在的值");
        }
        activityDao.update(Query.query(Criteria.where("_id").in(ids)), Update.update("deleteFlag", true));
        return ResponseDo.buildSuccessResponse();
    }


    /**
     * 获取附近的匹配的活动列表
     *
     * @param param 请求参数
     * @return
     */
    @Override
    public ResponseDo getNearByActivityList(HttpServletRequest request, ActivityQueryParam param) {
        LOG.info("Query parameters:{}", param.toString());

        //获取所有的活动列表
        List<Activity> activityList = activityDao.find(Query.query(param.buildCommonQueryParam()));


        if (activityList.isEmpty()) {
            LOG.warn("No activity result found from database");
            return ResponseDo.buildSuccessResponse(activityList);
        }

        Map<String, User> userMap = buildUserMap(activityList);

        //获取出该用户所关注的 所有的 用户 id
        List<Activity> activities = rebuildActivities(param, activityList, userMap);

        //排序
        sortActivityList(userMap, activities);

        if (activities.size() < param.getIgnore()) {
            LOG.warn("No data exist after ignore:{}", param.getIgnore());
            return ResponseDo.buildSuccessResponse(new ArrayList<>(0));
        }

        List<Activity> remainActivities = activities.subList(param.getIgnore(), activities.size());
        if (remainActivities.size() > param.getLimit()) {
            remainActivities = remainActivities.subList(0, param.getLimit());
        }

        LOG.debug("Query user subscriber info");
        Map<String, Subscriber> subscriberMap = initSubscriberMap(param.getUserId());
        Set<String> subscriberSet = subscriberMap.keySet();

        //查询出Activity的 组织者，并初始化
        return ResponseDo.buildSuccessResponse(buildResponse(param.getUserId(), userMap, remainActivities, subscriberSet));
    }

    private void sortActivityList(Map<String, User> userMap, List<Activity> activities) {
        //按照权重进行排序
        Collections.sort(activities);

        for (Activity item : activities) {
            User user = userMap.get(item.getUserId());

            //权重修正，如果一个人匹配的活动较多的时候，需要进行权重减小，防止一个人刷屏, 第一次重复减0.1,第二次重复减0.2,第三次重复减0.4,0.8,1.6...
            if (user.getMatchTimes() > 0) {
                item.setSortFactor(item.getSortFactor() - 0.1 * Math.pow(2, user.getMatchTimes() - 1));
            }
            user.setMatchTimes(user.getMatchTimes() + 1);
        }

        Collections.sort(activities);
    }

    private void initAndSaveStatisticActivityReMatch(HttpServletRequest request, ActivityQueryParam param, String eventType) {
        StatisticActivityMatch statisticActivityMatch = new StatisticActivityMatch();

        statisticActivityMatch.setType(param.getType());
        statisticActivityMatch.setMajorType(param.getMajorType());
        statisticActivityMatch.setPay(param.getPay());
        Address address = new Address();
        address.setProvince(param.getProvince());
        address.setCity(param.getCity());
        address.setDistrict(param.getDistrict());
        statisticActivityMatch.setDestination(address);

        Landmark landmark = new Landmark();
        landmark.setLongitude(param.getLongitude());
        landmark.setLatitude(param.getLatitude());
        statisticActivityMatch.setDestPoint(landmark);

        statisticActivityMatch.setTransfer(param.getTransfer());
        statisticActivityMatch.setUserId(param.getUserId());
        statisticActivityMatch.setIp(IPFetchUtil.getIPAddress(request));

        statisticActivityMatch.setEvent(eventType);
        statisticActivityMatch.setCount(1);


        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        statisticActivityMatch.setCreateTime(currentTime.getTime());
        statisticActivityMatch.setYear(calendar.get(Calendar.YEAR));
        statisticActivityMatch.setMonth(calendar.get(Calendar.MONTH) + 1);
        statisticActivityMatch.setDay(calendar.get(Calendar.DAY_OF_MONTH) + 1);
        statisticActivityMatch.setHour(calendar.get(Calendar.HOUR));
        statisticActivityMatch.setMinute(calendar.get(Calendar.MINUTE));
        statisticActivityMatchDao.save(statisticActivityMatch);
    }

    private boolean isApplied(String userId, List<String> applyIds) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        }

        if (null == applyIds || applyIds.isEmpty()) {
            return false;
        }

        boolean applyFlag = false;
        for (String id : applyIds) {
            if (StringUtils.equals(id, userId)) {
                applyFlag = true;
            }
        }
        return applyFlag;
    }

    private List<Map<String, Object>> buildResponse(String userId, Map<String, User> userMap, List<Activity> remainActivities, Set<String> subscriberSet) {
        String localServer = CommonUtil.getLocalPhotoServer();
        List<Map<String, Object>> result = new ArrayList<>(remainActivities.size());
        for (Activity item : remainActivities) {
            Map<String, Object> map = new HashMap<>();
            map.put("activityId", item.getActivityId());
            map.put("transfer", item.isTransfer());
            map.put("distance", item.getDistance());
            map.put("applyFlag", isApplied(userId, item.getApplyIds()));
            User user = userMap.get(item.getUserId());
            Map<String, Object> organizer = new HashMap<>(9, 1);
            if (user != null) {
                organizer.put("userId", user.getUserId());
                if (null != user.getCar()) {
                    user.getCar().refreshPhotoInfo(CommonUtil.getGPJBrandLogoPrefix());
                }
                organizer.put("car", user.getCar());
                organizer.put("subscribeFlag", subscriberSet.contains(item.getUserId()));
                organizer.put("nickname", user.getNickname());
                organizer.put("gender", user.getGender());
                organizer.put("photoAuthStatus", user.getPhotoAuthStatus());
                organizer.put("licenseAuthStatus", user.getLicenseAuthStatus());
                organizer.put("avatar", localServer + user.getAvatar());
                organizer.put("cover", user.getCover());
                organizer.put("age", user.getAge());
            }
            map.put("organizer", organizer);
            map.put("pay", item.getPay());
            map.put("majorType", item.getMajorType());
            map.put("type", item.getType());
            map.put("destination", item.getDestination());
            result.add(map);
        }
        return result;
    }

    /**
     * 重新按照条件梳理一下活动信息
     *
     * @param param
     * @param activityList
     * @param userMap
     * @return
     */
    private List<Activity> rebuildActivities(ActivityQueryParam param, List<Activity> activityList, Map<String, User> userMap) {
        Map<String, Appointment> appointmentActivityMap = buildUserAppointmentActivityIds(param);

        LOG.debug("Filter user by idle status and compute weight");
        Long current = DateUtil.getTime();
        List<Activity> activities = new ArrayList<>(activityList.size());
        for (Activity item : activityList) {
            if (StringUtils.isNotEmpty(param.getUserId())) {
                //用户Id存在，过滤掉所有的已经邀约的活动
                //检查当前用户邀约的活动是否存在，如果已经拒绝了或者邀请了就不展示
                Appointment appointment = appointmentActivityMap.get(item.getActivityId());
                if (appointment != null && appointment.getCreateTime() > item.getCreateTime()) {
                    //说明当前的活动用户已经报名参加了，并且遭到拒绝或者接受
                    continue;
                }
            }

            User user = userMap.get(item.getUserId());
            if (param.isCommonQuery()) {
                if (StringUtils.isNotEmpty(param.getGender())) {
                    if (!StringUtils.equals(param.getGender(), user.getGender())) {
                        //如果用户的性别不符合要求；
                        continue;
                    }
                }
            }
            if (user != null && user.getIdle()) {
                //只有当用户空闲的情况下才参与排序计算
                item.setSortFactor(computeWeight(param, current, item, user));
                activities.add(item);
            }
        }
        return activities;
    }

    /**
     * 获取用户的邀约的信息
     *
     * @param param
     * @return
     */
    private Map<String, Appointment> buildUserAppointmentActivityIds(ActivityQueryParam param) {
        LOG.debug("Check user is already appointment or not, filter the activity");
        List<Appointment> appointmentList = new ArrayList<>(0);
        if (StringUtils.isNotEmpty(param.getUserId())) {
            appointmentList = appointmentDao.find(Query.query(Criteria.where("applyUserId").is(param.getUserId()).and("deleteFlag").is(false)
                    .and("status").in(Constants.AppointmentStatus.ACCEPT, Constants.AppointmentStatus.REJECT)));
        }

        Map<String, Appointment> activityAppointmentMap = new HashMap<>();
        for (Appointment item : appointmentList) {
            //计算用户的活动处于接收或者拒绝的状态
            Appointment appointment = activityAppointmentMap.get(item.getActivityId());
            if (appointment == null) {
                //存在邀约，添加到map中
                activityAppointmentMap.put(item.getActivityId(), item);
                continue;
            }

            if (item.getCreateTime() > appointment.getCreateTime()) {
                //当前的邀约更新，取最新的
                activityAppointmentMap.put(item.getActivityId(), item);
            }
        }
        return activityAppointmentMap;
    }

    /**
     * 权重计算
     *
     * @param param   权重因子参数
     * @param current 当前时间
     * @param item    活动
     * @param user    活动发布人员
     * @return 返回权重
     */
    private double computeWeight(ActivityQueryParam param, Long current, Activity item, User user) {
        //距离权重计算
//        item.setDistance(DistanceUtil.getDistance(param.getLongitude(), param.getLatitude(), item.getEstabPoint().getLongitude(), item.getEstabPoint().getLatitude()));
        item.setDistance(DistanceUtil.getDistance(new Landmark(param.getLongitude(), param.getLatitude()), item.getEstabPoint()));

        double sortFactor = 0.2 * (1 - item.getDistance() / param.getMaxDistance());

        sortFactor += 0.1 * (1 - (current - item.getCreateTime()) / param.getMaxTimeLimit());

        //用户车主认证权重计算
        if (Constants.AuthStatus.ACCEPT.equals(user.getLicenseAuthStatus())) {
            sortFactor += 0.15;
        }
        //用户头像认证权重计算
        if (Constants.AuthStatus.ACCEPT.equals(user.getPhotoAuthStatus())) {
            sortFactor += 0.25;
        }
        //TODO 添加身份认证权重计算

        //目的地权重计算
        if (item.getDestination() != null) {
            Address address = item.getDestination();
            if (address.getProvince() != null && address.getDistrict() != null) {
                sortFactor += 0.15;
            }
        }
        //活动时间权重计算，都包含时间
        sortFactor += 0.05;

        return sortFactor;
    }

    private Map<String, User> buildUserMap(List<Activity> activityList) {
        LOG.debug("Query users by activity list");
        Map<String, User> userMap = new HashMap<>(activityList.size(), 1);
        for (Activity item : activityList) {
            userMap.put(item.getUserId(), null);
        }

        List<User> users = userDao.findByIds(userMap.keySet());
        for (User item : users) {
            userMap.put(item.getUserId(), item);
        }

        return userMap;
    }

    private Map<String, Subscriber> initSubscriberMap(String userId) {
        List<Subscriber> subscribers = new ArrayList<>(0);
        if (!StringUtils.isEmpty(userId)) {
            subscribers = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        }

        Map<String, Subscriber> subscriberMap = new HashMap<>(subscribers.size(), 1);
        for (Subscriber item : subscribers) {
            subscriberMap.put(item.getToUser(), item);
        }
        return subscriberMap;
    }

    /**
     * 获取用户看看数据
     *
     * @param param 查询参数
     * @return
     */
    public ResponseDo getRandomActivities(ActivityQueryParam param) {
        LOG.info("Query random activities parameters:{}", param.toString());

        int counts = 0;
        List<Activity> activities = new ArrayList<>(0);
        do {
            //距离最多扩展4个数量级
            if (counts >= 4) {
                break;
            }
            if (counts > 0) {
                //距离程10倍的扩张
                param.setMaxDistance(param.getMaxDistance() * 10);
            }
            //获取所有的活动列表
            List<Activity> activityList = activityDao.find(Query.query(param.buildExpandQueryParam()));
            Map<String, User> userMap = buildUserMap(activityList);

            //获取出该用户所关注的 所有的 用户 id
            activities = rebuildActivities(param, activityList, userMap);
            counts++;
        } while (activities.size() < param.getLimit());

        if (activities.size() > param.getLimit()) {
            activities = activities.subList(0, param.getLimit());
        }

        Map<String, User> userMap = buildUserMap(activities);

        LOG.debug("Begin build response");
        return ResponseDo.buildSuccessResponse(buildResponse(param.getUserId(), userMap, activities, new HashSet<String>(0)));
    }

    @Override
    public ResponseDo getNearByActivityCount(ActivityQueryParam param) {
        LOG.info("Query parameters:{}", param.toString());
        Map<String, Object> data = new HashMap<>(1);
        //获取所有的活动列表
        List<Activity> activityList = activityDao.find(Query.query(param.buildCommonQueryParam()));
        if (activityList.isEmpty()) {
            //如果没有找到活动，进行拓展查询
            LOG.info("No result find, begin expand query");
            data.put("count", 0);
            return ResponseDo.buildSuccessResponse(data);
        }

        Map<String, User> userMap = buildUserMap(activityList);

        //获取出该用户所关注的 所有的 用户 id
        List<Activity> activities = rebuildActivities(param, activityList, userMap);

        data.put("count", activities.size());
        //查询出Activity的 组织者，并初始化
        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo getActivityPushInfos(HttpServletRequest request, String userId, Integer limit, Integer ignore) {
        LOG.debug("Query user pushInfo, userId:{}", userId);
        List<PushInfo> pushInfoList = pushInfoDao.find(Query.query(Criteria.where("receivedUserId").is(userId).and("deleteFlag").is(false))
                .limit(limit).skip(ignore));

        Set<String> activityIds = new HashSet<>(pushInfoList.size(), 1);
        for (PushInfo item : pushInfoList) {
            activityIds.add(item.getActivityId());
        }
        LOG.debug("Query user subscriber info");
        Map<String, Subscriber> subscriberMap = initSubscriberMap(userId);
        Set<String> subscriberSet = subscriberMap.keySet();

        List<Activity> activityList = activityDao.find(Query.query(Criteria.where("activityId").in(activityIds).and("deleteFlag").is(false))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"))));
        Map<String, User> userMap = buildUserMap(activityList);

        User user = userDao.findById(userId);
        for (Activity item : activityList) {
            User organizer = userMap.get(item.getUserId());
            item.setDistance(DistanceUtil.getDistance(user.getLandmark(), organizer.getLandmark()));
        }

        LOG.debug("Finished query data, begin build response");
        return ResponseDo.buildSuccessResponse(buildResponse(userId, userMap, activityList, subscriberSet));
    }

    @Override
    public ResponseDo registerUserActivity(String phone, String userId,Activity activity) throws ApiException {
        LOG.debug("activityRegister");

        User user = userDao.findUserByPhone(phone);
//        if (user == null) {
//            throw new ApiException("用户不存在");
//        }

        if (user == null) {
            user = userDao.findById(userId);
        }

        Long current = DateUtil.getTime();

        saveUserActivity(user.getUserId(), activity, current);

        saveInterestMessage(user.getUserId(), activity, current);

        //向关注我的人发送感兴趣的信息
        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.interest", "{0}想找人一起{1}"),
                user.getNickname(), activity.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.INTEREST,
                buildUserSubscribers(user.getUserId()), message, ext);

        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.NEARBY,
                buildNearByUsers(user, activity.getActivityId()), message, ext);

        return ResponseDo.buildSuccessResponse();
    }
}
