package com.gongpingjia.carplay.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface UploadService {

	/**
	 * 上传用户图像资源
	 * 
	 * @param multiFile
	 *            图片文件资源
	 * @param request
	 *            Request请求
	 * @return 返回结果对象
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo uploadUserPhoto(MultipartFile multiFile, HttpServletRequest request) throws ApiException;

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

	/**
	 * 上传活动图片资源文件
	 * 
	 * @param multiFile
	 *            图片资源文件
	 * @param request
	 *            请求参数
	 * @return 返回上传结果
	 * @throws ApiException
	 *             业务异常信息
	 */
	ResponseDo uploadCoverPhoto(MultipartFile multiFile, HttpServletRequest request) throws ApiException;

	/**
	 * 相册图片上传
	 * 
	 * @param userId
	 *            用户ID
	 * @param multiFile
	 *            上传的图片资源文件
	 * @param request
	 *            请求参数
	 * @return 返回上传结果信息
	 * @throws ApiException
	 *             业务异常信息
	 */
	ResponseDo uploadAlbumPhoto(String userId, MultipartFile multiFile, HttpServletRequest request) throws ApiException;

	/**
	 * 上传反馈图片资源文件
	 * 
	 * @param multiFile
	 *            反馈图片资源文件
	 * @param request
	 *            请求参数信息
	 * @return 返回上传结果
	 * @throws ApiException
	 *             业务异常信息
	 */
	ResponseDo uploadFeedbackPhoto(MultipartFile multiFile, HttpServletRequest request) throws ApiException;

	/**
	 * 更改头像
	 * 
	 * @param multiFile
	 *            反馈图片资源文件
	 * @param request
	 *            请求参数信息
	 * @return 返回上传结果
	 * @throws ApiException
	 *             业务异常信息
	 */
	ResponseDo reUploadUserPhoto(String userId, MultipartFile multiFile, HttpServletRequest request)
			throws ApiException;

}
