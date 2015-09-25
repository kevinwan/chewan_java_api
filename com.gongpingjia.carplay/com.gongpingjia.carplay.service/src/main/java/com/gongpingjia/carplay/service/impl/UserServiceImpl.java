package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.user.UserToken;
import com.gongpingjia.carplay.service.UserService;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
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


    @Override
    public ResponseDo register(User user) throws ApiException {
        LOG.debug("Save register data begin");

        // 注册用户
        userDao.save(user);

        // 注册环信用户
        LOG.debug("Register emchat user by call remote service");
        Map<String, String> chatUser = new HashMap<String, String>(2, 1);
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

        JSONObject jsonObject = JSONObject.fromObject(userData);
        jsonObject.put("token", getUserToken(userData.getUserId()));
        jsonObject.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());

        if (StringUtils.isEmpty(userData.getPhoto())) {
            jsonObject.put("photo", "");
        } else {
            jsonObject.put("photo", CommonUtil.getLocalPhotoServer() + userData.getPhoto());
        }

        Map<String, Object> carMap = new HashMap<String, Object>(1, 1);
        // 查询用户车辆信息
        if (userData.getCar() == null) {
            carMap.put("brand", "");
            carMap.put("brandLogo", "");
            carMap.put("model", "");
            carMap.put("slug", "");
        }
        jsonObject.put("car", carMap);

        return ResponseDo.buildSuccessResponse(jsonObject);
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
        List<User> users = userDao.find(Query.query(Criteria.where("phone").is(user.getPhone())));
        if (users.isEmpty()) {
            LOG.warn("Fail to find user");
            throw new ApiException("用户不存在");
        }
        // 跟新密码
        User upUser = users.get(0);
        upUser.setPassword(user.getPassword());

        LOG.debug("Begin reset emchat account password by forget password");
        // 更新环信用户的密码
        chatThirdService.alterUserPassword(chatCommonService.getChatToken(), upUser.getEmchatName(),
                upUser.getPassword());

        userDao.update(upUser.getUserId(), upUser);

        Map<String, Object> data = new HashMap<String, Object>(2, 1);
        // 获取用户授权信息
        data.put("userId", upUser.getUserId());
        data.put("token", getUserToken(upUser.getUserId()));

        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public ResponseDo snsLogin(String uid, String channel, String sign, String username, String url)
            throws ApiException {
        checkSnsLoginParameters(uid, channel, sign);

        LOG.debug("Save data begin");
        List<User> users = userDao.find(Query.query(Criteria.where("snsInfos.uid").is(uid)));

        if (users.isEmpty()) {
            // 没有找到对应的已经存在的用户，注册新用户, 由客户端调用注册接口，这里只完成图片上传
            LOG.debug("No exist user in the system, register new user by client call register interface");

            // 头像ID
            String avatarId = CodeGenerator.generatorId();
            String key = MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, avatarId);

            uploadPhotoToServer(url, key);

            Map<String, String> data = new HashMap<String, String>(5, 1);
            data.put("uid", uid);
            data.put("nickname", username);
            data.put("channel", channel);
            data.put("avatar", avatarId);

            return ResponseDo.buildSuccessResponse(data);
        } else {
            // 用户已经存在于系统中
            LOG.debug("User is exist in the system, return login infor");
            User user = users.get(0);

            Map<String, Object> data = new HashMap<String, Object>(9, 1);
            data.put("userId", user.getUserId());
            data.put("gender", user.getGender());
            data.put("age", getAgeByBirthday(user.getBirthday()));
            data.put("token", getUserToken(user.getUserId()));
            data.put("nickname", user.getNickname());
            data.put("avatar", CommonUtil.getLocalPhotoServer() + user.getAvatar());
            if (StringUtils.isEmpty(user.getPhoto())) {
                data.put("photo", "");
            } else {
                data.put("photo", CommonUtil.getLocalPhotoServer() + user.getPhoto());
            }
            data.put("photoAuthStatus", user.getPhotoAuthStatus());
            data.put("drivingYears", user.getDrivingYears());
            data.put("licenseAuthStatus", user.getLicenseAuthStatus());

            Map<String, Object> carMap = new HashMap<String, Object>(4, 1);
            Car car = user.getCar();
            if (car != null) {
                carMap.put("brand", CommonUtil.ifNull(car.getBrand(), ""));
                carMap.put("logo", CommonUtil.getGPJBrandLogoPrefix() + CommonUtil.ifNull(car.getLogo(), ""));
                carMap.put("model", CommonUtil.ifNull(car.getModel(), ""));
                carMap.put("slug", car.getSlug());
            } else {
                carMap.put("brand", "");
                carMap.put("logo", "");
                carMap.put("model", "");
                carMap.put("slug", "");
            }
            data.put("car", carMap);
            return ResponseDo.buildSuccessResponse(data);
        }
    }

    @Override
    public ResponseDo getUserInfo(String beViewedUser, String viewUser, String token) throws ApiException {
        LOG.debug("Begin get user infomation, check input parameters");
        checker.checkUserInfo(viewUser, token);

        User user = userDao.findById(beViewedUser);
        if (user == null) {
            LOG.warn("No user exist by userId:{}", beViewedUser);
            return ResponseDo.buildFailureResponse("用户不存在");
        }

        String localPhotoServer = CommonUtil.getLocalPhotoServer();
        user.setAvatar(localPhotoServer + user.getAvatar());
        user.setPhoto(localPhotoServer + user.getPhoto());
        user.setDriverLicense(localPhotoServer + user.getDriverLicense());
        user.setDrivingLicense(localPhotoServer + user.getDrivingLicense());

        return ResponseDo.buildSuccessResponse(user);

    }

    public ResponseDo getAppointment(String userId, String token, String status, Integer limit, Integer ignore) throws ApiException {
        checker.checkUserInfo(userId, token);

        List<Appointment> appointments;

        if (status != null && !status.isEmpty()) {
            appointments = appointmentDao.find(Query.query(Criteria.where("invitedUserId").is(userId).and("status").is(status))
                    .with(new Sort(new Sort.Order(Sort.Direction.DESC, "modifyTime"))).skip(ignore).limit(limit));
        } else {
            appointments = appointmentDao.find(Query.query(Criteria.where("invitedUserId").is(userId))
                    .with(new Sort(new Sort.Order(Sort.Direction.DESC, "modifyTime"))).skip(ignore).limit(limit));
        }

        List<Map<String, Object>> data = new ArrayList<>();

        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            Activity activity = activityDao.findById(appointment.getActivityId());
            User applicantUser = userDao.findById(appointment.getApplyUserId());

            Map<String, Object> appointmentInfo = new HashMap<>();

            appointmentInfo.put("appointmentId", appointment.getAppointmentId());
            appointmentInfo.put("activityId", activity.getActivityId());
            appointmentInfo.put("type", activity.getType());
            appointmentInfo.put("destination", activity.getDestination());
            appointmentInfo.put("start", activity.getStart());
            appointmentInfo.put("pay", activity.getPay());
            appointmentInfo.put("transfer", activity.isTransfer());

            Map<String, Object> applicant = new HashMap<>(9, 1);
            applicant.put("userId", applicantUser.getUserId());
            applicant.put("nickname", applicantUser.getNickname());
            applicant.put("gender", applicantUser.getGender());
            applicant.put("age", getAgeByBirthday(applicantUser.getBirthday()));
            applicant.put("role", applicantUser.getRole());
            applicant.put("avatar", applicantUser.getAvatar());
            applicant.put("drivingYears", applicantUser.getDrivingYears());
            applicant.put("photoAuthStatus", applicantUser.getPhotoAuthStatus());
            applicant.put("licenseAuthStatus", applicantUser.getLicenseAuthStatus());

            Map<String, Object> car = new HashMap<>(2, 1);
            if (applicantUser.getCar() != null) {
                car.put("logo", applicantUser.getCar().getLogo());
                car.put("model", applicantUser.getCar().getModel());
            } else {
                car.put("logo", "");
                car.put("model", "");
            }
            applicant.put("car", car);
            appointmentInfo.put("applicant", applicant);

            appointmentInfo.put("status", appointment.getStatus());
            data.add(appointmentInfo);
        }

        return ResponseDo.buildSuccessResponse(data);
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

        CloseableHttpResponse response = HttpClientUtil.get(url, new HashMap<String, String>(0), new ArrayList<Header>(
                0), Constants.Charset.UTF8);
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

    private void checkSnsLoginParameters(String uid, String channel, String sign) throws ApiException {
        LOG.debug("Check input parameters");

        if (!Constants.Channel.CHANNEL_LIST.contains(channel)) {
            // Channel不在范围内
            LOG.warn("Input channel is not in the list, input channel:{}", channel);
            throw new ApiException("输入参数有误");
        }

        if (!isPassSignCheck(uid, channel, sign)) {
            // 检查sign不通过
            LOG.warn("Input sign correct");
            throw new ApiException("输入参数有误");
        }
    }

    private boolean isPassSignCheck(String uid, String channel, String sign) {
        StringBuilder builder = new StringBuilder();
        builder.append(uid);
        builder.append(channel);
        builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));

        String pass = EncoderHandler.encodeByMD5(builder.toString());
        if (pass.equals(sign)) {
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
        if (CommonUtil.isEmpty(json, "snsUid")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "snsUserName")) {
            return false;
        }

        if (CommonUtil.isEmpty(json, "snsChannel")) {
            return false;
        }

        String snsChannel = json.getString("snsChannel");
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

    private String getUserToken(String userId) throws ApiException {
        UserToken userToken = cacheManager.getUserTokenVerification(userId);
        if (null == userToken) {
            LOG.warn("Fail to get token and expire info from token_verification");
            throw new ApiException("获取用户授权信息失败");
        }

        // 如果过期 跟新Token
        if (userToken.getExpire() > DateUtil.getTime()) {
            return userToken.getToken();
        }

        String uuid = CodeGenerator.generatorId();
        userToken.setToken(uuid);
        userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE,
                PropertiesUtil.getProperty("carplay.token.over.date", 7)));
        userTokenDao.update(userToken.getId(), userToken);

        cacheManager.setUserToken(userToken);

        return uuid;
    }

    /**
     * @param Birthday 生日
     *                 <p/>
     *                 计算年龄
     */
    public int getAgeByBirthday(Long Birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtil.getTime());
        Calendar userCal = Calendar.getInstance();
        userCal.setTimeInMillis(Birthday);
        return calendar.get(Calendar.YEAR) - userCal.get(Calendar.YEAR);
    }


    @Override
    public ResponseDo getViewHistory(String userId, String token) throws ApiException {
        checker.checkUserInfo(userId, token);

        //



        return ResponseDo.buildSuccessResponse();
    }
}