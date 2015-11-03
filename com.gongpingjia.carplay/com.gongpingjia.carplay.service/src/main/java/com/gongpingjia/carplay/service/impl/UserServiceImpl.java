package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import com.gongpingjia.carplay.dao.history.AlbumViewHistoryDao;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.history.InterestMessageDao;
import com.gongpingjia.carplay.dao.user.*;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.activity.PushInfo;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.history.AlbumViewHistory;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.history.InterestMessage;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.user.UserAuthentication;
import com.gongpingjia.carplay.entity.user.UserToken;
import com.gongpingjia.carplay.service.UserService;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService photoService;

    @Autowired
    @Qualifier("localFileManager")
    private PhotoService localFileManager;

    @Autowired
    private ParameterChecker checker;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private ChatThirdPartyService chatThirdService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private AlbumViewHistoryDao albumViewHistoryDao;

    @Autowired
    private AuthenticationHistoryDao authenticationHistoryDao;

    @Autowired
    private AuthApplicationDao authApplicationDao;

    @Autowired
    private AlbumViewHistoryDao historyDao;

    @Autowired
    private SubscriberDao subscriberDao;

    @Autowired
    private InterestMessageDao interestMessageDao;

    @Autowired
    private OfficialActivityDao officialActivityDao;

    @Autowired
    private PushInfoDao pushInfoDao;
    @Autowired
    private UserAuthenticationDao userAuthenticationDao;

    @Override
    public ResponseDo register(User user) throws ApiException {
        LOG.debug("Save register data begin");

        // 注册用户
        user.setRegisterTime(DateUtil.getTime());
        user.setRole(Constants.UserCatalog.COMMON);
        user.setPhotoAuthStatus(Constants.AuthStatus.UNAUTHORIZED);
        user.setLicenseAuthStatus(Constants.AuthStatus.UNAUTHORIZED);
        userDao.save(user);

        // 注册环信用户
        LOG.debug("Register emchat user by call remote service");
        Map<String, String> chatUser = new HashMap<>(2, 1);
        chatUser.put("username", chatCommonService.getUsernameByUserid(user.getUserId()));
        chatUser.put("password", user.getPassword());

        JSONObject result = chatThirdService.registerChatUser(chatCommonService.getChatToken(), chatUser);
        if (result.isEmpty()) {
            //创建环信用户失败，需要回滚数据库
            userDao.deleteById(user.getUserId());
            LOG.warn("Create emchat user failure");
            throw new ApiException("未能成功创建环信用户");
        }

        String emchatName = chatCommonService.getUsernameByUserid(user.getUserId());
        userDao.update(Query.query(Criteria.where("userId").is(user.getUserId())), Update.update("emchatName", emchatName));

        UserToken userToken = new UserToken();
        userToken.setUserId(user.getUserId());
        userToken.setToken(CodeGenerator.generatorId());
        userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
        userTokenDao.save(userToken);

        cacheManager.setUserToken(userToken);

        pushNearbyActivity(user.getUserId(), emchatName, user.getLandmark());

        User data = userDao.findById(user.getUserId());
        data.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        data.setToken(userToken.getToken());
        data.setCompletion(computeCompletion(data));

        return ResponseDo.buildSuccessResponse(data);
    }

    public void pushNearbyActivity(String receivedUserId, String emchatName, Landmark landmark) throws ApiException {
        LOG.debug("push user nearby activity");

        Activity activity = activityDao.findOne(Query.query(Criteria.where("estabPoint").near(new Point(landmark.getLongitude(), landmark.getLatitude()))));
        if (activity == null) {
            //在系统中如果没有活动就直接退出，主要是第一个用手机注册活动的人员可能会遇到
            return;
        }

        PushInfo info = new PushInfo();
        info.setSendUserId(activity.getUserId());
        info.setReceivedUserId(receivedUserId);
        info.setCreateTime(DateUtil.getTime());
        info.setActivityId(activity.getActivityId());
        pushInfoDao.save(info);

        User organizer = userDao.findById(activity.getUserId());
        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", CommonUtil.getLocalPhotoServer() + organizer.getAvatar());

        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.interest", "{0}想找人一起{1}"), organizer.getNickname(), activity.getType());

        JSONObject result = chatThirdService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.NEARBY,
                Arrays.asList(emchatName), message, ext);
        LOG.info("Push result:{}", result);
    }

    @Override
    public ResponseDo loginUser(User user) throws ApiException {
        // 验证参数
        if (!CommonUtil.isPhoneNumber(user.getPhone())) {
            LOG.warn("Invalid params, phone:{}", user.getPhone());
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        // 查找用户
        User userData = userDao.findOne(Query.query(Criteria.where("phone").is(user.getPhone())));
        if (userData == null) {
            LOG.warn("Fail to find user");
            return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
        }

        if (!user.getPassword().equals(userData.getPassword())) {
            LOG.warn("User password is incorrect");
            return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
        }

        //刷新用户Token
        userData.setToken(refreshUserToken(userData.getUserId()));

        userData.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        userData.setCompletion(computeCompletion(userData));

        return ResponseDo.buildSuccessResponse(userData);
    }


    /**
     * 后台管理员用户登录
     *
     * @param user
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo loginAdminUser(User user) throws ApiException {
        // 验证参数
        if (!CommonUtil.isPhoneNumber(user.getPhone())) {
            LOG.warn("Invalid params, phone:{}", user.getPhone());
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        // 查找用户
        User userData = userDao.findOne(Query.query(Criteria.where("phone").is(user.getPhone())));
        if (userData == null) {
            LOG.warn("Fail to find user");
            return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
        }

        if (!user.getPassword().equals(userData.getPassword())) {
            LOG.warn("User password is incorrect");
            return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
        }

        if (!userData.getRole().equals(Constants.UserCatalog.ADMIN) && !userData.getRole().equals(Constants.UserCatalog.OFFICIAL)) {
            LOG.warn("User is not  ADMIN OR OFFICIAL");
            return ResponseDo.buildFailureResponse("无权限");
        }

        //刷新用户Token
        userData.setToken(refreshUserToken(userData.getUserId()));

        userData.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        // 查询用户车辆信息
        if (userData.getCar() != null) {
            userData.getCar().refreshPhotoInfo(CommonUtil.getGPJBrandLogoPrefix());
        }

        userData.setCompletion(computeCompletion(userData));

        return ResponseDo.buildSuccessResponse(userData);
    }

    /**
     * 计算用户的信息的完善程度
     *
     * @param user
     * @return
     */
    private Integer computeCompletion(User user) {
        int completion = 0;
        if (Constants.AuthStatus.ACCEPT.equals(user.getLicenseAuthStatus())) {
            completion += 20;
        }
        if (Constants.AuthStatus.ACCEPT.equals(user.getPhotoAuthStatus())) {
            completion += 20;
        }

        final int total = 6;  //总共需要填写6项
        int has = 0;
        if (!StringUtils.isEmpty(user.getNickname())) {
            has++;
        }
        if (user.getBirthday() != null) {
            has++;
        }
        if (!StringUtils.isEmpty(user.getGender())) {
            has++;
        }
        if (!StringUtils.isEmpty(user.getAvatar())) {
            has++;
        }
        if (!StringUtils.isEmpty(user.getPhone())) {
            has++;
        }
        if (user.getAlbum() != null && user.getAlbum().size() > 0) {
            has++;
        }

        if (has == total) {
            completion += 60;
        } else {
            completion += (has * 60) / total;
        }
        return completion;
    }

    @Override
    public void checkRegisterParameters(User user, JSONObject json) throws ApiException {
        LOG.debug("Begin check input parameters of register");

        Landmark landmark = user.getLandmark();
        if (landmark == null || !user.getLandmark().correct()) {
            LOG.warn("Input parameter landmark error, out of range, landmark:{}", landmark);
            throw new ApiException("输入参数错误");
        }

        if (!StringUtils.isEmpty(user.getAvatar())) {
            user.setAvatar(MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, user.getAvatar()));
            // 判断图片是否存在
            if (!localFileManager.isExist(user.getAvatar())) {
                LOG.warn("avatar not exist");
                throw new ApiException("头像未上传");
            }
        }

        boolean phoneRegister = isPhoneRegister(json);
        boolean snsRegister = isSnsRegister(json);

        if (!phoneRegister && !snsRegister) {
            /* 既不是Phone注册，也不是第三方SNS注册，需要报输入参数有误 */
            LOG.warn("Invalid params, it is neither phone register, nor sns register");
            throw new ApiException("输入参数有误");
        }

        checkPhoneRegister(phoneRegister, json);

        refreshUserBySnsRegister(user, snsRegister, json);
    }


    @Override
    public ResponseDo forgetPassword(User user, String code) throws ApiException {
        LOG.debug("Begin reset password by forget password");

        // 验证参数
        if (!CommonUtil.isPhoneNumber(user.getPhone())) {
            LOG.warn("invalid params");
            throw new ApiException("输入参数有误");
        }

        // 验证验证码
        checker.checkPhoneVerifyCode(user.getPhone(), code);

        // 查询用户注册信息
        User userData = userDao.findOne(Query.query(Criteria.where("phone").is(user.getPhone())));
        if (userData == null) {
            LOG.warn("Fail to find user");
            throw new ApiException("用户不存在");
        }

        // 更新环信用户的密码
        chatThirdService.alterUserPassword(chatCommonService.getChatToken(), userData.getEmchatName(), user.getPassword());

        LOG.debug("Begin reset emchat account password by forget password");

        userDao.update(Query.query(Criteria.where("userId").is(userData.getUserId())), Update.update("password", user.getPassword()));

        Map<String, Object> data = new HashMap<String, Object>(2, 1);
        // 获取用户授权信息
        data.put("userId", userData.getUserId());
        data.put("token", refreshUserToken(userData.getUserId()));

        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo snsLogin(User user) throws ApiException {
        checkSnsLoginParameters(user.getUid(), user.getChannel(), user.getPassword());

        LOG.debug("Save data begin");
        User userData = userDao.findOne(Query.query(Criteria.where("uid").is(user.getUid())));

        if (userData == null) {
            // 没有找到对应的已经存在的用户，注册新用户, 由客户端调用注册接口，这里只完成图片上传
            LOG.debug("No exist user in the system, register new user by client call register interface");

            // 头像ID
            String avatarId = CodeGenerator.generatorId();
            String key = MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, avatarId);

            uploadPhotoToServer(user.getAvatar(), key);
            user.setAvatar(avatarId);
            Map<String, Object> data = new HashMap<>(4, 1);
            data.put("uid", user.getUid());
            data.put("nickname", user.getNickname());
            data.put("channel", user.getChannel());
            data.put("avatar", avatarId);
            return ResponseDo.buildSuccessResponse(data);
        } else {
            if (userData.isDeleteFlag()) {
                LOG.warn("User is already deleted by administrator, cannot login again");
                throw new ApiException("用户不存在");
            }

            // 用户已经存在于系统中
            LOG.debug("User is exist in the system, return login information");
            //刷新用户会话Token
            userData.setToken(refreshUserToken(userData.getUserId()));
            userData.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
            userData.setCompletion(computeCompletion(userData));

            return ResponseDo.buildSuccessResponse(userData);
        }
    }

    @Override
    public ResponseDo getUserInfo(String beViewedUser, String viewUser, String token) throws ApiException {
        LOG.debug("Begin get user information, check input parameters");
        checker.checkUserInfo(viewUser, token);

        User beViewedUserInfo = userDao.findById(beViewedUser);
        if (beViewedUserInfo == null) {
            LOG.warn("No user exist by userId:{}", beViewedUser);
            return ResponseDo.buildFailureResponse("用户不存在");
        }

        if (beViewedUserInfo.getAlbum() != null) {
            Collections.sort(beViewedUserInfo.getAlbum());
        }
        if (!viewUser.equals(beViewedUser)) {
            //表示不是自己查看自己，beViewedUser被别人看过,记录相册查看的历史信息
            AlbumViewHistory history = new AlbumViewHistory();
            history.setViewTime(DateUtil.getTime());
            history.setViewUserId(viewUser);
            history.setUserId(beViewedUser);
            historyDao.save(history);

            User viewUserInfo = userDao.findById(viewUser);
            String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.view", "{0}看过了我的相册"),
                    viewUserInfo.getNickname());
            chatThirdService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.USER_VIEW,
                    beViewedUserInfo.getEmchatName(), message);

            Subscriber subscriber = subscriberDao.findOne(Query.query(Criteria.where("fromUser").is(viewUser).and("toUser").is(beViewedUser)));
            beViewedUserInfo.setSubscribeFlag(subscriber != null ? true : false);
        } else {
            UserAuthentication userAuthentication = userAuthenticationDao.findById(beViewedUser);
            if (userAuthentication != null) {
                String localServer = CommonUtil.getLocalPhotoServer();
                beViewedUserInfo.setDriverLicense(localServer + userAuthentication.getDriverLicense());
                beViewedUserInfo.setDrivingLicense(localServer + userAuthentication.getDrivingLicense());
            }
        }

        beViewedUserInfo.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        beViewedUserInfo.setCompletion(computeCompletion(beViewedUserInfo));
        beViewedUserInfo.hideSecretInfo();

        return ResponseDo.buildSuccessResponse(beViewedUserInfo);
    }

    @Override
    public ResponseDo getMyActivityAppointments(String userId, String token, Integer[] status, Integer limit, Integer ignore) throws ApiException {

        LOG.info("getMyActivityAppointments userId {}", userId);

        checker.checkUserInfo(userId, token);
        //我的活动，需要展示所有的历史活动信息
        Criteria criteria = new Criteria().orOperator(Criteria.where("invitedUserId").is(userId), Criteria.where("applyUserId").is(userId));
        if (status != null && status.length > 0) {
            criteria.andOperator(Criteria.where("status").in(Arrays.asList(status)));
        }
        List<Appointment> appointments = appointmentDao.find(Query.query(criteria)
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "modifyTime"))).skip(ignore).limit(limit));

        return ResponseDo.buildSuccessResponse(buildUserActivities(userId, appointments));
    }

    @Override
    public ResponseDo getDynamicActivityAppointments(String userId, String token, Integer[] status, Integer limit, Integer ignore) throws ApiException {
        LOG.info("getDynamicActivityAppointments userId:{}", userId);

        checker.checkUserInfo(userId, token);
        //活动动态只展示没有失效的活动信息
        Criteria criteria = Criteria.where("deleteFlag").is(false)
                .orOperator(Criteria.where("invitedUserId").is(userId), Criteria.where("applyUserId").is(userId));
        if (status != null && status.length > 0) {
            criteria.andOperator(Criteria.where("status").in(Arrays.asList(status)));
        }
        List<Appointment> appointments = appointmentDao.find(Query.query(criteria)
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "modifyTime"))).skip(ignore).limit(limit));

        return ResponseDo.buildSuccessResponse(buildUserActivities(userId, appointments));
    }

    private List<Object> buildUserActivities(String userId, List<Appointment> appointments) {
        //获取所有的appointment 对应的 user 信息；
        Map<String, User> users = buildUsers(userId, appointments);
        Map<String, OfficialActivity> officialActivityMap = buildOfficialActivityMap(appointments);

        List<Subscriber> subscribers = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        Set<String> subscriberIdSet = new HashSet<>(subscribers.size(), 1);
        for (Subscriber subscriber : subscribers) {
            subscriberIdSet.add(subscriber.getToUser());
        }

        User currentUser = userDao.findById(userId);
        LOG.debug("Begin build response data");
        List<Object> data = new ArrayList<>(appointments.size());
        String thirdServer = CommonUtil.getThirdPhotoServer();
        for (Appointment appointment : appointments) {
            if (Constants.ActivityCatalog.OFFICIAL.equals(appointment.getActivityCategory())) {
                //官方活动
                OfficialActivity officialActivity = officialActivityMap.get(appointment.getActivityId());
                User user = users.get(appointment.getInvitedUserId());

                Map<String, Object> organizer = new HashMap<>(4, 1);
                organizer.put("nickname", user.getNickname());
                organizer.put("avatar", user.getAvatar());
                organizer.put("emchatName", user.getEmchatName());
                officialActivity.setOrganizer(organizer);

                officialActivity.setCovers(new String[]{thirdServer + officialActivity.getCover().getKey()});
                officialActivity.setActivityCategory(appointment.getActivityCategory());
                officialActivity.setStatus(appointment.getStatus());
                data.add(officialActivity);
            } else {
                //邀他同去， 普通邀约
                buildCommonAppointment(currentUser, users, subscriberIdSet, appointment);
                data.add(appointment);
            }
        }
        return data;
    }

    private void buildCommonAppointment(User currentUser, Map<String, User> users, Set<String> subscriberIdSet, Appointment appointment) {
        User userInfo = null;
        if (currentUser.getUserId().equals(appointment.getApplyUserId())) {
            userInfo = users.get(appointment.getInvitedUserId());
            appointment.setIsApplicant(true);
        } else {
            userInfo = users.get(appointment.getApplyUserId());
            appointment.setIsApplicant(false);
        }
        Map<String, Object> userMap = new HashMap<>(16, 1);
        userMap.put("userId", userInfo.getUserId());
        userMap.put("nickname", userInfo.getNickname());
        userMap.put("gender", userInfo.getGender());
        userMap.put("age", userInfo.getAge());
        userMap.put("role", userInfo.getRole());
        userMap.put("avatar", userInfo.getAvatar());
        userMap.put("drivingYears", userInfo.getDrivingYears());
        userMap.put("photoAuthStatus", userInfo.getPhotoAuthStatus());
        userMap.put("licenseAuthStatus", userInfo.getLicenseAuthStatus());
        userMap.put("cover", userInfo.getCover());
        userMap.put("car", userInfo.getCar());
        //是否在subscriberIdSet中 即为 是否关注了该用户
        userMap.put("subscribeFlag", subscriberIdSet.contains(userInfo.getUserId()));
        userMap.put("emchatName", userInfo.getEmchatName());
        userMap.put("distance", DistanceUtil.getDistance(currentUser.getLandmark().getLongitude(), currentUser.getLandmark().getLatitude(),
                userInfo.getLandmark().getLongitude(), userInfo.getLandmark().getLatitude()));
        appointment.setApplicant(userMap);
    }

    private Map<String, OfficialActivity> buildOfficialActivityMap(List<Appointment> appointments) {
        LOG.debug("Begin build official activities");
        Map<String, OfficialActivity> officialActivityMap = new HashMap<>();
        for (Appointment item : appointments) {
            if (Constants.ActivityCatalog.OFFICIAL.equals(item.getActivityCategory())) {
                officialActivityMap.put(item.getActivityId(), null);
            }
        }
        List<OfficialActivity> officialActivityList = officialActivityDao.findByIds(officialActivityMap.keySet());
        for (OfficialActivity item : officialActivityList) {
            officialActivityMap.put(item.getOfficialActivityId(), item);
        }

        return officialActivityMap;
    }

    private Map<String, User> buildUsers(String userId, List<Appointment> appointments) {
        User user = userDao.findById(userId);

        String localServer = CommonUtil.getLocalPhotoServer();
        String remoteServer = CommonUtil.getThirdPhotoServer();
        String gpjServer = CommonUtil.getGPJBrandLogoPrefix();

        LOG.debug("build users and hide user info");
        Map<String, User> users = new HashMap<>(appointments.size());
        for (Appointment appointment : appointments) {
            users.put(appointment.getApplyUserId(), null);
            users.put(appointment.getInvitedUserId(), null);
        }

        List<User> userList = userDao.findByIds(users.keySet());
        for (User item : userList) {
            item.hideSecretInfo();
            item.refreshPhotoInfo(localServer, remoteServer, gpjServer);
            item.setDistance(DistanceUtil.getDistance(user.getLandmark().getLongitude(), user.getLandmark().getLatitude(),
                    item.getLandmark().getLongitude(), item.getLandmark().getLatitude()));
            users.put(item.getUserId(), item);
        }

        return users;
    }

    @Override
    public ResponseDo alterUserInfo(String userId, String token, JSONObject json) throws ApiException {
        LOG.debug("Begin alert user info");

        checker.checkUserInfo(userId, token);

        Update update = new Update();
        if (!CommonUtil.isEmpty(json, "nickname")) {
            update.set("nickname", json.getString("nickname"));
        }

        if (!CommonUtil.isEmpty(json, "birthday")) {
            update.set("birthday", json.getLong("birthday"));
        }

        if (!CommonUtil.isEmpty(json, "idle")) {
            update.set("idle", json.getBoolean("idle"));
        }
        userDao.update(Query.query(Criteria.where("userId").is(userId)), update);

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo changeLocation(String userId, String token, Landmark landmark) throws ApiException {
        LOG.debug("Begin check input parameters");

        checker.checkUserInfo(userId, token);

        userDao.update(Query.query(Criteria.where("userId").is(userId)), Update.update("landmark", landmark));

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo listInterests(String userId, String token, Integer ignore, Integer limit) throws ApiException {
        LOG.debug("Begin check input parameters");
        checker.checkUserInfo(userId, token);

        List<InterestMessage> interestMessages = buildInterestMessages(userId, ignore, limit);

        Map<String, Activity> activityMap = new HashMap<>(interestMessages.size(), 1);
        Map<String, User> userMap = new HashMap<>(interestMessages.size(), 1);
        for (InterestMessage message : interestMessages) {
            if (message.getType() == InterestMessage.USER_ACTIVITY) {
                activityMap.put(message.getRelatedId(), null);
            }
            userMap.put(message.getUserId(), null);
        }

        List<Activity> activityList = activityDao.findByIds(activityMap.keySet());
        for (Activity activity : activityList) {
            activityMap.put(activity.getActivityId(), activity);
        }
        List<User> userList = userDao.findByIds(userMap.keySet());
        for (User user : userList) {
            userMap.put(user.getUserId(), user);
        }

        Map<String, Appointment> appointmentMap = buildActivityIdToAppointmentMap(userId, activityMap);

        return ResponseDo.buildSuccessResponse(buildResponseData(interestMessages, activityMap, userMap, appointmentMap, userId));
    }

    private Map<String, Appointment> buildActivityIdToAppointmentMap(String userId, Map<String, Activity> activityMap) {
        Criteria criteria = Criteria.where("applyUserId").is(userId);
        criteria.and("activityId").in(activityMap.keySet());
        List<Appointment> appointmentList = appointmentDao.find(Query.query(criteria));
        Map<String, Appointment> appointmentMap = new HashMap<>(appointmentList.size(), 1);
        for (Appointment appointment : appointmentList) {
            //注意 key 存放的 是 activityId
            appointmentMap.put(appointment.getActivityId(), appointment);
        }
        return appointmentMap;
    }

    private List<Map<String, Object>> buildResponseData(List<InterestMessage> interestMessages, Map<String, Activity> activityMap, Map<String, User> userMap, Map<String, Appointment> appointmentMap, String userId) {
        LOG.debug("Build response data");
        //fetch own user
        User ownUser = userDao.findById(userId);

        //注意 appointmentMap中 key 存放的是 activityId  即为 从 activityId 找找到对应的 appoint 因为 只会出查找  该用户对此活动申请的 appointment 所以 是唯一对应关系；

        String localServer = CommonUtil.getLocalPhotoServer();
        List<Map<String, Object>> interests = new ArrayList<>(interestMessages.size());
        for (int index = 0; index < interestMessages.size(); index++) {
            InterestMessage message = interestMessages.get(index);
            Map<String, Object> interestMap = new HashMap<>();
            interestMap.put("id", message.getId());
            interestMap.put("type", message.getType());
            interestMap.put("relatedId", message.getRelatedId());
            interestMap.put("createTime", message.getCreateTime());
            if (message.getType() == InterestMessage.USER_ACTIVITY) {
                //用户创建活动
                Activity activity = activityMap.get(message.getRelatedId());
                interestMap.put("activityType", activity.getType());
                interestMap.put("activityPay", activity.getPay());
                interestMap.put("activityTransfer", activity.isTransfer());
                interestMap.put("activityDestination", activity.getDestination());
                interestMap.put("photoCount", 0);

                //该活动距离当前用户的距离
                interestMap.put("distance", DistanceUtil.getDistance(ownUser.getLandmark().getLongitude(), ownUser.getLandmark().getLatitude(), userMap.get(activity.getUserId()).getLandmark().getLongitude(), userMap.get(activity.getUserId()).getLandmark().getLatitude()));
                Appointment appointment = appointmentMap.get(activity.getActivityId());
                if (null == appointment) {
                    //没有发送邀请;
                    interestMap.put("activityStatus", Constants.AppointmentStatus.INITIAL);
                } else {
                    interestMap.put("activityStatus", appointment.getStatus());
                }
            } else if (message.getType() == InterestMessage.USER_ALBUM) {
                //用户上传相册
                interestMap.put("activityType", "");
                interestMap.put("activityPay", "");
                interestMap.put("activityTransfer", "");
                interestMap.put("activityDestination", "");
                interestMap.put("photoCount", message.getCount());

                interestMap.put("distance", "");
                interestMap.put("activityStatus", "");
            }

            User user = userMap.get(message.getUserId());
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("gender", user.getGender());
            userInfo.put("age", user.getAge());
            userInfo.put("photoAuthStatus", user.getPhotoAuthStatus());
            userInfo.put("licenseAuthStatus", user.getLicenseAuthStatus());
            userInfo.put("car", user.getCar());
            userInfo.put("avatar", localServer + user.getAvatar());
            userInfo.put("cover", user.getCover());
            userInfo.put("subscribeFlag", true);

            interestMap.put("user", userInfo);

            interests.add(interestMap);
        }
        return interests;
    }

    private List<InterestMessage> buildInterestMessages(String userId, Integer ignore, Integer limit) {
        //获取我关注的人
        List<Subscriber> subscribers = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        Set<String> toUserIds = new HashSet<>(subscribers.size());
        for (Subscriber subscriber : subscribers) {
            toUserIds.add(subscriber.getToUser());
        }

        LOG.debug("Query interest messages");
        Query query = Query.query(Criteria.where("userId").in(toUserIds))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"))).skip(ignore).limit(limit);

        return interestMessageDao.find(query);
    }

    @Override
    public ResponseDo deleteAlbumPhotos(String userId, String token, JSONObject json) throws ApiException {
        LOG.debug("Begin check input parameters");
        checker.checkUserInfo(userId, token);
        if (CommonUtil.isArrayEmpty(json, "photos")) {
            LOG.warn("Input parameters photos is empty");
            throw new ApiException("输入参数有误");
        }
        String[] photos = CommonUtil.getStringArray(json.getJSONArray("photos"));
        User user = userDao.findById(userId);
        List<Photo> userPhotos = user.getAlbum();
        //待存入数据库的数据
        List<Photo> newAlbum = userPhotos;
        LOG.debug("check photos is exit or not");
        for (int photosIndex = 0; photosIndex < photos.length; photosIndex++) {
            for (int userPhotosIndex = 0; userPhotosIndex < userPhotos.size(); userPhotosIndex++) {
                if (photos[photosIndex].equals(userPhotos.get(userPhotosIndex).getId())) {
                    newAlbum.remove(userPhotosIndex);
                    break;
                }
                if (userPhotosIndex == userPhotos.size() - 1) {
                    LOG.warn("The photo is not exit! photo:{}", photos[photosIndex]);
                    throw new ApiException("有相片不存在");
                }
            }
        }

        LOG.debug("delete photo in qiniu");
        for (String photo : photos) {
            photoService.delete(MessageFormat.format(Constants.PhotoKey.USER_ALBUM_KEY, userId, photo));
        }
        LOG.debug("delete photo in DB");
        userDao.update(Query.query(Criteria.where("userId").is(userId)), Update.update("album", newAlbum));

        return ResponseDo.buildSuccessResponse(photos);
    }

    @Override
    public ResponseDo bindingPhone(String userId, String token, String phone, String code) throws ApiException {
        LOG.debug("begin parameter check");
        checker.checkUserInfo(userId, token);
        User user = userDao.findById(userId);
        if (!CommonUtil.isPhoneNumber(phone)) {
            LOG.warn("Phone number is not correct format");
            throw new ApiException("不是有效的手机号");
        }

        checker.checkPhoneVerifyCode(phone, code);

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            LOG.warn("The user have phone Number");
            throw new ApiException("该用户已有手机号");
        }
        LOG.debug("update the user phone :{} ", phone);
        userDao.update(Query.query(Criteria.where("userId").is(userId)), Update.update("phone", phone));

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getAuthenticationHistory(String userId, String token) throws ApiException {
        LOG.debug("check parameters");
        checker.checkUserInfo(userId, token);
        List<AuthenticationHistory> authenticationHistories = authenticationHistoryDao.find(Query.query(Criteria.where("applyUserId").is(userId)));
        Set<String> authIds = new HashSet<>(authenticationHistories.size());
        for (AuthenticationHistory authenticationHistory : authenticationHistories) {
            authIds.add(authenticationHistory.getAuthId());
        }
        List<User> authUsers = userDao.findByIds(authIds);
        Map<String, User> authUserMap = new HashMap<>(authUsers.size());
        for (User user : authUsers) {
            authUserMap.put(user.getUserId(), user);
        }

        LOG.debug("Input map parameter");
        List<Map<String, Object>> data = new ArrayList<>(authenticationHistories.size());
        for (AuthenticationHistory authenticationHistory : authenticationHistories) {
            Map<String, Object> history = new HashMap<>();
            User user = authUserMap.get(authenticationHistory.getAuthId());
            history.put("userId", user.getUserId());
            history.put("nickname", user.getNickname());
            history.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
            history.put("authTime", authenticationHistory.getAuthTime());
            history.put("type", authenticationHistory.getType());
            history.put("status", authenticationHistory.getStatus());
            history.put("content", authenticationHistory.getRemark());

            data.add(history);
        }

        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo recordUploadPhotoCount(String userId, String token, Integer count) throws ApiException {
        LOG.debug("record upload photo count:{}", count);
        checker.checkUserInfo(userId, token);

        User user = userDao.findById(userId);
        if (user.getAlbum() == null || user.getAlbum().isEmpty()) {
            LOG.warn("User:{} album count is empty", userId);
            throw new ApiException("输入参数错误");
        }

        if (count <= 0 || count > user.getAlbum().size()) {
            LOG.warn("Input parameter count:{} is over album size:{}", count, user.getAlbum().size());
            throw new ApiException("输入参数有误");
        }

        InterestMessage interestMessage = new InterestMessage();
        interestMessage.setRelatedId(userId);
        interestMessage.setUserId(userId);
        interestMessage.setType(InterestMessage.USER_ALBUM);
        interestMessage.setCount(count);
        interestMessage.setCreateTime(DateUtil.getTime());
        interestMessageDao.save(interestMessage);
        LOG.debug("Finished record message and send emchat message");

        List<Subscriber> subscribers = subscriberDao.find(Query.query(Criteria.where("toUser").is(userId)));
        if (!subscribers.isEmpty()) {
            List<String> userIds = new ArrayList<>(subscribers.size());
            for (Subscriber subscriber : subscribers) {
                userIds.add(subscriber.getFromUser());
            }

            List<User> users = userDao.findByIds(userIds);
            List<String> emchatNames = new ArrayList<>(users.size());
            for (User item : users) {
                emchatNames.add(item.getEmchatName());
            }
            String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.album", "{0}上传了{1}张照片"),
                    user.getNickname(), count);

            chatThirdService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.INTEREST,
                    emchatNames, message, null);
        }
        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 第三方登录，上传图片到本地服务器
     *
     * @param url 请求URL
     * @param key 图片 Key值
     * @throws ApiException 业务异常
     */
    private void uploadPhotoToServer(String url, String key) throws ApiException {
        LOG.debug("Download user photo from internet, url:{}", url);
        if (StringUtils.isEmpty(url)) {
            LOG.warn("Failed to obtain user photo from server");
            throw new ApiException("未能从三方登录获取头像信息");
        }

        CloseableHttpResponse response = HttpClientUtil.get(url, new HashMap<String, String>(0),
                new ArrayList<Header>(0), Constants.Charset.UTF8);
        if (!HttpClientUtil.isStatusOK(response)) {
            LOG.warn("Failed to obtain user photo from server");
            HttpClientUtil.close(response);
            throw new ApiException("未能从三方登录获取头像信息");
        }

        byte[] imageBytes = HttpClientUtil.parseResponseGetBytes(response);
        HttpClientUtil.close(response);

        LOG.debug("Upload photo to photo server");
        Map<String, String> uploadResult = localFileManager.upload(imageBytes, key, true);
        if (!Constants.Result.SUCCESS.equals(uploadResult.get("result"))) {
            // 上传失败了
            LOG.warn("Failed to upload photo to the server");
            throw new ApiException("未能成功上传图像");
        }
    }

    private void checkSnsLoginParameters(String uid, String channel, String password) throws ApiException {
        LOG.debug("Check input parameters");

        if (!Constants.Channel.CHANNEL_LIST.contains(channel)) {
            // Channel不在范围内
            LOG.warn("Input channel is not in the list, input channel:{}", channel);
            throw new ApiException("输入参数有误");
        }

        if (!isPassSignCheck(uid, channel, password)) {
            // 检查sign不通过
            LOG.warn("Input sign correct");
            throw new ApiException("输入参数有误");
        }
    }

    private boolean isPassSignCheck(String uid, String channel, String password) {
        StringBuilder builder = new StringBuilder();
        builder.append(uid);
        builder.append(channel);
        builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));

        String pass = EncoderHandler.encodeByMD5(builder.toString());
        if (pass.equals(password)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为手机注册
     *
     * @param json 请求
     * @return 手机注册返回true
     */
    private boolean isPhoneRegister(JSONObject json) {
        if (CommonUtil.isEmpty(json, "phone")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "code")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "password")) {
            return false;
        }

        return true;
    }

    /**
     * 判读是否为第三方注册
     *
     * @param json 请求参数
     * @return 第三方注册，返回true
     */
    private boolean isSnsRegister(JSONObject json) {
        if (CommonUtil.isEmpty(json, "uid")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "nickname")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "channel")) {
            return false;
        }

        String snsChannel = json.getString("channel");
        if (!Constants.Channel.CHANNEL_LIST.contains(snsChannel)) {
            // 检查Channel是否包含在Channel——List中
            LOG.warn("Input channel:{} is not in the channel list", snsChannel);
            return false;
        }

        return true;
    }

    /**
     * 检查用户注册的手机号和验证码是否正确
     *
     * @param phoneRegister 是否为手机注册
     * @param json          请求参数
     * @throws ApiException
     */
    private void checkPhoneRegister(boolean phoneRegister, JSONObject json) throws ApiException {
        if (phoneRegister) {
            String phone = json.getString("phone");
            if (!CommonUtil.isPhoneNumber(phone)) {
                LOG.warn("Phone number:{} is not correct format", phone);
                throw new ApiException("输入参数有误");
            }

            checker.checkPhoneVerifyCode(phone, json.getString("code"));

            // 判断用户是否注册过
            User user = userDao.findOne(Query.query(Criteria.where("phone").is(phone)));
            if (user != null) {
                LOG.warn("Phone already registed");
                throw new ApiException("该手机号已注册");
            }
        }
    }

    /**
     * 根据第三方注册的信息，刷新用户信息
     *
     * @param user
     * @param snsRegister
     * @param json
     */
    private void refreshUserBySnsRegister(User user, boolean snsRegister, JSONObject json) {
        if (snsRegister) {
            // SNS注册 刷新用户信息
            String snsChannel = json.getString("channel");
            String uid = json.getString("uid");
            LOG.debug("Register user by sns way, channel:{}", snsChannel);

            // 设置第三方登录密码
            StringBuilder builder = new StringBuilder();
            builder.append(uid);
            builder.append(snsChannel);
            builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));

            user.setPassword(EncoderHandler.encodeByMD5(builder.toString()));
        }
    }

    /**
     * 刷新用户的会话信息
     *
     * @param userId 用户Id
     * @return 返回会话Token字符串
     * @throws ApiException
     */
    private String refreshUserToken(String userId) throws ApiException {
        UserToken userToken = cacheManager.getUserToken(userId);
        String token = CodeGenerator.generatorId();
        if (null == userToken) {
            LOG.warn("Fail to get token and expire info from userToken, new a token");
            userToken = new UserToken();
            userToken.setUserId(userId);
            userToken.setToken(token);
            userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
                    PropertiesUtil.getProperty("carplay.token.over.date", 7)));
            userTokenDao.save(userToken);
        } else {
            userToken.setToken(token);
            userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
                    PropertiesUtil.getProperty("carplay.token.over.date", 7)));

            Update update = new Update();
            update.set("token", userToken.getToken());
            update.set("expire", userToken.getExpire());
            UserToken toFind = userTokenDao.findById(userToken.getId());
            //TODO
            if (null == toFind) {
                userToken = new UserToken();
                userToken.setId(null);
                userToken.setUserId(userId);
                userToken.setToken(token);
                userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
                        PropertiesUtil.getProperty("carplay.token.over.date", 7)));
                userTokenDao.save(userToken);
            } else {
                userTokenDao.update(userToken.getId(), update);
            }
        }
        cacheManager.setUserToken(userToken);

        return token;
    }

    @Override
    public ResponseDo getViewHistory(String userId, String token, int limit, int ignore) throws ApiException {
        LOG.debug("get user view history information, userId:{}", userId);
        checker.checkUserInfo(userId, token);

        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "viewTime"))).skip(ignore).limit(limit);
        List<AlbumViewHistory> viewHistoryList = albumViewHistoryDao.find(query);
        Set<String> userIdSet = new HashSet<>(viewHistoryList.size());
        for (AlbumViewHistory item : viewHistoryList) {
            userIdSet.add(item.getViewUserId());
        }

        List<User> userList = userDao.findByIds(userIdSet);
        //计算distance
        if (null == userList || userList.size() == 0) {
            return ResponseDo.buildSuccessResponse(new ArrayList<>(0));
        }

        Map<String, User> userMap = new HashMap<>(userList.size());
        for (User item : userList) {
            userMap.put(item.getUserId(), item);
        }

        //获取当前的用户信息；
        User nowUser = userDao.findById(userId);

        String localServer = CommonUtil.getLocalPhotoServer();
        List<Map<String, Object>> users = new ArrayList<>(userIdSet.size());
        for (AlbumViewHistory item : viewHistoryList) {
            User user = userMap.get(item.getViewUserId());
            Map<String, Object> map = new HashMap<>(8, 1);
            map.put("userId", user.getUserId());
            map.put("nickname", user.getNickname());
            map.put("gender", user.getGender());
            map.put("age", user.getAge());
            map.put("avatar", localServer + user.getAvatar());
            map.put("distance", DistanceUtil.getDistance(nowUser.getLandmark().getLongitude(), nowUser.getLandmark().getLatitude(),
                    user.getLandmark().getLongitude(), user.getLandmark().getLatitude()));
            map.put("viewTime", item.getViewTime());
            map.put("cover", user.getCover());
            users.add(map);
        }
        return ResponseDo.buildSuccessResponse(users);
    }

    @Override
    public ResponseDo getAuthHistory(String userId, String token, int limit, int ignore) throws ApiException {
        checker.checkUserInfo(userId, token);

        Criteria criteria = Criteria.where("applyUserId").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "authTime")));
        query.skip(ignore).limit(limit);
        List<AuthenticationHistory> authenticationHistoryList = authenticationHistoryDao.find(query);

