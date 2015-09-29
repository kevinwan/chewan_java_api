package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Car;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.user.AuthApplication;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.AunthenticationService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by licheng on 2015/9/23.
 */
@Service
public class AunthenticationServiceImpl implements AunthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    @Qualifier("localFileManager")
    private PhotoService localFileManager;

    @Autowired
    private ParameterChecker checker;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthApplicationDao authApplicationDao;

    @Autowired
    private AuthenticationHistoryDao historyDao;

    @Override
    public ResponseDo licenseAuthenticationApply(JSONObject json, String token, String userId) throws ApiException {
        LOG.debug("Begin apply license authentication");

        User user = checkApplyUserInfo(json, token, userId);

        String drivingLicenseKey = MessageFormat.format(Constants.PhotoKey.DRIVING_LICENSE_KEY, json.getString("drivingLicense"));
        // 判断图片是否存在
        if (!localFileManager.isExist(drivingLicenseKey)) {
            LOG.warn("drivingLicense photo not Exist");
            throw new ApiException("行驶证图片未上传");
        }

        String driverLicenseKey = MessageFormat.format(Constants.PhotoKey.DRIVER_LICENSE_KEY, json.getString("driverLicense"));
        // 判断图片是否存在
        if (!localFileManager.isExist(driverLicenseKey)) {
            LOG.warn("driverLicense photo not Exist");
            throw new ApiException("驾驶证图片未上传");
        }

        LOG.debug("record application information");
        Long current = DateUtil.getTime();

        Update update = new Update();
        update.set("driverLicense", driverLicenseKey);
        update.set("drivingLicense", drivingLicenseKey);
        update.set("licenseAuthStatus", Constants.AuthStatus.AUTHORIZING);
        Car car = new Car();
        car.setBrand(json.getString("brand"));
        car.setModel(json.getString("model"));
        update.set("car", car);

        userDao.update(Query.query(Criteria.where("userId").is(user.getUserId())), update);

        AuthApplication application = new AuthApplication();
        application.setApplyTime(current);
        application.setStatus(Constants.AuthStatus.AUTHORIZING);
        application.setType(Constants.AuthType.LICENSE_AUTH);
        application.setApplyUserId(userId);
        authApplicationDao.save(application);

        AuthenticationHistory history = new AuthenticationHistory();
        history.setApplicationId(application.getApplicationId());
        history.setStatus(application.getStatus());
        history.setAuthTime(current);
        history.setRemark("车主认证申请");
        historyDao.save(history);

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 校验申请人的信息
     *
     * @param json
     * @param token
     * @param userId
     * @return
     * @throws ApiException
     */
    private User checkApplyUserInfo(JSONObject json, String token, String userId) throws ApiException {
        checker.checkUserInfo(userId, token);

        if (!CommonUtil.isUUID(json.getString("drivingLicense")) || !CommonUtil.isUUID(json.getString("driverLicense"))) {
            LOG.warn("Input parameter drivingLicense or driverLicense is not uuid");
            throw new ApiException("输入参数有误");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            LOG.warn("User not exist, userId:{}", userId);
            throw new ApiException("用户不存在");
        }

        if (!Constants.AuthStatus.AUTHORIZING.equals(user.getLicenseAuthStatus())) {
            LOG.warn("User already authenticated");
            throw new ApiException("用户认证中，请勿重复申请");
        }

        if (!Constants.AuthStatus.ACCEPT.equals(user.getLicenseAuthStatus())) {
            LOG.warn("User already authenticated");
            throw new ApiException("用户已认证，请勿重复认证");
        }
        return user;
    }

    @Override
    public ResponseDo photoAuthenticationApply(String userId, String token, JSONObject json) throws ApiException {
        LOG.debug("Begin photo authentication apply check params");

        checker.checkUserInfo(userId, token);

        if (!CommonUtil.isUUID(json.getString("photoId"))) {
            LOG.warn("Input parameter photoId is not uuid");
            throw new ApiException("输入参数有误");
        }

        String photoKey = MessageFormat.format(Constants.PhotoKey.PHOTO_KEY, json.getString("photoId"));
        if (!localFileManager.isExist(photoKey)) {
            LOG.warn("user photo not Exist");
            throw new ApiException("个人图像未上传");
        }

        LOG.debug("Begin save data");
        Long current = DateUtil.getTime();

        Update update = new Update();
        update.set("photo", photoKey);
        update.set("photoAuthStatus", Constants.AuthStatus.AUTHORIZING);
        userDao.update(Query.query(Criteria.where("userId").is(userId)), update);

        AuthApplication application = new AuthApplication();
        application.setApplyTime(current);
        application.setStatus(Constants.AuthStatus.AUTHORIZING);
        application.setType(Constants.AuthType.PHOTO_AUTH);
        application.setApplyUserId(userId);
        authApplicationDao.save(application);

        AuthenticationHistory history = new AuthenticationHistory();
        history.setApplicationId(application.getApplicationId());
        history.setStatus(application.getStatus());
        history.setAuthTime(current);
        history.setRemark("个人图像认证申请");
        historyDao.save(history);

        return ResponseDo.buildSuccessResponse();
    }
}
