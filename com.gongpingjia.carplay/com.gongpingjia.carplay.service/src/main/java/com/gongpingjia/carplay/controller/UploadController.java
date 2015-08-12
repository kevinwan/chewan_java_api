package com.gongpingjia.carplay.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public ResponseDo uploadAvatarPhoto(MultipartFile file, HttpServletRequest request) {
		LOG.info("Upload file size: " + file.getSize());

		try {
			return service.uploadFile(file, request);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse("上传文件失败");
		}
	}
}
