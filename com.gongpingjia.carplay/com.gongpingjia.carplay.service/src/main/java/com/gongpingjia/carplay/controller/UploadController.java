package com.gongpingjia.carplay.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.UploadService;

@RestController
public class UploadController {

	private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private UploadService service;

	/**
	 * 2.4 头像上传
	 * 
	 * @return 上传结果
	 */
	@RequestMapping(value = "/avatar/upload", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public ResponseDo uploadUserPhoto(@RequestParam("attach") MultipartFile attach, HttpServletRequest request) {
		LOG.info("uploadAvatarPhoto attach size: {}", attach.getSize());

		try {
			return service.uploadUserPhoto(attach, request);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse("上传文件失败");
		}
	}

	/**
	 * 2.10 行驶证上传
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @return 返回结果对象
	 */
	@RequestMapping(value = "/user/{userId}/license/upload", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public ResponseDo uploadLicensePhoto(@PathVariable(value = "userId") String userId,
			@RequestParam("attach") MultipartFile attach, HttpServletRequest request) {
		LOG.info("uploadLicensePhoto attach size: {}", attach.getSize());

		try {
			return service.uploadLicensePhoto(userId, attach, request);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse("上传文件失败");
		}
	}

	/**
	 * 2.14 活动图片上传
	 * 
	 * @param attach
	 *            图片资源文件
	 * @param request
	 *            请求参数
	 * @return 返回上传结果
	 */
	@RequestMapping(value = "/activity/cover/upload", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public ResponseDo uploadCoverPhoto(@RequestParam("attach") MultipartFile attach, HttpServletRequest request) {
		LOG.info("uploadCoverPhoto attach size: {}", attach.getSize());
		try {
			return service.uploadCoverPhoto(attach, request);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

}
