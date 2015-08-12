package com.gongpingjia.carplay.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface UploadService {

	ResponseDo uploadFile(MultipartFile multiFile, HttpServletRequest request) throws ApiException;

}
