package com.gongpingjia.carplay.common.util;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 公共类公共方法
 * 
 * @author licheng
 *
 */
public class CommonUtil {

	private static final Logger LOG = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 * 手机号正则匹配模式
	 */
	private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{11}$");

	/**
	 * UUID的正则表达式匹配
	 */
	private static final Pattern UUID_PATTERN = Pattern.compile(
			"^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);

	/**
	 * 检查电话号码是否正确
	 * 
	 * @param phone
	 *            电话号码
	 * @return 如果是电话号码返回true， 否则返回false
	 */
	public static boolean isPhoneNumber(String phone) {
		LOG.debug("Check phone number: {}", phone);
		if (StringUtils.isEmpty(phone)) {
			LOG.error("phone number is empty");
			return false;
		}

		// 手机号必须为长度为11的数字字符串
		if (!PHONE_PATTERN.matcher(phone).matches()) {
			LOG.error("phone number is not a number sequence which length is 11");
			return false;
		}

		return true;
	}

	/**
	 * 检查字符串是否为UUID生成字符串
	 * 
	 * @param uuid
	 *            UUID字符串
	 * @return 是UUID字符串返回true，否则返回false
	 */
	public static boolean isUUID(String uuid) {
		if (StringUtils.isEmpty(uuid)) {
			return false;
		}
		return UUID_PATTERN.matcher(uuid).matches();
	}

	/**
	 * 获取图片资源服务器
	 * 
	 * @return 图片资源服务器URL
	 */
	public static String getPhotoServer() {
		return PropertiesUtil.getProperty("qiniu.server.url", "http://7xknzo.com1.z0.glb.clouddn.com/");
	}

	/**
	 * 获取公平价图标前缀
	 * 
	 * @return 返回gpjImagePrefix
	 */
	public static String getGPJImagePrefix() {
		return PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "http://img.gongpingjia.com/img/logo/");
	}

	/**
	 * 获取查找七牛服务器的后缀
	 * 
	 * @return 返回后缀配置信息
	 */
	public static String getActivityPhotoPostfix() {
		return PropertiesUtil.getProperty("activity.photo.postfix", "?imageView2/1/w/200");
	}

	/**
	 * 获取当前ObjectValue，如果为Null的话，取默认值DefaultValue
	 * 
	 * @param objectValue
	 *            目标值
	 * @param defaultValue
	 *            默认值
	 * @return 获取当前ObjectValue，如果为Null的话，取默认值DefaultValue
	 */
	public static Object ifNull(Object objectValue, Object defaultValue) {
		if (objectValue == null) {
			return defaultValue;
		}
		return objectValue;
	}
}