//        //将所有的认证种类查询出来；
//        HashSet<String> applicationIds = new HashSet<>();
//        for (AuthenticationHistory history : authenticationHistoryList) {
//            applicationIds.add(history.getApplicationId());
//        }
//        List<AuthApplication> authApplications = authApplicationDao.findByIds((String[])applicationIds.toArray());

        //查询出所有的认证官方的信息；
        HashSet<String> authUserIds = new HashSet<>();
        for (AuthenticationHistory item : authenticationHistoryList) {
            authUserIds.add(item.getAuthId());
        }
        List<User> userList = userDao.findByIds(authUserIds);
        Map<String, User> userMap = new HashMap<>(userList.size(), 1);
        for (User user : userList) {
            userMap.put(user.getUserId(), user);
        }

        //封装返回数据
        //封装了 认证历史记录 以及 认证人的信息； authUserId 例如 对应着 车玩官方；
        for (AuthenticationHistory history : authenticationHistoryList) {
            history.setAuthUser(userMap.get(history.getAuthId()));
        }
//        JSONArray jsonArr = new JSONArray();
//        for (AuthenticationHistory history : authenticationHistoryList) {
//            JSONObject jsonObject = JSONObject.fromObject(history);
//            jsonObject.put("authUser", getUserFromList(userList, history.getAuthId()));
//        }
        return ResponseDo.buildSuccessResponse(authenticationHistoryList);
    }


    /**
     * 获取某一个用户的发布的活动信息；
     *
     * @param viewUserId
     * @param userId
     * @param limit
     * @param ignore
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo getUserActivityList(String viewUserId, String userId, int limit, int ignore) throws ApiException {
        User viewUser = userDao.findById(viewUserId);
        User nowUser = userDao.findById(userId);
        if (null == viewUser) {
            LOG.error("the view user not exist userId is:{}", viewUserId);
            throw new ApiException("查看的用户不存在");
        }
        //查看自己的活动
        if (viewUserId.equals(userId)) {
            LOG.warn("view self:viewUserId:{} userId:{}", viewUserId, userId);
        }

        //分页查询出 他的活动列表；     创建时间 降序排序
        Criteria criteria = new Criteria();
        criteria.and("userId").is(viewUserId);
        criteria.and("deleteFlag").is(false);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.skip(ignore).limit(limit);
        List<Activity> activityList = activityDao.find(query);

        if (null == activityList || activityList.isEmpty()) {
            return ResponseDo.buildSuccessResponse("[]");
        }

        //根据 activityIds applyUserId(userId) 查询出 这些活动 所对应的 我发起的 appointment
        List<String> activityIds = new ArrayList<>(activityList.size());
        for (Activity activity : activityList) {
            activityIds.add(activity.getActivityId());
        }
        Criteria appointCriteria = new Criteria();
        appointCriteria.and("applyUserId").is(userId);
        appointCriteria.and("activityId").in(activityIds);
        appointCriteria.and("activityCategory").is(Constants.ActivityCatalog.COMMON);
        List<Appointment> appointmentList = appointmentDao.find(Query.query(appointCriteria));
        if (null == appointmentList) {
            appointmentList = new ArrayList<>();
        }

        //AppointmentList 中的每一个Appointment applyUserId=userId invitedUserId=viewUserId
        //所以 activityId 可以 一一对应 AppointmentList中的 Appointment
        Map<String, Appointment> activityIdToAppointmentMap = new HashMap<>(appointmentList.size());
        for (Appointment appointment : appointmentList) {
            activityIdToAppointmentMap.put(appointment.getActivityId(), appointment);
        }

        Map<String, Object> resultMap = new HashMap<>();
        //被查看用户的照片
        resultMap.put("cover", viewUser.getCover());
        //被查看的用户与我的距离
        resultMap.put("distance", DistanceUtil.getDistance(viewUser.getLandmark().getLongitude(), viewUser.getLandmark().getLatitude(), nowUser.getLandmark().getLongitude(), nowUser.getLandmark().getLatitude()));

        //被查看用户的活动信息：基本信息+我申请该活动的状态
        ArrayList<Map<String, Object>> activityInfoList = new ArrayList<>(activityList.size());

        for (Activity activity : activityList) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("activityId", activity.getActivityId());
            itemMap.put("establish", activity.getEstablish());
            itemMap.put("estabPoint", activity.getEstabPoint());
            itemMap.put("type", activity.getType());
            itemMap.put("pay", activity.getPay());
            itemMap.put("transfer", activity.isTransfer());
            itemMap.put("destination", activity.getDestination());
            itemMap.put("destPoint", activity.getDestPoint());
            itemMap.put("createTime", activity.getCreateTime());

            //获取该活动 该用户申请状态 0 未申请 1 申请中 3 已经同意 4 已被拒绝
            int applyStatus = Constants.AppointmentStatus.INITIAL;
            Appointment appointment = activityIdToAppointmentMap.get(activity.getActivityId());
            if (null != appointment) {
                applyStatus = appointment.getStatus();
            }
            itemMap.put("status", applyStatus);

            activityInfoList.add(itemMap);
        }

        resultMap.put("activities", activityInfoList);

        return ResponseDo.buildSuccessResponse(resultMap);
    }

    /**
     * 获取环信信息
     *
     * @param emchatName 环信ID
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo getUserEmchatInfo(String emchatName) throws ApiException {
        LOG.debug("Query user infomation by emchatName:{}", emchatName);
        User user = userDao.findOne(Query.query(Criteria.where("emchatName").is(emchatName)));
        if (user == null) {
            LOG.warn("No user exist with ehcmatName:{}", emchatName);
            throw new ApiException("用户不存在");
        }

        Map<String, Object> data = new HashMap<>(8, 1);
        data.put("userId", user.getUserId());
        data.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        data.put("nickname", user.getNickname());
        data.put("emchatName", user.getEmchatName());
        data.put("gender", user.getGender());
        data.put("age", user.getAge());
        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo changePassword(String userId, String oldPwd, String newPwd) throws ApiException {
        LOG.debug("Check user old password first");

        User user = userDao.findById(userId);
        if (!user.getPassword().equals(oldPwd)) {
            LOG.warn("Input old password error");
            throw new ApiException("密码错误");
        }

        // 更新环信用户的密码
        LOG.debug("Old password is correct, change user password of huanxin");
        chatThirdService.alterUserPassword(chatCommonService.getChatToken(), user.getEmchatName(), newPwd);

        LOG.debug("Old password is correct, change password in the database");
        userDao.update(Query.query(Criteria.where("userId").is(userId)), Update.update("password", newPwd));
        return ResponseDo.buildSuccessResponse();
    }

}