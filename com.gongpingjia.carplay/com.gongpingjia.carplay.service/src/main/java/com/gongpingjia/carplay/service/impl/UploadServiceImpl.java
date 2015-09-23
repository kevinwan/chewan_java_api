package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.user.AlbumDao;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.user.Album;
import com.gongpingjia.carplay.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
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

    /**
     * Token校验
     */
    @Autowired
    private UserTokenDao tokenDao;

    /**
     * 相册Dao
     */
    @Autowired
    private AlbumDao albumDao;

    /**
     * 对存放在本地文件的操
     */
    @Autowired
    @Qualifier("localFileManager")
    private PhotoService localFileManager;

    @Override
    public ResponseDo uploadAvatarPhoto(MultipartFile multiFile) throws ApiException {
        LOG.debug("Begin upload file to server");

        byte[] data = buildFileBytes(multiFile);

        String id = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, id);

        return uploadLocalServer(id, data, key);
    }

    @Override
    public ResponseDo uploadPersonalPhoto(MultipartFile multipartFile, String userId, String token) throws ApiException {
        LOG.debug("Begin upload file to server");

        checker.checkUserInfo(userId, token);

        byte[] data = buildFileBytes(multipartFile);

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
            dataMap.put("photoId", photoId);
            return ResponseDo.buildSuccessResponse(dataMap);
        } else {
            LOG.error("Upload avatar resource failure, result: {}", result);
            return ResponseDo.buildFailureResponse("上传失败");
        }
    }

    /**
     * 获取上传文件的byte数组，准备向图片资源服务器上�?
     *
     * @param multiFile HTTP上传的数据文�?
     * @return 返回文件byte数组
     * @throws ApiException 文件读写异常
     */
    private byte[] buildFileBytes(MultipartFile multiFile) throws ApiException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream out = null;
        byte[] fileContent = null;

        try {
            bis = new BufferedInputStream(multiFile.getInputStream());
            out = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = bis.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }

            fileContent = out.toByteArray();

        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            throw new ApiException("上传文件失败");
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    LOG.warn("Close BufferedInputStream bis failure at finally");
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("Close ByteArrayOutputStream out failure at finally");
                }
            }
        }
        return fileContent;
    }

    @Override
    public ResponseDo uploadDrivingLicensePhoto(String userId, MultipartFile multiFile, String token) throws ApiException {
        LOG.debug("uploadDrivingLicensePhoto to server, userId:{}", userId);

        checker.checkUserInfo(userId, token);

        byte[] data = buildFileBytes(multiFile);
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
        byte[] data = buildFileBytes(multiFile);

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
        List<Album> userAlbumList = albumDao.find(Query.query(Criteria.where("userId").is(userId)));
        if (userAlbumList.isEmpty()) {
            LOG.warn("get userAlbum by userId return empty result");
            throw new ApiException("获取相册失败");
        }

        Album album = userAlbumList.get(0);

        LOG.debug("check photos in album is over max count or not");
        int photosCount = album.getPhotos().size();
        int maxCount = PropertiesUtil.getProperty("user.album.photo.max.count", 9);
        if (photosCount >= maxCount) {
            LOG.error("Photos in album size is {}, nearly over max count{}", photosCount, maxCount);
            throw new ApiException(MessageFormat.format("相册图片数不能超过{0}张", maxCount));
        }

        LOG.debug("transfer photo file into byte array and upload photo to server");
        // 3.上传个人相册图片
        byte[] data = buildFileBytes(multiFile);
        String photoId = CodeGenerator.generatorId();
        String key = MessageFormat.format(Constants.PhotoKey.USER_ALBUM_KEY, userId, photoId);
        return uploadThirdServer(data, photoId, key, true);
    }

    @Override
    public ResponseDo uploadFeedbackPhoto(MultipartFile multiFile) throws ApiException {
        byte[] data = buildFileBytes(multiFile);
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
        byte[] data = buildFileBytes(multiFile);
        LOG.debug("reUploadUserPhoto upload , userId:{}", userId);

        String key = MessageFormat.format(Constants.PhotoKey.AVATAR_KEY, userId);
        return uploadThirdServer(data, userId, key, true);
    }

    @Override
    public ResponseDo uploadDriverLicensePhoto(String userId, MultipartFile attach, String token) throws ApiException {
        LOG.debug("uploadDriverLicensePhoto to server, userId:{}", userId);

        checker.checkUserInfo(userId, token);

        byte[] data = buildFileBytes(attach);
        String key = MessageFormat.format(Constants.PhotoKey.DRIVER_LICENSE_KEY, userId);

        return uploadLocalServer(userId, data, key);
    }
}
