package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
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

        checker.checkUserInfo(userId, token);

        User user = userDao.findById(userId);
        if (user == null) {
            LOG.warn("User not exist, userId:{}", userId);
            return ResponseDo.buildFailureResponse("用户不存在");
        }
        if (!Constants.AuthStatus.UNAUTHORIZED.equals(user.getLicenseAuthStatus())) {
            LOG.warn("User already authenticated");
            return ResponseDo.buildFailureResponse("用户已认证，请勿重复认证");
        }

        String drivingLicense = MessageFormat.format(Constants.PhotoKey.DRIVING_LICENSE_KEY, json.getString("drivingLicense"));
        // 判断图片是否存在
        if (!localFileManager.isExist(drivingLicense)) {
            LOG.warn("drivingLicense photo not Exist");
            return ResponseDo.buildFailureResponse("行驶证图片未上传");
        }

        String driverLicense = MessageFormat.format(Constants.PhotoKey.DRIVER_LICENSE_KEY, json.getString("driverLicense"));
        // 判断图片是否存在
        if (!localFileManager.isExist(driverLicense)) {
            LOG.warn("driverLicense photo not Exist");
            return ResponseDo.buildFailureResponse("驾驶证图片未上传");
        }

        LOG.debug("record application information");
        Long current = DateUtil.getTime();

        AuthApplication application = new AuthApplication();
        application.setApplyTime(current);
        application.setStatus(Constants.AuthStatus.AUTHORIZING);
        application.setType(Constants.AuthType.LICENSE_AUTH);
        application.setUserId(userId);
        authApplicationDao.save(application);

        AuthenticationHistory history = new AuthenticationHistory();
        history.setApplicationId(application.getApplicationId());
        history.setStatus(application.getStatus());
        history.setAuthTime(current);
        history.setRemark("车主认证申请");
        historyDao.save(history);

        Update update = new Update();
        update.set("driverLicense", driverLicense);
        update.set("drivingLicense", drivingLicense);

        Car car = new Car();
        car.setBrand(json.getString("brand"));
        car.setModel(json.getString("model"));
        update.set("car", car);

        userDao.update(Query.query(Criteria.where("userId").is(user.getUserId())), update);

        return ResponseDo.buildSuccessResponse();
    }
}
