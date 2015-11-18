package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.*;
import com.gongpingjia.carplay.dao.common.PhotoDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadServiceImpl implements UploadService {

    private static final Logger LOG = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private ParameterChecker checker;

    /**
     * 图片上传
     */
    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService thirdPhotoManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PhotoDao photoDao;

    /**
     * 对存放在本地文件的操
     */
    @Autowired
    @Qualifier("localFileManager")
    private PhotoService localFileManager;

    @Override
    public ResponseDo uploadAvatarPhoto(MultipartFile multiFile) throws ApiException {
        LOG.debug("Begin upload file to server");

        byte[] data = FileUtil.buildFileBytes(multiFile);

        String id = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, id);

        return uploadLocalServer(id, data, key);
    }

    @Override
    public ResponseDo uploadPersonalPhoto(MultipartFile multipartFile, String userId, String token) throws ApiException {
        LOG.debug("Begin upload file to server");

        checker.checkUserInfo(userId, token);

        byte[] data = FileUtil.buildFileBytes(multipartFile);

        String id = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.PHOTO_KEY, id);

        return uploadLocalServer(id, data, key);
    }

    /**
     * 上传图片
     *
     * @param data    photo二进制数据文件
     * @param photoId photo的ID件
     * @param key     七牛服务器唯一识别的Key值
     * @return 返回上传结果对象
     * @throws ApiException 义务异常
     */
    private ResponseDo uploadThirdServer(byte[] data, String photoId, String key, boolean override) throws ApiException {
        Map<String, String> result = thirdPhotoManager.upload(data, key, override);
        LOG.debug("Upload result: {}", result);
        if (Constants.Result.SUCCESS.equalsIgnoreCase(result.get("result"))) {
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("photoUrl", CommonUtil.getThirdPhotoServer() + result.get("key"));
            dataMap.put("photoKey", result.get("key"));
            dataMap.put("photoId", photoId);
            return ResponseDo.buildSuccessResponse(dataMap);
        } else {
            LOG.error("Upload avatar resource failure, result: {}", result);
            return ResponseDo.buildFailureResponse("上传失败");
        }
    }


    @Override
    public ResponseDo uploadDrivingLicensePhoto(String userId, MultipartFile multiFile, String token) throws ApiException {
        LOG.debug("uploadDrivingLicensePhoto to server, userId:{}", userId);

        checker.checkUserInfo(userId, token);

        byte[] data = FileUtil.buildFileBytes(multiFile);
        String key = MessageFormat.format(Constants.PhotoKey.DRIVING_LICENSE_KEY, userId);

        return uploadLocalServer(userId, data, key);
    }

    private ResponseDo uploadLocalServer(String photoId, byte[] data, String key) throws ApiException {
        localFileManager.upload(data, key, true);

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("photoUrl", CommonUtil.getLocalPhotoServer() + key);
        dataMap.put("photoId", photoId);
        return ResponseDo.buildSuccessResponse(dataMap);
    }

    @Override
    public ResponseDo uploadCoverPhoto(MultipartFile multiFile, String userId, String token) throws ApiException {
        LOG.debug("uploadCoverPhoto to server begin");

        checker.checkUserInfo(userId, token);
        byte[] data = FileUtil.buildFileBytes(multiFile);

        String coverUuid = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.COVER_KEY, coverUuid);

        return uploadThirdServer(data, coverUuid, key, true);
    }

    @Override
    public ResponseDo uploadAlbumPhoto(String userId, MultipartFile multiFile, String token) throws ApiException {
        // 1.参数校验
        checker.checkUserInfo(userId, token);

        LOG.debug("check user album exist or not");
        // 2.查数据库信息
        long albumCount = photoDao.count(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM)));
        //3.检查是否达到上限
        if (albumCount >= PropertiesUtil.getProperty("carplay.photos.upper.limit", 40)) {
            LOG.warn("User:{} album count is over upper limit", albumCount);
            throw new ApiException("用户照片数量已经达到上限，不能继续上传");
        }

        LOG.debug("transfer photo file into byte array and upload photo to server");
        // 3.上传个人相册图片
        byte[] data = FileUtil.buildFileBytes(multiFile);
        String photoId = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.USER_ALBUM_KEY, userId, photoId);

        ResponseDo response = uploadThirdServer(data, photoId, key, true);
        if (response.success()) {
            Photo photo = new Photo();
            photo.setKey(key);
            photo.setUserId(userId);
            photo.setType(Constants.PhotoType.USER_ALBUM);
            photo.setUploadTime(DateUtil.getTime());
            photoDao.save(photo);

            Object responseData = response.getData();
            if (responseData instanceof Map) {
                ((Map) responseData).put("photoId", photo.getId());
            }
        }

        return response;
    }

    @Override
    public ResponseDo uploadFeedbackPhoto(MultipartFile multiFile) throws ApiException {
        byte[] data = FileUtil.buildFileBytes(multiFile);
        String photoId = CodeGenerator.generatorId();
        LOG.debug("begin upload feedback photo , photoId:{}", photoId);

        String key = MessageFormat.format(Constants.PhotoKey.FEEDBACK_KEY, photoId);
        return uploadThirdServer(data, photoId, key, true);
    }

    @Override
    public ResponseDo reUploadUserPhoto(String userId, MultipartFile multiFile, String token) throws ApiException {

        LOG.debug("reUploadUserPhoto to server, userId:{}", userId);

        // 参数校验
        checker.checkUserInfo(userId, token);

        // 重新上传图片
        byte[] data = FileUtil.buildFileBytes(multiFile);
        LOG.debug("reUploadUserPhoto upload , userId:{}", userId);

        User user = userDao.findById(userId);

        return uploadLocalServer(userId, data, user.getAvatar());
    }

    @Override
    public ResponseDo uploadDriverLicensePhoto(String userId, MultipartFile attach, String token) throws ApiException {
        LOG.debug("uploadDriverLicensePhoto to server, userId:{}", userId);

        checker.checkUserInfo(userId, token);

        byte[] data = FileUtil.buildFileBytes(attach);
        String key = MessageFormat.format(Constants.PhotoKey.DRIVER_LICENSE_KEY, userId);

        return uploadLocalServer(userId, data, key);
    }
}
