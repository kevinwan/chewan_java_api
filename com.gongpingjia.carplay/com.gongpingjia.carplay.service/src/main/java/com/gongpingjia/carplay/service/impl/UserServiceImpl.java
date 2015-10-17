package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.history.AlbumViewHistoryDao;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.history.AlbumViewHistory;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.user.Subscriber;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.user.UserToken;
import com.gongpingjia.carplay.service.UserService;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import net.sf.json.JSONObject;
import org.apache.commons.collections.ListUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
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

        userDao.update(Query.query(Criteria.where("userId").is(user.getUserId())),
                Update.update("emchatName", chatCommonService.getUsernameByUserid(user.getUserId())));

        UserToken userToken = new UserToken();
        userToken.setUserId(user.getUserId());
        userToken.setToken(CodeGenerator.generatorId());
        userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
        userTokenDao.save(userToken);

        cacheManager.setUserToken(userToken);

        Map<String, Object> data = new HashMap<String, Object>(2, 1);
        data.put("userId", user.getUserId());
        data.put("token", userToken.getToken());
        if (StringUtils.isEmpty(user.getAvatar())) {
            data.put("avatar", "");
        } else {
            data.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
        }

        return ResponseDo.buildSuccessResponse(data);
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
            data.put("avatar", user.getAvatar());
            return ResponseDo.buildSuccessResponse(data);
        } else {
            // 用户已经存在于系统中
            LOG.debug("User is exist in the system, return login infor");
            if (userData.getCar() == null) {
                userData.setCar(new Car());
            }

            //刷新用户会话Token
            userData.setToken(refreshUserToken(userData.getUserId()));
            return ResponseDo.buildSuccessResponse(userData);
        }
    }

    @Override
    public ResponseDo getUserInfo(String beViewedUser, String viewUser, String token) throws ApiException {
        LOG.debug("Begin get user information, check input parameters");
        checker.checkUserInfo(viewUser, token);

        User user = userDao.findById(beViewedUser);
        if (user == null) {
            LOG.warn("No user exist by userId:{}", beViewedUser);
            return ResponseDo.buildFailureResponse("用户不存在");
        }

        if (!viewUser.equals(beViewedUser)) {
            //表示不是自己查看自己，beViewedUser被别人看过,记录相册查看的历史信息
            AlbumViewHistory history = new AlbumViewHistory();
            history.setViewTime(DateUtil.getTime());
            history.setViewUserId(viewUser);
            history.setUserId(beViewedUser);
            historyDao.save(history);

            String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.view", "{0}看过了我的相册"),
                    user.getNickname());
            chatThirdService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.USER_VIEW,
                    user.getEmchatName(), message);
        }

        user.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        user.setCompletion(computeCompletion(user));
        user.hideSecretInfo();

        return ResponseDo.buildSuccessResponse(user);
    }

    public ResponseDo getAppointment(String userId, String token, String status, Integer limit, Integer ignore) throws ApiException {
        LOG.debug("get user appointment infomation");
        checker.checkUserInfo(userId, token);

        Criteria criteria = Criteria.where("invitedUserId").is(userId);
        if (!StringUtils.isEmpty(status)) {
            criteria.and("status").is(status);
        }
        List<Appointment> appointments = appointmentDao.find(Query.query(Criteria.where("invitedUserId").is(userId))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "modifyTime"))).skip(ignore).limit(limit));

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            User applicantUser = userDao.findById(appointment.getApplyUserId());
            applicantUser.hideSecretInfo();

            JSONObject jsonObject = JSONObject.fromObject(appointment);
            jsonObject.put("applicant", applicantUser);

            data.add(jsonObject);
        }

        return ResponseDo.buildSuccessResponse(data);
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

        List<Subscriber> subscribers = subscriberDao.find(Query.query(Criteria.where("fromUser").is(userId)));
        List<String> toUserIds = new ArrayList<>(subscribers.size());
        for (Subscriber subscriber : subscribers) {
            toUserIds.add(subscriber.getToUser());
        }

        List<User> organizers = userDao.findByIds(toUserIds);

        Query query = Query.query(Criteria.where("userId").in(toUserIds)).with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime"))).skip(ignore).limit(limit);
        List<Activity> activitiesData = activityDao.find(query);

        //将活动创建者与用户一一对应
        for (int orgIndex = 0; orgIndex < organizers.size(); orgIndex++) {
            User organizer = organizers.get(orgIndex);
            for (int actIndex = 0; actIndex < activitiesData.size(); actIndex++) {
                Activity activity = activitiesData.get(actIndex);
                if (organizer.getUserId().equals(activity.getUserId())) {
                    organizer.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
                    organizer.hideSecretInfo();
                    activity.setOrganizer(organizer);
                    break;
                }
            }
        }

        return ResponseDo.buildSuccessResponse(activitiesData);
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

        if (!user.getPhone().isEmpty()) {
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

        LOG.debug("Input map parameter");
        List<User> data = new ArrayList<>();
        for (AuthenticationHistory authenticationHistory : authenticationHistories) {
            User user = findById(authUsers, authenticationHistory.getAuthId());
            user.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
            user.hideSecretInfo();
            data.add(user);
        }

        return ResponseDo.buildSuccessResponse(data);
    }

    private User findById(List<User> users, String id) {
        for (User user : users) {
            if (user.getUserId().equals(id)) {
                return user;
            }
        }
        return null;
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
        if (null == userToken) {
            LOG.warn("Fail to get token and expire info from userToken, new a token");
            userToken = new UserToken();
            userToken.setUserId(userId);
        }

        String uuid = CodeGenerator.generatorId();
        userToken.setToken(uuid);
        userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
                PropertiesUtil.getProperty("carplay.token.over.date", 7)));
        userTokenDao.update(userToken.getId(), userToken);

        cacheManager.setUserToken(userToken);

        return uuid;
    }

    @Override
    public ResponseDo getViewHistory(String userId, String token, int limit, int ignore) throws ApiException {
        LOG.debug("get user view history information, userId:{}", userId);
        checker.checkUserInfo(userId, token);

        //获取当前的用户信息；
        User nowUser = userDao.findById(userId);

        if (null == nowUser) {
            LOG.warn("null nowUser");
            throw new ApiException("用户数据异常");
        }

        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "viewTime"))).skip(ignore).limit(limit);
        List<AlbumViewHistory> viewHistoryList = albumViewHistoryDao.find(query);

        LinkedHashSet<String> userIdSet = new LinkedHashSet<>();
        for (AlbumViewHistory item : viewHistoryList) {
            userIdSet.add(item.getViewUserId());
        }

        List<User> userList = userDao.findByIds(userIdSet);
        //计算distance
        if (null == userList || userList.size() == 0) {
            return ResponseDo.buildSuccessResponse("[]");
        }
        List<User> users = new ArrayList<User>(userIdSet.size());
        for (String itemUId : userIdSet) {
            User userItem = getUserFromList(userList, itemUId);
            double distance = DistanceUtil.getDistance(nowUser.getLandmark().getLongitude(), nowUser.getLandmark().getLatitude(),
                    userItem.getLandmark().getLongitude(), userItem.getLandmark().getLatitude());
            userItem.setDistance(distance);
            userItem.hideSecretInfo();
            users.add(userItem);
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

        //封装返回数据
        //封装了 认证历史记录 以及 认证人的信息； authUserId 例如 对应着 车玩官方；
        for (AuthenticationHistory history : authenticationHistoryList) {
            history.setAuthUser(getUserFromList(userList, history.getAuthId()));
        }
//        JSONArray jsonArr = new JSONArray();
//        for (AuthenticationHistory history : authenticationHistoryList) {
//            JSONObject jsonObject = JSONObject.fromObject(history);
//            jsonObject.put("authUser", getUserFromList(userList, history.getAuthId()));
//        }
        return ResponseDo.buildSuccessResponse(authenticationHistoryList);
    }

    private User getUserFromList(List<User> users, String id) {
        for (User item : users) {
            if (item.getUserId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}