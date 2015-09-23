package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.UploadService;

/**
 * 图片资源文件上传
 *
 * @author licheng
 */
@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private UploadService service;

    /**
     * 头像上传(上传到本地服务器)
     *
     * @return 上传结果
     */
    @RequestMapping(value = "/avatar/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadAvatarPhoto(@RequestBody MultipartFile attach) {
        LOG.info("uploadAvatarPhoto attach size: {}", attach.getSize());

        try {
            return service.uploadAvatarPhoto(attach);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse("上传文件失败");
        }
    }

    /**
     * 上传个人图像，用于个人图像认证
     *
     * @param attach 图像附件
     * @return 返回上传结果
     */
    @RequestMapping(value = "/user/{userId}/photo/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadPersonalPhoto(@RequestBody MultipartFile attach, @PathVariable("userId") String userId, @RequestParam("token") String token) {
        LOG.info("upload user personal photo, attach size:{}", attach.getSize());
        try {
            return service.uploadPersonalPhoto(attach, userId, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 行驶证上传
     *
     * @param userId 用户ID
     * @param token  用户会话Token
     * @return 返回结果对象
     */
    @RequestMapping(value = "/user/{userId}/drivingLicense/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadDrivingLicensePhoto(@PathVariable(value = "userId") String userId,
                                                @RequestParam("token") String token, @RequestBody MultipartFile attach) {
        LOG.info("uploadDrivingLicensePhoto attach size: {}", attach.getSize());

        try {
            return service.uploadDrivingLicensePhoto(userId, attach, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse("上传文件失败");
        }
    }

    /**
     * 行驶证上传
     *
     * @param userId 用户ID
     * @param token  用户会话Token
     * @return 返回结果对象
     */
    @RequestMapping(value = "/user/{userId}/driverLicense/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadDriverLicensePhoto(@PathVariable(value = "userId") String userId,
                                               @RequestParam("token") String token, @RequestBody MultipartFile attach) {
        LOG.info("uploadDrivingLicensePhoto attach size: {}", attach.getSize());

        try {
            return service.uploadDriverLicensePhoto(userId, attach, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse("上传文件失败");
        }
    }


    /**
     * 活动图片上传
     *
     * @param attach  图片资源文件
     * @param request 请求参数
     * @return 返回上传结果
     */
    @RequestMapping(value = "/activity/cover/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadCoverPhoto(@RequestBody MultipartFile attach, @RequestParam("token") String token,
                                       @RequestParam("userId") String userId) {
        LOG.info("uploadCoverPhoto attach size: {}", attach.getSize());
        try {
            return service.uploadCoverPhoto(attach, userId, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 相册图片上传
     *
     * @param userId  用户ID
     * @param attach  上传的附件
     * @param request 请求参数
     * @return 返回响应结果信息
     */
    @RequestMapping(value = "/user/{userId}/album/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadAlbumPhoto(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                       @RequestBody MultipartFile attach) {
        LOG.info("uploadAlbumPhoto attach size: {}, userId: {}", attach.getSize(), userId);

        try {
            return service.uploadAlbumPhoto(userId, attach, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 上传意见反馈图片
     *
     * @param attach  图片资源文件
     * @param request 请求信息
     * @return 返回上传结果信息
     */
    @RequestMapping(value = "/feedback/upload", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo uploadFeedbackPhoto(@RequestBody MultipartFile attach) {
        LOG.info("uploadFeedbackPhoto attach size: {}", attach.getSize());
        try {
            return service.uploadFeedbackPhoto(attach);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 更改头像
     *
     * @param userId 用户ID
     * @param token  用户会话Token
     * @return 返回结果对象
     */
    @RequestMapping(value = "/user/{userId}/avatar", method = RequestMethod.POST, headers = {"Content-Type=multipart/form-data"})
    public ResponseDo alterAvatar(@PathVariable(value = "userId") String userId, @RequestParam("token") String token,
                                  @RequestBody MultipartFile attach) {
        LOG.info("reUploadUserPhoto attach size: {}", attach.getSize());

        try {
            return service.reUploadUserPhoto(userId, attach, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse("上传文件失败");
        }
    }


}
