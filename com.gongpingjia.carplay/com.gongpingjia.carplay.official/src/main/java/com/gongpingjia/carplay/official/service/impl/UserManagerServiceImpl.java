package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.common.PhotoDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.official.service.UserManagerService;
import com.gongpingjia.carplay.service.impl.ChatCommonService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

import java.util.*;

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

    @Autowired
    private PhotoDao photoDao;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Override
    public ResponseDo listUsers(String phone, String nickname, String licenseAuthStatus, String photoAuthStatus,
                                Long start, Long end) {
        LOG.debug("Begin build query criteria");

//        Criteria criteria = Criteria.where("registerTime").gte(start).lt(end);
        Criteria criteria = new Criteria();
        if (null != end && null != start) {
            criteria.and("registerTime").gte(start).lte(end + Constants.DAY_MILLISECONDS);
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
        List<User> userList = userDao.find(Query.query(criteria).with(new Sort(new Sort.Order(Sort.Direction.DESC, "registerTime"))));
        List<Map<String, Object>> userMapList = new ArrayList<>(userList.size());
        for (User user : userList) {
            userMapList.add(user.buildCommonUserMap());
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

        Map<String, Object> map = user.buildCommonUserMap();
        User.appendAlbum(map, photoDao.getUserAlbum(userId));

        return ResponseDo.buildSuccessResponse(map);
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
        userDao.update(userId, update);

        LOG.debug("Update user photo info ");
        if (!CommonUtil.isEmpty(jsonObject, "photoIds")) {
            List<Photo> userPhotos = photoDao.getUserAlbum(userId);
            Map<String, Photo> photoMap = new HashMap<>(userPhotos.size(), 1);
            for (Photo item : userPhotos) {
                photoMap.put(item.getId(), item);
            }

            JSONArray photoIds = jsonObject.getJSONArray("photoIds");
            List<String> idList = new ArrayList<>(photoIds.size());
            idList.addAll(photoIds);
            photoDao.deleteUserPhotos(userId, idList);

            deletePhotosRemoteServer(photoMap, idList);

            LOG.debug("Send users emcahat message");
            String message = PropertiesUtil.getProperty("dynamic.format.delete.album.notice", "你的相册照片违反车玩规定，已经给予删除。");
            chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                    Arrays.asList(user.getEmchatName()), message, new Object());
        }


        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 删除远程服务器相片文件
     *
     * @param deletePhotoIds 相片的Key列表
     */
    private void deletePhotosRemoteServer(Map<String, Photo> userPhotoMap, List<String> deletePhotoIds) {
        for (String id : deletePhotoIds) {
            try {
                Photo photo = userPhotoMap.get(id);
                if (photo != null) {
                    thirdPhotoManager.delete(photo.getKey());
                } else {
                    LOG.warn("Wrong photo id:{}", id);
                }
            } catch (Exception e) {
                LOG.warn("Delete photo failure, photoId:{}", id);
                LOG.warn(e.getMessage(), e);
            }
        }
    }
}
