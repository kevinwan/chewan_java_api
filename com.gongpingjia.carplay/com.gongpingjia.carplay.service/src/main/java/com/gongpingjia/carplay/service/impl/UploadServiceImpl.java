package com.gongpingjia.carplay.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gongpingjia.carplay.common.avatar.AvatarService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService {

	private static final Logger LOG = LoggerFactory.getLogger(UploadServiceImpl.class);

	private static final String KEY_FORMAT = "asset/user/{0}/avatar.jpg";

	@Autowired
	private AvatarService avatarService;

	@Override
	public ResponseDo uploadFile(MultipartFile multiFile, HttpServletRequest request) throws ApiException {
		LOG.debug("Begin upload file to server");

		byte[] data = buildFileBytes(multiFile);

		String id = CodeGenerator.generatorId();
		String key = MessageFormat.format(KEY_FORMAT, id);

		Map<String, String> result = avatarService.upload(data, key, false);
		LOG.debug("Upload result: " + result);
		if ("success".equalsIgnoreCase(result.get("result"))) {
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("photoUrl", PropertiesUtil.getProperty("qiniu.server.url", "") + result.get("key"));
			dataMap.put("photoId", id);
			return ResponseDo.buildSuccessResponse(dataMap);
		} else {
			LOG.error("Upload avatar resource failure, result: " + result);
			return ResponseDo.buildFailureResponse("未能成功上传图片");
		}
	}

	/**
	 * 获取上传文件的byte数组，准备向图片资源服务器上传
	 * 
	 * @param multiFile
	 *            HTTP上传的数据文件
	 * @return 返回文件byte数组
	 * @throws ApiException
	 *             文件读写异常
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
			LOG.error(e.getMessage(), e);
			throw new ApiException("上传文件失败");
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					LOG.error("Close BufferedInputStream bis failure at finally");
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.error("Close ByteArrayOutputStream out failure at finally");
				}
			}
		}
		return fileContent;
	}
}
