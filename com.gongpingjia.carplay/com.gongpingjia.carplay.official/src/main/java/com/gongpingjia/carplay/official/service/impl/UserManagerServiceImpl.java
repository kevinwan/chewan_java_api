package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.official.service.UserManagerService;
import net.sf.json.JSONArray;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 123 on 2015/10/19.
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService thirdPhotoManager;

    @Override
    public ResponseDo listUsers(String phone, String nickname, String licenseAuthStatus, String photoAuthStatus,
                                Long start, Long end, Integer limit, Integer ignore) {
        LOG.debug("Begin build query criteria");

//        Criteria criteria = Criteria.where("registerTime").gte(start).lt(end);
        Criteria criteria = new Criteria();
        if (null != end && null != start) {
            end = end + 24*60*60*1000;
            criteria.and("registerTime").gte(start).lte(end);
        }

        if (!StringUtils.isEmpty(phone)) {
            criteria.and("phone").is(phone);
        }
        if (!StringUtils.isEmpty(nickname)) {
            criteria.and("nickname").is(nickname);
        }
        if (!StringUtils.isEmpty(licenseAuthStatus)) {
            criteria.and("licenseAuthStatus").is(licenseAuthStatus);
        }
        if (!StringUtils.isEmpty(photoAuthStatus)) {
            criteria.and("photoAuthStatus").is(photoAuthStatus);
        }

        LOG.debug("query users by criteria and refresh user info");
        List<User> userList = userDao.find(Query.query(criteria).limit(limit).skip(ignore));
        for (User user : userList) {
            user.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
            user.hideSecretInfo();
        }

        return ResponseDo.buildSuccessResponse(userList);
    }

    @Override
    public ResponseDo viewUserDetail(String userId) throws ApiException {
        LOG.debug("Query uer detail, userId:{}", userId);
        User user = userDao.findById(userId);
        if (user == null) {
            LOG.warn("User is not exist by userId:{}", userId);
            throw new ApiException("输入参数错误");
        }

        user.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer(), CommonUtil.getGPJBrandLogoPrefix());
        user.hideSecretInfo();

        return ResponseDo.buildSuccessResponse(user);
    }

    @Override
    public ResponseDo updateUserDetail(String userId, JSONObject jsonObject) throws ApiException {
        LOG.debug("Begin check parameter userId:{}, update json:{}", userId, jsonObject);
        User user = userDao.findById(userId);
        if (user == null) {
            LOG.warn("Input userId is not exist, userId:{}", userId);
            throw new ApiException("输入参数错误");
        }

        Update update = new Update();
        if (!CommonUtil.isEmpty(jsonObject, "role")) {
            String role = jsonObject.getString("role");
            if (!Constants.UserCatalog.ROLES.contains(role)) {
                LOG.warn("Input parameter role:{} is not in the role list", role);
                throw new ApiException("输入参数错误");
            }
            update.set("role", role);
        }

        if (!CommonUtil.isEmpty(jsonObject, "deleteFlag")) {
            update.set("deleteFlag", jsonObject.getBoolean("deleteFlag"));
        }

        List<String> deletePhotoKeys = new ArrayList<>();
        if (!CommonUtil.isEmpty(jsonObject, "photoIds") && user.getAlbum() != null) {
            List<Photo> album = new ArrayList<>();
            JSONArray photoIds = jsonObject.getJSONArray("photoIds");
            for (Photo photo : user.getAlbum()) {
                if (photoIds.contains(photo.getId())) {
                    album.add(photo);
                } else {
                    deletePhotoKeys.add(photo.getKey());
                }
            }
            update.set("album", album);
        }

        userDao.update(userId, update);

        deletePhotosRemoteServer(deletePhotoKeys);

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 删除远程服务器相片文件
     *
     * @param deletePhotoKeys 相片的Key列表
     */
    private void deletePhotosRemoteServer(List<String> deletePhotoKeys) {
        for (String key : deletePhotoKeys) {
            try {
                thirdPhotoManager.delete(key);
            } catch (Exception e) {
                LOG.warn("Delete photo failure, key:{}", key);
                LOG.warn(e.getMessage(), e);
            }
        }
    }
}
