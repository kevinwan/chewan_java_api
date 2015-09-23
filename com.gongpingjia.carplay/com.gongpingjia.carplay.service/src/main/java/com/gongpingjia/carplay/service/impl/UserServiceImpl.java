package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.dao.user.AlbumDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.user.Album;
import com.gongpingjia.carplay.entity.user.SnsInfo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.UserToken;
import com.gongpingjia.carplay.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        String userId = user.getUserId();

        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setToken(CodeGenerator.generatorId());
        userToken.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
        userTokenDao.save(userToken);

        Album userAlbum = new Album();
        userAlbum.setAlbumId(CodeGenerator.generatorId());
        userAlbum.setUserId(userId);
        userAlbum.setCreateTime(DateUtil.getDate());
        albumDao.save(userAlbum);

        // 注册环信用户
        LOG.debug("Register emchat user by call remote service");
        Map<String, String> chatUser = new HashMap<String, String>(2, 1);
        chatUser.put("username", chatCommonService.getUsernameByUserid(user.getUserId()));
        chatUser.put("password", user.getPassword());

        JSONObject result = chatThirdService.registerChatUser(chatCommonService.getChatToken(), chatUser);
        if (result.isEmpty()) {
            LOG.warn("Create emchat user failure");
            throw new ApiException("未能成功创建环信用户");
        }

        user.setEmchatName(chatCommonService.getUsernameByUserid(user.getUserId()));
        // 注册用户
        userDao.save(user);

        cacheManager.setUserToken(userToken);

        Map<String, Object> data = new HashMap<String, Object>(2, 1);
        data.put("userId", userId);
        data.put("token", userToken.getToken());
        if (StringUtils.isEmpty(user.getPhoto())) {
            data.put("photo", "");
        } else {
            data.put("photo", CommonUtil.getLocalPhotoServer() + user.getPhoto());
        }

        return ResponseDo.buildSuccessResponse(data);
    }

    @Override
    public void checkRegisterParameters(User user, JSONObject json) throws ApiException {
        LOG.debug("Begin check input parameters of register");

        // 验证参数
        if (StringUtils.isEmpty(user.getUserId())) {
            LOG.debug("User register has not upload photo");
            user.setUserId(CodeGenerator.generatorId());
        }

        if (!StringUtils.isEmpty(user.getPhoto())) {
            user.setPhoto(MessageFormat.format(Constants.PhotoKey.USER_KEY, user.getPhoto()));
            // 判断图片是否存在
            if (!localFileManager.isExist(user.getPhoto())) {
                LOG.warn("photo not exist");
                throw new ApiException("图像未上传");
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
            Map<String, Object> param = new HashMap<String, Object>(1);
            param.put("phone", phone);
            List<User> users = userDao.find(param);
            if (users.size() > 0) {
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
            if (Constants.Channel.WECHAT.equals(snsChannel) || Constants.Channel.QQ.equals(snsChannel) || Constants.Channel.SINA_WEIBO.equals(snsChannel)) {
                snsInfo.setUid(uid);
                snsInfo.setChannel(snsChannel);
                //三方注册，未完成
            }
            // 设置第三方登录密码
            StringBuilder builder = new StringBuilder();
            builder.append(uid);
            builder.append(snsChannel);
            builder.append(PropertiesUtil.getProperty("user.password.bundle.id", ""));
            user.setPassword(EncoderHandler.encodeByMD5(builder.toString()));
        } else {
            user.setPhone(json.getString("phone"));
            user.setPassword(json.getString("password"));
        }
    }

}