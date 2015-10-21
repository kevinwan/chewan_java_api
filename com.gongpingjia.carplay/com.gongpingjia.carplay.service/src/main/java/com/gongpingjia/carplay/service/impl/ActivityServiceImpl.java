package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.util.ActivityUtil;
import com.gongpingjia.carplay.service.util.ActivityWeight;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import com.gongpingjia.carplay.service.util.FetchUtil;
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

        activity.setActivityId(null);
        //设置活动的创建人ID
        activity.setUserId(userId);
        List<String> memberIds = new ArrayList<String>(1);
        //创建人默认加入到活动成员列表中
        memberIds.add(userId);
        activity.setMembers(memberIds);
        activity.setCreateTime(new Date().getTime());
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

        //向关注我的人发送感兴趣的信息
        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.interest", "{0}想找人一起{1}"),
                user.getNickname(), activity.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.INTEREST,
                buildUserSubscribers(userId), message, ext);

        return ResponseDo.buildSuccessResponse();
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



        SearchInfo searchInfo = initSearchInfo(request, userId);


        //获得所有的满足基础条件的活动；
        List<Activity> allActivityList = activityDao.find(Query.query(searchInfo.criteria));
        if (allActivityList.isEmpty()) {
            LOG.warn("all Activity list is empty");
            return ResponseDo.buildSuccessResponse("[]");
        }

        LOG.debug("allActivityList size is:" + allActivityList.size());


        //初始化关注者id
        List<String> subscriberIds = initSubscriberIds(userId);

        //查询出Activity的 组织者，并初始化
        initOrganizer(allActivityList, subscriberIds);

        /**
         *  对所有的基础条件活动进行权重打分，并且排序
         * 此处是查询条件下的内存分页；现业务下没有更好的方式；需要全部排序 就需要将所有的数据读入到内存中进行计算；
         * 优化方式可以 实用缓存方式 ，对于用户重复的请求可以缓存起来，利用version 更改机制 探讨一下；
         */


        HashSet<String> removeActivityIdSet = initActivityRemoveSet(userId, searchInfo);

        List<Activity> resultList = ActivityUtil.getSortResult(allActivityList, new Date(), searchInfo.landmark, searchInfo.maxDistance, searchInfo.maxPubTime, searchInfo.ignore, searchInfo.limit, searchInfo.genderType, removeActivityIdSet);

        if (null == resultList || resultList.size() == 0) {
            return ResponseDo.buildSuccessResponse("[]");
        }

        LOG.debug("resultList size is:" + resultList.size());

        //初始化返回数据
        JSONArray rltArr = initResultMap(userId, resultList);


        return ResponseDo.buildSuccessResponse(rltArr);
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

        //初始化 activity 的信息  并添加 activity 的 组织者 信息  以及 组织者 所对应的 car 的信息；
        JSONArray jsonArray = new JSONArray();
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

        //接收或者拒绝了别人的请求，需要发送环信消息
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.activity.state", "{0}{1}了您的{2}邀请"),
                user.getNickname(), status, appointment.getType());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.ACTIVITY_STATE,
                applyUser.getEmchatName(), message);

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
            throw new ApiException("活动邀请处理中");
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

        for (Activity activity : activityList) {
            User organizer = FetchUtil.getUserFromList(users, activity.getUserId());
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
        Criteria criteria = new Criteria();

        //
        String city = json.getString("city");
        if (StringUtils.isNotEmpty(city)) {
            criteria.and("estabPoint.city").is(city);
        }

        String phone = json.getString("phone");
        if (StringUtils.isNotEmpty(phone)) {
            User user = userDao.findOne(Query.query(Criteria.where("phone").is(phone)));
            if (null == user || StringUtils.isEmpty(user.getUserId())) {
                throw new ApiException("未找到该号码发布的活动");
            }
            criteria.and("userId").is(user.getUserId());
        }

//        String startTimeStr = request.getParameter("startTime");
//        String endTimeStr = request.getParameter("endTime");
//        if (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) {
//            long start = TypeConverUtil.convertToLong("startDate", startTimeStr, true);
//            long end = TypeConverUtil.convertToLong("endDate", endTimeStr, true) + 24 * 60 * 60 * 1000;
//            criteria.and("createTime").gte(start).lte(end);
//        }

        String pay = json.getString("pay");
        if (StringUtils.isNotEmpty(pay) && !StringUtils.equals(pay, "-1")) {
            criteria.and("pay").is(pay);
        }

        String type = json.getString("type");
        if (StringUtils.isNotEmpty(type) && !StringUtils.equals(type, "-1")) {
            criteria.and("type").is(type);
        }

        String transferStr = json.getString("transfer");
        if (StringUtils.isNotEmpty(transferStr) && !StringUtils.equals(transferStr, "-1")) {
            criteria.and("transfer").is(TypeConverUtil.convertToBoolean("transfer", transferStr, true));
        }

        List<Activity> activityList = activityDao.find(Query.query(criteria));
        if (null == activityList || activityList.isEmpty()) {
            return ResponseDo.buildSuccessResponse("[]");
        }

        HashSet<String> userIdSet = new HashSet<>(activityList.size());
        for (Activity activity : activityList) {
            userIdSet.add(activity.getUserId());
        }
        List<User> userList = userDao.findByIds(userIdSet);
        for (Activity activity : activityList) {
            activity.setOrganizer(FetchUtil.getUserFromList(userList, activity.getUserId()));
        }

        return ResponseDo.buildSuccessResponse(activityList);
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
        activityDao.deleteByIds(ids);
        return ResponseDo.buildSuccessResponse();
    }


    private SearchInfo initSearchInfo(HttpServletRequest request, String userId) throws ApiException {
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


        //选填项目
        String maxDistanceStr = request.getParameter("maxDistance");
        LOG.debug("maxDistanceStr is:{}", maxDistanceStr);
        double maxDistance = Double.parseDouble(PropertiesUtil.getProperty("activity.default_max_distance", String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));
        if (StringUtils.isNotEmpty(maxDistanceStr)) {
            maxDistance = TypeConverUtil.convertToDouble("maxDistance", maxDistanceStr, true);
        }
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
        long maxPubTime = Long.parseLong(PropertiesUtil.getProperty("activity.default_max_pub_time", String.valueOf(ActivityWeight.MAX_PUB_TIME)));
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
}
