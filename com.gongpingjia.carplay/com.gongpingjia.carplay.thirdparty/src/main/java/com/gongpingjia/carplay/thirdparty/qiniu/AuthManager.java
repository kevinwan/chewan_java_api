package com.gongpingjia.carplay.thirdparty.qiniu;

import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;

/**
 * 建议采用Spring IOC单例管理
 * 
 * @author 链接七牛服务器鉴权
 *
 */
public class AuthManager {

	/**
	 * 七牛服务器鉴权使用
	 */
	private Auth auth;

	private BucketManager bucketManager;

	private static AuthManager instance = new AuthManager();

	// private static Auth auth =
	// Auth.create("VDo2clWr4g7DJ2d1S8h_8W17d2RzmMdrywI-TiBm",
	// "H7Axjej_QhlpgbAry4rVNyoBOnNj9etSfWYcHXi7");

	private AuthManager() {
		this.auth = Auth.create(PropertiesUtil.getProperty("qiniu.server.access.key", ""),
				PropertiesUtil.getProperty("qiniu.server.secret.key", ""));
		this.bucketManager = new BucketManager(auth);
	}

	/**
	 * 获取鉴权管理的单例对象
	 * 
	 * @return
	 */
	public static AuthManager getInstance() {
		return instance;
	}

	/**
	 * 简单上传，使用默认策略
	 * 
	 * @return 
	 *         返回uptoken，uptoken是一个字符串，作为http协议Header的一部分（Authorization字段）发送到我们七牛的服务端
	 *         ，表示这个http请求是经过用户授权的。
	 */
	public String getUpToken() {
		return auth.uploadToken(getBucket());
	}

	/**
	 * 覆盖上传
	 * 
	 * @param key
	 *            需要被覆盖的文件的key值
	 * @return 
	 *         返回uptoken，uptoken是一个字符串，作为http协议Header的一部分（Authorization字段）发送到我们七牛的服务端
	 *         ，表示这个http请求是经过用户授权的。
	 */
	public String getOverrideUpToken(String key) {
		return auth.uploadToken(getBucket(), key);
	}

	/**
	 * 返回图片资源空间
	 * 
	 * @return 资源空间
	 */
	public String getBucket() {
		return PropertiesUtil.getProperty("qiniu.server.bucket", "");
	}

	/**
	 * 获取空间管理对象
	 * 
	 * @return 空间管理对象
	 */
	public BucketManager getBucketManager() {
		return bucketManager;
	}
}
