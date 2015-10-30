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
import com.gongpingjia.carplay.entity.common.Car;
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
//        try {
//            String groupId = createEmchatGroup(activity);
//            activityDao.update(Query.query(Criteria.where("_id").is(activity.getActivityId())), Update.update("emchatGroupId", groupId));
//        } catch (ApiException e) {
//            //创建环信群组失败的时候，需要删除mongodb中的对应的群组；
//            activityDao.deleteById(activity.getActivityId());
//            LOG.error(e.getMessage(), e);
//            throw new ApiException("创建环信群组失败");
//        }

        InterestMessage interestMessage = new InterestMessage();
        interestMessage.setUserId(userId);
        interestMessage.setType(InterestMessage.USER_ACTIVITY);
        interestMessage.setRelatedId(activity.getActivityId());
        interestMessage.setCreateTime(current);
        interestMessageDao.save(interestMessage);

        //向关注我的人发送感兴趣的信息
        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.interest", "{0}想找人一起{1}"),
                user.getNickname(), activity.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.INTEREST,
                buildUserSubscribers(userId), message, ext);

        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.NEARBY,
                buildNearByUsers(user, activity.getActivityId()), message, ext);

        return ResponseDo.buildSuccessResponse();
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
    public ResponseDo getActivityInfo(String userId, String activityId) throws ApiException {
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
        activity.setOrganizer(organizer);
        return ResponseDo.buildSuccessResponse(activity);
    }

    /**
     * 获取附近的活动；
     *
     * @param request
     * @param userId
     * @return
     * @throws ApiException longitude latitude 经纬度信息       必填项；
     *                      type 活动类型； 选填
     *                      pay 付费类型    选填
     *                      gender 性别 选填
     *                      transfer 是否包接送   选填        true 查询 包接送项      false 查询所有
     *                      <p/>
     *                      limit ignore 信息     选填； 默认 ignore ： 0， limit： 10；
     */
    @Override
    public ResponseDo getNearActivityList(HttpServletRequest request, String userId) throws ApiException {
        LOG.debug("getNearActivityList");
        String limitStr = request.getParameter("limit");
        String ignoreStr = request.getParameter("ignore");
        int limit = 10;
        int ignore = 0;
        if (StringUtils.isNotEmpty(ignoreStr)) {
            ignore = TypeConverUtil.convertToInteger("ignore", ignoreStr, true);
        }
        if (StringUtils.isNotEmpty(limitStr)) {
            limit = TypeConverUtil.convertToInteger("limit", limitStr, true);
        }

        AreaRangeInfo areaRangeInfo = new AreaRangeInfo();
        areaRangeInfo.maxDistance = Double.parseDouble(PropertiesUtil.getProperty("activity.defaultMaxDistance", String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));
        areaRangeInfo.maxPubTime = Long.parseLong(PropertiesUtil.getProperty("activity.defaultMaxPubTime", String.valueOf(ActivityWeight.MAX_PUB_TIME)));

        List<Activity> resultList = null;
        int searchDepth = 0;
        do {

            resultList = buildResultList(request, userId, areaRangeInfo);
            //不是取第一页的信息 跳出此判断
            if (ignore != 0) {
                break;
            }
            if (searchDepth > 4) {
                break;
            }
            if (resultList == null || resultList.size() < limit) {
                areaRangeInfo.maxDistance = areaRangeInfo.maxDistance * 10;
                areaRangeInfo.maxPubTime = areaRangeInfo.maxPubTime * 10;
                searchDepth++;
            } else {
                break;
            }

        } while (true);


        //初始化返回数据
        JSONArray rltArr = initResultMap(userId, resultList);


        return ResponseDo.buildSuccessResponse(rltArr);
    }

    public List<Activity> buildResultList(HttpServletRequest request, String userId, AreaRangeInfo areaRangeInfo) throws ApiException {
        SearchInfo searchInfo = initSearchInfo(request, userId, areaRangeInfo);

        //获得所有的满足基础条件的活动；
        List<Activity> allActivityList = activityDao.find(Query.query(searchInfo.criteria));
        if (allActivityList.isEmpty()) {
            LOG.warn("all Activity list is empty");
            return null;
        }
        LOG.debug("allActivityList size is:" + allActivityList.size());

        //获取出该用户所关注的 所有的 用户 id
        List<String> subscriberIds = initSubscriberIds(userId);
        //查询出Activity的 组织者，并初始化
        initOrganizer(allActivityList, subscriberIds);

        /**
         *  对所有的基础条件活动进行权重打分，并且排序
         * 此处是查询条件下的内存分页；现业务下没有更好的方式；需要全部排序 就需要将所有的数据读入到内存中进行计算；
         * 优化方式可以 实用缓存方式 ，对于用户重复的请求可以缓存起来，利用version 更改机制 探讨一下；
         */

        //需要排除 的 activityId 集合
        HashSet<String> removeActivityIdSet = initActivityRemoveSet(userId, searchInfo);

        List<Activity> resultList = ActivityUtil.getSortResult(allActivityList, new Date(), searchInfo.landmark, searchInfo.maxDistance, searchInfo.maxPubTime, searchInfo.ignore, searchInfo.limit, searchInfo.genderType, removeActivityIdSet);

        if (null == resultList || resultList.size() == 0) {
            return null;
        }
        LOG.debug("resultList size is:" + resultList.size());
        return resultList;
    }

    private HashSet<String> initActivityRemoveSet(String userId, SearchInfo searchInfo) {
        HashSet<String> removeActivityIdSet = null;
        //查出 最大发布时间内的 该用户已经申请过的 appointment  将 appointment中的      处于被拒绝 或者 已经接受的  activity 剔除掉；
        if (StringUtils.isNotEmpty(userId)) {
            Criteria appointCriteria = Criteria.where("applyUserId").is(userId);

            appointCriteria.and("createTime").gte(DateUtil.addTime(new Date(), Calendar.MINUTE, (0 - (int) searchInfo.maxPubTime)));
            //用户活动 非官方活动；
            appointCriteria.and("activityCategory").is(Constants.ActivityCatalog.COMMON);

            appointCriteria.and("status").ne(Constants.AppointmentStatus.APPLYING);

            List<Appointment> appointmentList = appointmentDao.find(Query.query(appointCriteria));
            if (null != appointmentList && !appointmentList.isEmpty()) {
                removeActivityIdSet = new HashSet<>(appointmentList.size());
                for (Appointment appointment : appointmentList) {
                    removeActivityIdSet.add(appointment.getActivityId());
                }
            }
        }
        return removeActivityIdSet;
    }

    private List<String> initSubscriberIds(String userId) {
        List<Subscriber> subscribers = null;
        if (StringUtils.isEmpty(userId)) {
            subscribers = new ArrayList<Subscriber>();
        } else {
            subscribers = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        }
        List<String> subscriberIds = new ArrayList<>(subscribers.size());

        for (Subscriber subscriber : subscribers) {
            subscriberIds.add(subscriber.getToUser());
        }
        return subscriberIds;
    }

    private JSONArray initResultMap(String userId, List<Activity> resultList) throws ApiException {
        JSONArray jsonArray = new JSONArray();
        if (null == resultList || resultList.isEmpty()) {
            return jsonArray;
        }

        //初始化 activity 的信息  并添加 activity 的 组织者 信息  以及 组织者 所对应的 car 的信息；
        for (Activity activity : resultList) {
            Map<String, Object> jsonItem = new HashMap<>();
            jsonItem.put("type", activity.getType());
            jsonItem.put("activityId", activity.getActivityId());
            jsonItem.put("distance", activity.getDistance());
            jsonItem.put("pay", activity.getPay());
            jsonItem.put("transfer", activity.isTransfer());
            jsonItem.put("destination", activity.getDestination());
            List<String> applyIds = activity.getApplyIds();
            if (StringUtils.isEmpty(userId)) {
                jsonItem.put("applyFlag", false);
            } else {
                if (null == applyIds || applyIds.isEmpty()) {
                    jsonItem.put("applyFlag", false);
                } else {
                    boolean applyFlag = false;
                    for (String id : applyIds) {
                        if (StringUtils.equals(id, userId)) {
                            applyFlag = true;
                        }
                    }
                    jsonItem.put("applyFlag", applyFlag);
                }
            }

            Map<String, Object> itemOrganizer = new HashMap<>();
            User organizer = activity.getOrganizer();
            if (null == organizer) {
                throw new ApiException("该活动没有组织者");
            }
            itemOrganizer.put("userId", organizer.getUserId());
            itemOrganizer.put("nickname", organizer.getNickname());

            // 对 avatar 信息添加了 服务器主机信息
            itemOrganizer.put("avatar", CommonUtil.getLocalPhotoServer() + organizer.getAvatar());
            itemOrganizer.put("gender", organizer.getGender());
            itemOrganizer.put("age", organizer.getAge());

            //初始化用户相册信息
            initUserAlbumInfo(itemOrganizer, organizer);

            //初始化 活动的组织者的 car 信息 Car 的 brand 品牌 和  logo 是一个 url
            initUserCarInfo(activity, itemOrganizer);

            itemOrganizer.put("photoAuthStatus", activity.getOrganizer().getPhotoAuthStatus());
            itemOrganizer.put("subscribeFlag", activity.getOrganizer().getSubscribeFlag());
            itemOrganizer.put("cover", activity.getOrganizer().getCover());//活动封面展示的URL图像
            jsonItem.put("organizer", itemOrganizer);
            jsonArray.add(jsonItem);
        }

//        rltMap.put("activityList", jsonArray);
        return jsonArray;
    }

    private void initUserCarInfo(Activity activity, Map<String, Object> itemOrganizer) {
        JSONObject carJson = new JSONObject();
        Car car = activity.getOrganizer().getCar();
        if (null != car) {
            if (StringUtils.isNotEmpty(car.getBrand())) {
                carJson.put("brand", car.getBrand());
            }
            //car 的 logo 需要添加 公平价的 服务器地址前缀
            if (StringUtils.isNotEmpty(car.getLogo())) {
                carJson.put("logo", CommonUtil.getGPJBrandLogoPrefix() + car.getLogo());
            }
        }
        itemOrganizer.put("car", carJson);
    }

    private void initUserAlbumInfo(Map<String, Object> itemOrganizer, User organizer) {
        if (null == organizer.getAlbum() || organizer.getAlbum().size() == 0) {
            itemOrganizer.put("album", "[]");
        } else {
            //获取时间最近的一张;
            long maxTime = 0L;
            Photo tempPhoto = null;
            Iterator<Photo> iterator = organizer.getAlbum().iterator();
            while (iterator.hasNext()) {
                Photo temp = iterator.next();
                if (maxTime < temp.getUploadTime()) {
                    maxTime = temp.getUploadTime();
                    tempPhoto = temp;
                }
            }
            tempPhoto.setUrl(CommonUtil.getThirdPhotoServer() + tempPhoto.getKey());
            List<Photo> onePhoneAlbum = new ArrayList<>(1);
            onePhoneAlbum.add(tempPhoto);
            itemOrganizer.put("album", onePhoneAlbum);
        }
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

        Long current = DateUtil.getTime();
        appointment.setActivityId(activity.getActivityId());
        appointment.setApplyUserId(userId);
        appointment.setInvitedUserId(activity.getUserId());
        appointment.setCreateTime(current);
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setModifyTime(current);
        appointment.setActivityCategory(Constants.ActivityCatalog.COMMON);
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
     * 根据活动信息创建聊天群
     *
     * @param activity 活动信息
     * @throws ApiException 创建群聊失败时
     */
    private String createEmchatGroup(Activity activity) throws ApiException {
        LOG.debug("Begin create chat group");
        User owner = userDao.findById(activity.getUserId());
        JSONObject json = chatThirdPartyService.createChatGroup(chatCommonService.getChatToken(),
                owner.getNickname() + activity.getType(), activity.getActivityId(), owner.getEmchatName(), null);
        if (json.isEmpty()) {
            LOG.warn("Failed to create chat group");
            throw new ApiException("创建聊天群组失败");
        }
        String groupId = json.getJSONObject("data").getString("groupid");
        return groupId;

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
        appointmentDao.update(appointmentId, Update.update("status", status));

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

        String pay = json.getString("pay");
        if (StringUtils.isNotEmpty(pay) && !StringUtils.equals(pay, "-1")) {
            criteria.and("pay").is(pay);
        }

        String type = json.getString("type");
        if (StringUtils.isNotEmpty(type) && !StringUtils.equals(type, "-1")) {
            criteria.and("majorType").is(type);
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
            Address destination = (Address) JSONObject.toBean(json.getJSONObject("destination"), Address.class);
            update.set("destination", destination);
            Address establish = (Address) JSONObject.toBean(json.getJSONObject("establish"), Address.class);
            update.set("establish", establish);
            Landmark destPoint = (Landmark) JSONObject.toBean(json.getJSONObject("destPoint"), Landmark.class);
            update.set("destPoint", destPoint);
            Landmark estabPoint = (Landmark) JSONObject.toBean(json.getJSONObject("estabPoint"), Landmark.class);
            update.set("estabPoint", estabPoint);
            String type = json.getString("type");
            update.set("type", type);
            String pay = json.getString("pay");
            update.set("pay", pay);
            boolean transfer = json.getBoolean("transfer");
            update.set("transfer", transfer);
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

    private SearchInfo initSearchInfo(HttpServletRequest request, String userId, AreaRangeInfo areaRangeInfo) throws ApiException {
        SearchInfo searchInfo = new SearchInfo();
        //必填项
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            LOG.error("longitude or latitude has not init");
            throw new ApiException("经纬度信息没有提供");
        }
        Landmark landmark = new Landmark();
        landmark.setLatitude(TypeConverUtil.convertToDouble("latitude", latitude, true));
        landmark.setLongitude(TypeConverUtil.convertToDouble("longitude", longitude, true));
        searchInfo.landmark = landmark;


//        //选填项目
//        String maxDistanceStr = request.getParameter("maxDistance");
//        LOG.debug("maxDistanceStr is:{}", maxDistanceStr);
        double maxDistance = areaRangeInfo.maxDistance;
//        if (StringUtils.isNotEmpty(maxDistanceStr)) {
//            maxDistance = TypeConverUtil.convertToDouble("maxDistance", maxDistanceStr, true);
//        }
        searchInfo.maxDistance = maxDistance;

        String type = request.getParameter("type");
        String pay = request.getParameter("pay");
        String genderTypeStr = request.getParameter("gender");
        String transferStr = request.getParameter("transfer");

        //分页信息  可以不填
        String limitStr = request.getParameter("limit");
        String ignoreStr = request.getParameter("ignore");
        int limit = 10;
        //ignore是跳过的参数
        int ignore = 0;
        //默认的最大距离参数 如果没有传递最大距离 则实用默认的最大距离
        if (StringUtils.isNotEmpty(limitStr)) {
            limit = TypeConverUtil.convertToInteger("limit", limitStr, true);
        }
        if (StringUtils.isNotEmpty(ignoreStr)) {
            ignore = TypeConverUtil.convertToInteger("ignore", ignoreStr, true);
        }
        searchInfo.limit = limit;
        searchInfo.ignore = ignore;


        LOG.debug("init Query");
        Criteria criteria = new Criteria();

        //查询创建在此时间之前的活动；
        long maxPubTime = areaRangeInfo.maxPubTime;
        searchInfo.maxPubTime = maxPubTime;
        long gtTime = DateUtil.addTime(new Date(), Calendar.MINUTE, (0 - (int) maxPubTime));
        criteria.and("createTime").gte(gtTime);

        //查询在最大距离内的 活动；      此处的maxDistance 需要换算成 对应的 弧度
        criteria.and("estabPoint").near(new Point(landmark.getLongitude(), landmark.getLatitude())).maxDistance(maxDistance * 180 / DistanceUtil.EARTH_RADIUS);

        //非用户自己创建的活动
        if (StringUtils.isNotEmpty(userId)) {
            criteria.and("userId").ne(userId);
        }

        //deleteFlag 为 false 的活动；
        criteria.and("deleteFlag").is(false);

//        //剔除掉用户已经申请过该活动
//        if (StringUtils.isNotEmpty(userId)) {
//            criteria.and("applyIds").nin(userId);
//        }

        if (StringUtils.isNotEmpty(type)) {
            criteria.and("type").is(type);
        }
        if (StringUtils.isNotEmpty(pay)) {
            criteria.and("pay").is(pay);
        }
        if (StringUtils.isNotEmpty(transferStr)) {
            boolean transfer = TypeConverUtil.convertToBoolean("transfer", transferStr, true);
            //如果包接送
            if (transfer) {
                criteria.and("transfer").is(true);
            }
        }
        searchInfo.criteria = criteria;

        int genderType = -1;

        if (StringUtils.isEmpty(genderTypeStr)) {
            genderType = -1;
        } else if (StringUtils.equals(genderTypeStr, Constants.UserGender.MALE)) {
            genderType = 0;
        } else if (StringUtils.equals(genderTypeStr, Constants.UserGender.FEMALE)) {
            genderType = 1;
        } else {
            throw new ApiException("用户性别设置不正确");
        }
        searchInfo.genderType = genderType;

        return searchInfo;
    }

    class SearchInfo {
        Criteria criteria;
        //landmark, maxDistance, maxPubTime, ignore, limit, genderType
        public Landmark landmark;
        public double maxDistance;
        public long maxPubTime;
        public int ignore;
        public int limit;
        public int genderType;
    }

    class AreaRangeInfo {
        public long maxPubTime;
        public double maxDistance;
    }


    /**
     * 获取附近的匹配的活动列表
     *
     * @param param 请求参数
     * @return
     */
    @Override
    public ResponseDo getNearByActivityList(HttpServletRequest request,ActivityQueryParam param) {
        LOG.info("Query parameters:{}", param.toString());
        //获取所有的活动列表
        List<Activity> activityList = activityDao.find(Query.query(param.buildCommonQueryParam()));
        if (activityList.isEmpty()) {
            LOG.info("No result find, begin expand query");


            //TODO
            //埋点统计        没有找到对应的活动 进行了扩展查询；
            StatisticActivityMatch statisticActivityMatch = new StatisticActivityMatch();

//            statisticActivityMatchDao.save();


            //如果没有找到活动，进行拓展查询
            activityList = activityDao.find(Query.query(param.buildExpandQueryParam()));
        }

        if (activityList.isEmpty()) {
            LOG.warn("No activity result found fron database");
            return ResponseDo.buildSuccessResponse(activityList);
        }

        Map<String, User> userMap = buildUserMap(activityList);

        //获取出该用户所关注的 所有的 用户 id
        List<Activity> activities = rebuildActivities(param, activityList, userMap);

        //按照权重进行排序
        Collections.sort(activities);
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
        LOG.debug("Filter user by idle status and compute weight");
        Long current = DateUtil.getTime();
        List<Activity> activities = new ArrayList<>(activityList.size());
        for (Activity item : activityList) {
            User user = userMap.get(item.getUserId());
            if (param.isCommonQuery()) {
                if (StringUtils.isNotEmpty(param.getGender())) {
                    if (!StringUtils.equals(param.getGender(), user.getGender())) {
                        //如果用户的性别不符合要求；
                        continue;
                    }
                }
            }
            if (user != null || user.getIdle()) {
                //只有当用户空闲的情况下才参与排序计算
                item.setSortFactor(computeWeight(param, current, item, user));
                activities.add(item);
            }
        }
        return activities;
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
        item.setDistance(DistanceUtil.getDistance(param.getLongitude(), param.getLatitude(),
                item.getEstabPoint().getLongitude(), item.getEstabPoint().getLatitude()));
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

        //权重修正，如果一个人匹配的活动较多的时候，需要进行权重减小，防止一个人刷屏, 第一次重复减0.1,第二次重复减0.2,第三次重复减0.4,0.8,1.6...
        if (user.getMatchTimes() > 0) {
            sortFactor -= 0.1 * Math.pow(2, user.getMatchTimes() - 1);
        }
        user.setMatchTimes(user.getMatchTimes() + 1);
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
    public ResponseDo getActivityPushInfos(String userId) {
        LOG.debug("Query user pushInfo, userId:{}", userId);
        List<PushInfo> pushInfoList = pushInfoDao.find(Query.query(Criteria.where("receivedUserId").is(userId).and("deleteFlag").is(false)));

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
            item.setDistance(DistanceUtil.getDistance(user.getLandmark().getLongitude(), user.getLandmark().getLatitude(),
                    organizer.getLandmark().getLongitude(), organizer.getLandmark().getLatitude()));
        }

        LOG.debug("Finished query data, begin build response");
        return ResponseDo.buildSuccessResponse(buildResponse(userId, userMap, activityList, subscriberSet));
    }
}
