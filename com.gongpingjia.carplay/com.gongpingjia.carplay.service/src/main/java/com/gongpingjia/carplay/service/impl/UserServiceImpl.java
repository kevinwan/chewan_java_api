package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.user.AlbumDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.user.Album;
import com.gongpingjia.carplay.entity.user.SnsInfo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.user.UserToken;
import com.gongpingjia.carplay.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.util.calendar.CalendarDate;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AlbumDao albumDao;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private ChatThirdPartyService chatThirdService;

    @Autowired
    private CacheManager cacheManager;


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

        Album userAlbum = new Album();
        userAlbum.setAlbumId(CodeGenerator.generatorId());
        userAlbum.setUserId(user.getUserId());
        userAlbum.setCreateTime(DateUtil.getTime());
        albumDao.save(userAlbum);

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

        Map<String, Object> data = new HashMap<String, Object>(8, 1);
        // 验证参数
        if (!CommonUtil.isPhoneNumber(user.getPhone())) {
            LOG.warn("Invalid params, phone:{}", user.getPhone());
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        // 查找用户

        List<User> users = userDao.find(Query.query(Criteria.where("phone").is(user.getPhone())));
        if (users.isEmpty()) {
            LOG.warn("Fail to find user");
            return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
        }

        User userData = users.get(0);
        if (!user.getPassword().equals(userData.getPassword())) {
            LOG.warn("User password is incorrect");
            return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
        }

        data.put("userId", userData.getUserId());
        data.put("photoAuthStatus", userData.getPhotoAuthStatus());
        data.put("licenseAuthStatus", userData.getLicenseAuthStatus());
        data.put("nickname", userData.getNickname());
        data.put("gender", userData.getGender());
        data.put("age", DateUtil.getDate().getYear() - DateUtil.getDate(userData.getBirthday()).getYear());
        data.put("drivingYears", userData.getDrivingYears());
        data.put("photo", userData.getPhoto());

        // 获取用户授权信息
        data.put("token", getUserToken(userData.getUserId()));

        if (StringUtils.isEmpty(userData.getPhoto())) {
            data.put("avatar", "");
        } else {
            data.put("avatar", CommonUtil.getLocalPhotoServer() + userData.getPhoto());
        }

        Map<String, Object> carMap = new HashMap<String, Object>(1, 1);
        // 查询用户车辆信息
        Car car = userData.getCar();
        if (null != car) {
            carMap.put("brand", car.getBrand());
            carMap.put("brandLogo", CommonUtil.getGPJBrandLogoPrefix() + car.getLogo());
            carMap.put("model", car.getModel());
            carMap.put("photoCount", car.getSeat());
        } else {
            carMap.put("brand", "");
            carMap.put("brandLogo", "");
            carMap.put("model", "");
            carMap.put("photoCount", "");
        }
        data.put("car", carMap);

        return ResponseDo.buildSuccessResponse(data);
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
            String snsChannel = json.getString("snsChannel");
            String uid = json.getString("snsUid");
            LOG.debug("Register user by sns way, snsChannel:{}", snsChannel);
            SnsInfo snsInfo = new SnsInfo();
            snsInfo.setUid(uid);
            snsInfo.setChannel(snsChannel);

            // 设置第三方登录密码
            StringBuilder builder = new StringBuilder();
            builder.append(uid);
            builder.append(snsChannel);
            builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));

            user.setPassword(EncoderHandler.encodeByMD5(builder.toString()));

            if (!StringUtils.isEmpty(json.getString("snsUserName"))) {
                user.setNickname(json.getString("snsUserName"));
            }
        } else {
            user.setPhone(json.getString("phone"));
            user.setPassword(json.getString("password"));
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
}