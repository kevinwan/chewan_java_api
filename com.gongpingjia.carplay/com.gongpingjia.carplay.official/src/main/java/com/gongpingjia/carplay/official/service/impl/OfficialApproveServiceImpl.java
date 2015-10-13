package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.dao.user.UserAuthenticationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.user.*;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/9/28.
 */
@Service
public class OfficialApproveServiceImpl implements OfficialApproveService {

    private static Logger LOG = LoggerFactory.getLogger(OfficialApproveServiceImpl.class);

    @Autowired
    private AuthApplicationDao authApplicationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthenticationDao userAuthenticationDao;

    @Autowired
    private AuthenticationHistoryDao historyDao;

    @Override
    public ResponseDo approveUserDrivingAuthentication(String userId, JSONObject json) throws ApiException {
        LOG.debug("approveUserAuthentication start");

        String applicationId = json.getString("applicationId");
        AuthApplication application = authApplicationDao.findById(applicationId);
        if (application == null) {
            LOG.warn("Approved auth application is not exist, applicationId:{}", applicationId);
            throw new ApiException("输入参数错误");
        }

        Long current = DateUtil.getTime();
        Update update = Update.update("authUserId", userId).set("authTime", current);
        boolean accept = json.getBoolean("accept");
        String status = Constants.AuthStatus.ACCEPT;
        if (!accept) {
            status = Constants.AuthStatus.REJECT;
            if (!CommonUtil.isEmpty(json, "remarks")) {
                update.set("remarks", json.getString("remarks"));
            }
        }

        update.set("status", status);

        updateUserAuthenticationInfo(json, application.getApplyUserId());

        authApplicationDao.update(applicationId, update);

        userDao.update(application.getApplyUserId(), Update.update("licenseAuthStatus", status));

        AuthenticationHistory history = new AuthenticationHistory();
        history.setAuthTime(current);
        history.setApplicationId(applicationId);
        history.setAuthId(userId);
        history.setApplyUserId(application.getApplyUserId());
        history.setType(application.getType());
        history.setStatus(status);
        if (!CommonUtil.isEmpty(json, "remarks")) {
            history.setRemark(json.getString("remarks"));
        }
        historyDao.save(history);

        LOG.debug("Finished approved user apply");
        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 根据JSON对象更新userId 对应的证件信息
     *
     * @param json
     * @param userId
     */
    private void updateUserAuthenticationInfo(JSONObject json, String userId) {
        UserAuthentication userAuthentication = userAuthenticationDao.findById(userId);
        if (userAuthentication == null) {
            LOG.warn("User authentication is not exist with userId:{}", userId);
            return;
        }

        if (!StringUtils.isEmpty(json.getString("driver"))) {
            DriverLicense driver = (DriverLicense) JSONObject.toBean(json.getJSONObject("driver"), DriverLicense.class);
            userAuthentication.setDriver(driver);
        }

        if (!StringUtils.isEmpty(json.getString("license"))) {
            DrivingLicense license = (DrivingLicense) JSONObject.toBean(json.getJSONObject("license"), DrivingLicense.class);
            userAuthentication.setLicense(license);
        }

        userAuthenticationDao.update(userAuthentication.getUserId(), userAuthentication);
    }

    @Override
    public ResponseDo getAuthApplicationList(String userId, String type, String status, Long start, Long end, int ignore, int limit) {
        LOG.debug("getAuthApplicationList start");

        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(type)) {
            criteria.and("type").is(type);
        }
        if (StringUtils.isNotEmpty(status)) {
            criteria.and("status").is(status);
        }
        if (start != null && end != null) {
            criteria.and("applyTime").gte(start).lt(end);
        } else if (end != null) {
            criteria.and("applyTime").lt(end);
        } else if (start != null) {
            criteria.and("applyTime").gte(start);
        }

        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "applyTime")));
        query.limit(limit);
        if (ignore != 0) {
            query.skip(ignore);
        }
        List<AuthApplication> authApplicationList = authApplicationDao.find(query);

        Map<String, User> userMap = buildUserMap(authApplicationList);

        LOG.debug("Query apply user information");
        for (AuthApplication application : authApplicationList) {
            User applyUser = userMap.get(application.getApplyUserId());
            if (applyUser == null) {
                continue;
            }

            applyUser.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer());
            if (applyUser.getCar() != null) {
                applyUser.getCar().refreshPhotoInfo(CommonUtil.getGPJBrandLogoPrefix());
            }

            applyUser.hideSecretInfo();

            application.setApplyUser(applyUser);
        }

        return ResponseDo.buildSuccessResponse(authApplicationList);
    }

    /**
     * 根据申请信息构造用户信息Map
     *
     * @param authApplicationList
     * @return
     */
    private Map<String, User> buildUserMap(List<AuthApplication> authApplicationList) {
        List<String> userIds = new ArrayList<>(authApplicationList.size());
        for (AuthApplication application : authApplicationList) {
            userIds.add(application.getApplyUserId());
        }

        Map<String, User> userMap = new HashMap<>(authApplicationList.size());
        List<User> users = userDao.findByIds(userIds);
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }
        return userMap;
    }

    @Override
    public ResponseDo getApplicationInfo(String applicationId) throws ApiException {
        LOG.debug("Query application and apply user info");
        AuthApplication application = authApplicationDao.findById(applicationId);
        if (application == null) {
            LOG.warn("Input parameter applicationId:{} is not correct application", applicationId);
            throw new ApiException("输入参数有误");
        }

        User applyUser = userDao.findById(application.getApplyUserId());
        if (applyUser == null) {
            LOG.warn("Input application user is not exist");
            throw new ApiException("输入参数有误");
        }
        applyUser.hideSecretInfo();
        applyUser.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer());
        application.setApplyUser(applyUser);

        UserAuthentication userAuthentication = userAuthenticationDao.findById(application.getApplyUserId());
        if (userAuthentication != null) {
            userAuthentication.refreshPhotoInfo(CommonUtil.getLocalPhotoServer());
        }
        application.setAuthentication(userAuthentication);

        return ResponseDo.buildSuccessResponse(application);
    }

    @Override
    public ResponseDo getUserAuthenticationInfo(String authenticationId, String userId) throws ApiException {
        LOG.debug("Begin query user authentication info");

        User userAuth = userDao.findById(authenticationId);
        if (userAuth == null) {
            LOG.warn("Input parameter authenticationId:{} with no exist user", authenticationId);
            throw new ApiException("输入参数有误");
        }

        UserAuthentication userAuthentication = userAuthenticationDao.findById(authenticationId);
        userAuth.setAuthentication(userAuthentication);

        userAuth.hideSecretInfo();

        return ResponseDo.buildSuccessResponse(userAuth);
    }

    @Override
    public ResponseDo modifyUserAuthenticationInfo(String userId, JSONObject json) throws ApiException {
        LOG.debug("Begin modify user authentication info");

        updateUserAuthenticationInfo(json, userId);

        return ResponseDo.buildSuccessResponse();
    }
}
