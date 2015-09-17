package com.gongpingjia.carplay.common.photo;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by licheng on 2015/9/16.
 */
@Service
public class LocalFileManager {

	private static final Logger LOG = LoggerFactory.getLogger(LocalFileManager.class);

	/**
	 * 保存文件，保存文件之前先删除已经存在的文件
	 *
	 * @param data
	 *            文件的byte数组
	 * @param key
	 *            文件存放的key值
	 * @throws ApiException
	 *             文件保存过程抛异常，转换成业务异常业务异常
	 */
	public void saveFile(byte[] data, String key) throws ApiException {

		LOG.debug("Begin save file");
		String filePath = PropertiesUtil.getProperty("photo.static.path", "photos/") + key;
		FileOutputStream outputStream = null;

		try {
			String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));

			makeDirectory(dirPath);

			deleteFile(key);

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
					outputStream.close();
				} catch (IOException e) {
					LOG.warn("Close outputStream failure");
				}
			}
		}
	}

	/**
	 * 删除指定文件路径的文件
	 *
	 * @param key
	 *            文件路径Key值
	 */
	public void deleteFile(String key) {
		String filePath = PropertiesUtil.getProperty("photo.static.path", "photos/") + key;
		File file = new File(filePath);
		if (file.exists()) {
			LOG.debug("Delete file: {}", filePath);
			file.delete();
		}
	}

	/**
	 * 检查本地文件是否存在
	 * 
	 * @param key
	 *            文件路径Key值
	 * @return 文件存在返回true， 否则返回false
	 */
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

}
