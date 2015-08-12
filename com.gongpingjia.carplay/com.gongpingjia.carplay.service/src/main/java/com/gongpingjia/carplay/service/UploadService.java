package com.gongpingjia.carplay.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface UploadService {

	/**
	 * 上传图片资源
	 * 
	 * @param multiFile
	 *            图片文件资源
	 * @param request
	 *            Request请求
	 * @return 返回结果对象
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo uploadAvatarPhoto(MultipartFile multiFile, HttpServletRequest request) throws ApiException;

	/**
	 * 上传图片资源
	 * 
	 * @param userId
	 *            用户ID
	 * @param multiFile
	 *            图片文件资源
	 * @param request
	 *            Request请求
	 * @return 返回结果对象
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo uploadLicensePhoto(String userId, MultipartFile multiFile, HttpServletRequest request)
			throws ApiException;
}
