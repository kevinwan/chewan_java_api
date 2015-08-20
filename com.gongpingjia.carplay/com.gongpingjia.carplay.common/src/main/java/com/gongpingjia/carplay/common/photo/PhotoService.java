package com.gongpingjia.carplay.common.photo;

import java.util.Map;

import com.gongpingjia.carplay.common.exception.ApiException;

/**
 * 上传图片资源文件
 * 
 * @author licheng
 *
 */
public interface PhotoService {

	/**
	 * 上传图片资源文件
	 * 
	 * @param data
	 *            图片数据
	 * @param key
	 *            图片路径Key值
	 * @param override
	 *            是否覆盖
	 * @return 返回上传结果信息
	 * @throws ApiException
	 */
	Map<String, String> upload(byte[] data, String key, boolean override) throws ApiException;

	/**
	 * 删除服务器上图片资源文件
	 * 
	 * @param key
	 *            图片资源文件Key值
	 */
	void delete(String key) throws ApiException;

	/**
	 * 检测文件是否存在于服务器上
	 * 
	 * @param key
	 *            图片资源文件Key值
	 * @return 存在返回true，不存在返回false
	 */
	boolean isExist(String key);

	/**
	 * 移动文件
	 * 
	 * @param key
	 *            源图片资源文件Key值
	 * @param targetKey
	 *            目标图片资源文件Key值
	 */
	void move(String key, String targetKey) throws ApiException;

	/**
	 * 重命名图片文件
	 * 
	 * @param oldname
	 *            旧名称
	 * @param newname
	 *            新名称
	 */
	void rename(String oldname, String newname) throws ApiException;

}
