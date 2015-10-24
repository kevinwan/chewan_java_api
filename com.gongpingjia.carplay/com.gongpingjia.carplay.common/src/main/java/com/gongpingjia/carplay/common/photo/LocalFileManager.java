package com.gongpingjia.carplay.common.photo;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by licheng on 2015/9/16.
 */
@Service
@Qualifier("localFileManager")
public class LocalFileManager implements PhotoService {

	private static final Logger LOG = LoggerFactory.getLogger(LocalFileManager.class);

	/**
	 * 检查本地文件是否存在
	 * 
	 * @param key
	 *            文件路径Key值
	 * @return 文件存在返回true， 否则返回false
	 */
	@Override
	public boolean isExist(String key) {
		String filePath = PropertiesUtil.getProperty("photo.static.path", "photos/") + key;
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 *            路径
	 */
	private void makeDirectory(String path) {
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			boolean mkdirs = pathFile.mkdirs();
			if (!mkdirs) {
				LOG.warn("Make directories failure, dir:{}", path);
			}
		}
	}

	@Override
	public Map<String, String> upload(byte[] data, String key, boolean override) throws ApiException {
		LOG.debug("Begin save file");

		Map<String, String> result = new HashMap<String, String>(2, 1);

		String filePath = PropertiesUtil.getProperty("photo.static.path", "photos/") + key;
		FileOutputStream outputStream = null;

		try {
			String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));

			makeDirectory(dirPath);

			delete(key);

			outputStream = new FileOutputStream(filePath);
			outputStream.write(data);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			throw new ApiException("保存文件失败", e);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ApiException("保存文件失败", e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					LOG.warn("Close outputStream failure");
				}
			}
		}

		result.put("result", Constants.Result.SUCCESS);
		result.put("key", key);
		return result;
	}

	@Override
	public void delete(String key) throws ApiException {
		String filePath = PropertiesUtil.getProperty("photo.static.path", "photos/") + key;
		File file = new File(filePath);
		if (file.exists()) {
			LOG.debug("Delete file: {}", filePath);
			boolean result = file.delete();
			if (!result) {
				LOG.warn("Delete file:{} failure", key);
			}
		}
	}

	@Override
	public void move(String key, String targetKey) throws ApiException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String oldname, String newname) throws ApiException {
		// TODO Auto-generated method stub

	}

}
