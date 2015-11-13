package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.history.AlbumAuthHistoryDao;
import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.dao.user.UserAuthenticationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.history.AlbumAuthHistory;
import com.gongpingjia.carplay.entity.history.AuthenticationHistory;
import com.gongpingjia.carplay.entity.user.*;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import com.gongpingjia.carplay.service.impl.ChatCommonService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

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

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService thirdPhotoService;

    @Autowired
    private AlbumAuthHistoryDao albumAuthHistoryDao;

    @Override
    public ResponseDo approveUserDrivingAuthentication(String userId, JSONObject json) throws ApiException {
        LOG.debug("approveUserAuthentication start");

        String applicationId = json.getString("applicationId");
        AuthApplication application = authApplicationDao.findById(applicationId);
        if (application == null) {
            LOG.warn("Approved auth application is not exist, applicationId:{}", applicationId);
            throw new ApiException("输入参数错误");
        }

        String status = getStatus(json.getBoolean("accept"));
        String remarks = getRemarks(json);

        Long current = DateUtil.getTime();
        Update update = Update.update("authUserId", userId).set("authTime", current);
        if (Constants.AuthStatus.ACCEPT.equals(status)) {
            update.set("remarks", "");
        } else {
            update.set("remarks", remarks);
        }
        update.set("status", status);

        updateUserAuthenticationInfo(json, application.getApplyUserId());

        authApplicationDao.update(applicationId, update);

        userDao.update(application.getApplyUserId(), Update.update("licenseAuthStatus", status));

        recordHistory(userId, application, current, status, remarks);
        sendEmchatMessage(userId, application, status, remarks, Constants.MessageType.LICENSE_AUTH_MSG);

        LOG.debug("Finished approved user driving authentication apply");
        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 获取备注信息
     *
     * @param json
     * @return
     */
    private String getRemarks(JSONObject json) {
        String remarks = "";
        if (!CommonUtil.isEmpty(json, "remarks")) {
            remarks = json.getString("remarks");
        }
        return remarks;
    }

    /**
     * 根据审批结果获取审批状态
     */
    private String getStatus(Boolean accept) {
        String status = Constants.AuthStatus.ACCEPT;
        if (!accept) {
            status = Constants.AuthStatus.REJECT;
        }
        return status;
    }

    /**
     * 记录审批历史信息。并向用户发送消息
     *
     * @param authUserId
     * @param application
     * @param current
     * @param status
     * @param remarks
     * @throws ApiException
     */
    private void recordHistory(String authUserId, AuthApplication application, Long current, String status, String remarks) throws ApiException {
        AuthenticationHistory history = new AuthenticationHistory();
        history.setAuthTime(current);
        history.setApplicationId(application.getApplicationId());
        history.setAuthId(authUserId);
        history.setApplyUserId(application.getApplyUserId());
        history.setType(application.getType());
        history.setStatus(status);
        history.setRemark(remarks);
        historyDao.save(history);
    }

    private void sendEmchatMessage(String authUserId, AuthApplication application, String status, String remarks, int messageType) throws ApiException {
        Map<String, Object> ext = new HashMap<>(8, 1);
        ext.put("type", messageType);
        ext.put("result", Constants.Flag.POSITIVE);

        User authUser = userDao.findById(authUserId);
        ext.put("nickName", authUser.getNickname());
        ext.put("headUrl", CommonUtil.getLocalPhotoServer() + authUser.getAvatar());
        ext.put("userId", authUserId);

        User user = userDao.findById(application.getApplyUserId());
        String result = "通过";
        if (Constants.AuthStatus.REJECT.equals(status)) {
            result = "未通过";
            ext.put("result", Constants.Flag.NEGATIVE);
        }
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.authentication", "您的{0}审核{1}"),
                application.getType(), result);
        if (Constants.AuthStatus.REJECT.equals(status)) {
            message += "\n原因：" + remarks + "\n重新认证";
        }

        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                Arrays.asList(user.getEmchatName()), message, ext);
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
        Update update = new Update();
        if (!StringUtils.isEmpty(json.getString("driver"))) {
            DriverLicense driver = (DriverLicense) JSONObject.toBean(json.getJSONObject("driver"), DriverLicense.class);
            update.set("driver", driver);
//            userAuthentication.setDriver(driver);
        }

        if (!StringUtils.isEmpty(json.getString("license"))) {
            DrivingLicense license = (DrivingLicense) JSONObject.toBean(json.getJSONObject("license"), DrivingLicense.class);
//            userAuthentication.setLicense(license);
            update.set("license", license);
        }

        userAuthenticationDao.update(userAuthentication.getUserId(), update);
    }

    @Override
    public ResponseDo getAuthApplicationList(String userId, String type, String status, Long start, Long end, String phone) {
        LOG.debug("getAuthApplicationList start");

        String applyUserId = null;
        if (StringUtils.isNotEmpty(phone)) {
            User queryUser = userDao.findOne(Query.query(Criteria.where("phone").is(phone)));
            if (queryUser == null) {
                LOG.debug("No auth applications with user phone:{}", phone);
                return ResponseDo.buildSuccessResponse(new ArrayList<>(0));
            }
            applyUserId = queryUser.getUserId();
        }

        Query query = buildQueryParam(type, status, start, end, applyUserId);

        List<AuthApplication> authApplicationList = authApplicationDao.find(query);

        Map<String, User> userMap = buildUserMap(authApplicationList);

        LOG.debug("Query apply user information");
        for (AuthApplication application : authApplicationList) {
            User applyUser = userMap.get(application.getApplyUserId());
            if (applyUser == null) {
                continue;
            }
            applyUser.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
            if (applyUser.getCar() != null) {
                applyUser.getCar().refreshPhotoInfo(CommonUtil.getGPJBrandLogoPrefix());
            }

            applyUser.hideSecretInfo();
            application.setApplyUser(applyUser);
        }

        return ResponseDo.buildSuccessResponse(authApplicationList);
    }

    @Override
    public List<AuthenticationHistory> buildAuthHistory(String applicationId) {
        LOG.debug("ApplicationId:{}", applicationId);
        return historyDao.find(Query.query(Criteria.where("applicationId").is(applicationId))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "authTime"))));
    }

    /**
     * 构造查询条件
     *
     * @param type
     * @param status
     * @param start
     * @param end
     * @return
     */
    private Query buildQueryParam(String type, String status, Long start, Long end, String applyUserId) {
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(applyUserId)) {
            criteria.and("applyUserId").is(applyUserId);
        }
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
//        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "status")));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "applyTime")));
        return query;
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
        applyUser.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        application.setApplyUser(applyUser);

        UserAuthentication userAuthentication = userAuthenticationDao.findById(application.getApplyUserId());
        if (userAuthentication != null) {
            userAuthentication.refreshPhotoInfo(CommonUtil.getLocalPhotoServer());
        }
        application.setAuthentication(userAuthentication);

        application.setAuthHistorys(buildAuthHistory(applicationId));

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

    @Override
    public ResponseDo approveUserPhotoAuthentication(String userId, JSONObject json) throws ApiException {
        String applicationId = json.getString("applicationId");
        AuthApplication application = authApplicationDao.findById(applicationId);
        if (application == null) {
            LOG.warn("authApplication is not exit with applicationId:{}", applicationId);
            throw new ApiException("输入参数有误");
        }

        String status = getStatus(json.getBoolean("accept"));
        String remarks = getRemarks(json);

        LOG.debug("Begin update data");
        Long current = DateUtil.getTime();
        Update update = new Update();
        update.set("authTime", current);
        update.set("authUserId", userId);
        update.set("status", status);
        if (Constants.AuthStatus.ACCEPT.equals(status)) {
            update.set("remarks", "");
        } else {
            update.set("remarks", remarks);
        }

        authApplicationDao.update(applicationId, update);

        userDao.update(application.getApplyUserId(), Update.update("photoAuthStatus", status));

        recordHistory(userId, application, current, status, remarks);

        sendEmchatMessage(userId, application, status, remarks, Constants.MessageType.PHOTO_AUTH_MSG);

        LOG.debug("Finished approved user photo authentication apply");
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo authUserAlbum(String userId, JSONObject json) throws ApiException {
        LOG.debug("Auth user album and check user info");
        User applyUser = userDao.findById(json.getString("userId"));
        if (applyUser == null) {
            LOG.warn("auth user:{} is not exist in the system", json.getString("userId"));
            throw new ApiException("输入参数错误");
        }
        if (applyUser.getAlbum() == null || applyUser.getAlbum().isEmpty()) {
            LOG.warn("auth user album is empty");
            return ResponseDo.buildSuccessResponse();
        }

        JSONArray photoIds = json.getJSONArray("photoIds");
        if (photoIds.isEmpty()) {
            userDao.update(Query.query(Criteria.where("userId").is(applyUser.getUserId())),
                    Update.update("albumStatus", Constants.UserAlbumAtuhStatus.AUTHENTICATED));
            return ResponseDo.buildSuccessResponse();
        }

        List<Photo> deletePhotos = new ArrayList<>(applyUser.getAlbum().size());
        List<Photo> leftPhotos = new ArrayList<>(applyUser.getAlbum().size());
        for (Photo item : applyUser.getAlbum()) {
            if (photoIds.contains(item.getId())) {
                deletePhotos.add(item);
            } else {
                leftPhotos.add(item);
            }
        }

        LOG.debug("Update user data first and remove photo on the server later");
        Update update = new Update();
        update.set("album", leftPhotos);
        update.set("albumStatus", Constants.UserAlbumAtuhStatus.AUTHENTICATED);  //相册审核完成
        userDao.update(Query.query(Criteria.where("userId").is(applyUser.getUserId())), update);
        for (Photo item : deletePhotos) {
            thirdPhotoService.delete(item.getKey());
        }

        AlbumAuthHistory history = new AlbumAuthHistory();
        history.setApplyUserId(applyUser.getUserId());
        history.setAuthUserId(userId);
        history.setAuthTime(DateUtil.getTime());
        history.setApplyTime(applyUser.getAlbumModifyTime());
        history.setRemark(json.containsKey("remark") ? json.getString("remark") : "");
        history.setPhotos(deletePhotos);
        albumAuthHistoryDao.save(history);

        LOG.debug("Update user data finished and send emcahat message");
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                applyUser.getEmchatName(), "你的相册照片违反车玩规定，已经给予删除。");

        return ResponseDo.buildSuccessResponse();
    }
}
