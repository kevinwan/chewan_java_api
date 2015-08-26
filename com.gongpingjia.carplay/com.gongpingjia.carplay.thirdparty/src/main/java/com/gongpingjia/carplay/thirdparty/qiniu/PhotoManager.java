package com.gongpingjia.carplay.thirdparty.qiniu;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.Constants;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;

@Service
public class PhotoManager implements PhotoService {

	private static final Logger LOG = LoggerFactory.getLogger(PhotoManager.class);

	/**
	 * 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
	 */
	private UploadManager uploadManager = new UploadManager();

	/**
	 * 上传内存中数据
	 * 
	 * @param data
	 *            图片资源数据
	 * @param key
	 *            图片资源的唯一Key
	 * @param 是否覆盖已有的文件
	 * @param upToken
	 *            uptoken是一个字符串，作为http协议Header的一部分（Authorization字段）发送到我们七牛的服务端，
	 *            表示这个http请求是经过用户授权的。
	 * @throws ApiException
	 *             接口异常
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> upload(byte[] data, String key, boolean override) throws ApiException {
		Map<String, String> result = new HashMap<String, String>(0);

		try {
			String upToken = "";
			if (override) {
				upToken = AuthManager.getInstance().getOverrideUpToken(key);
			} else {
				upToken = AuthManager.getInstance().getUpToken();
			}

			Response response = uploadManager.put(data, key, upToken);
			LOG.info(response.bodyString());
			result = response.jsonToObject(HashMap.class);
			if (response.isOK()) {
				result.put("result", Constants.Result.SUCCESS);
			} else {
				result.put("result", Constants.Result.FAILURE);
			}
		} catch (QiniuException e) {
			Response res = e.response;
			// 请求失败时简单状态信息
			LOG.warn(res.toString());
			LOG.warn(e.getMessage(), e);
			try {
				// 响应的文本信息
				LOG.warn(res.bodyString());
			} catch (QiniuException e1) {
				LOG.warn(e.getMessage(), e);
			}
			throw new ApiException("未能成功上传至服务器");
		}

		return result;
	}

	@Override
	public void delete(String key) throws ApiException {
		AuthManager instance = AuthManager.getInstance();
		try {
			instance.getBucketManager().delete(instance.getBucket(), key);
		} catch (QiniuException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("删除文件失败");
		}
	}

	@Override
	public boolean isExist(String key) {
		AuthManager instance = AuthManager.getInstance();
		FileInfo fileInfo = null;
		try {
			fileInfo = instance.getBucketManager().stat(instance.getBucket(), key);
		} catch (QiniuException e) {
			LOG.warn(e.getMessage(), e);
			// throw new ApiException("查找文件失败");
		}
		return fileInfo != null;
	}

	@Override
	public void move(String key, String targetKey) throws ApiException {
		AuthManager instance = AuthManager.getInstance();
		try {
			instance.getBucketManager().move(instance.getBucket(), key, instance.getBucket(), targetKey);
		} catch (QiniuException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("移动文件失败");
		}
	}

	@Override
	public void rename(String oldname, String newname) throws ApiException {
		AuthManager instance = AuthManager.getInstance();
		try {
			instance.getBucketManager().rename(instance.getBucket(), oldname, newname);
		} catch (QiniuException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("移动重命名失败");
		}
	}

}
