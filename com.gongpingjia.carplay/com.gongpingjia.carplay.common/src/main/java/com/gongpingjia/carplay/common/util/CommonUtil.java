package com.gongpingjia.carplay.common.util;

import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.exception.ApiException;

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
			LOG.warn("phone number is empty");
			return false;
		}

		// 手机号必须为长度为11的数字字符串
		if (!PHONE_PATTERN.matcher(phone).matches()) {
			LOG.warn("phone number is not a number sequence which length is 11");
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
	 * 获取版本信息
	 * 
	 * @return 返回版本信息
	 */
	public static String getVersion() {
		return PropertiesUtil.getProperty("carplay.version", "v1");
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

	/**
	 * 检查JSON对象中对应的Key值是否为空，如果为空返回true， 否则返回false
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static boolean isEmpty(JSONObject json, String key) {
		if (json == null) {
			return true;
		}

		if (!json.containsKey(key)) {
			return true;
		}

		if (StringUtils.isEmpty(json.getString(key))) {
			return true;
		}
		return false;
	}

	/**
	 * 检查Key对应的参数为数组类型是否为空
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static boolean isArrayEmpty(JSONObject json, String key) {
		if (json == null) {
			return true;
		}

		if (!json.containsKey(key)) {
			return true;
		}
		try {
			if (json.getJSONArray(key).isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			LOG.warn("{} is not a JSONArray", key);
			return true;
		}
		return false;
	}

	/**
	 * 根据JSONArray返回String数组
	 */
	public static String[] jsonArrayToStrings(JSONArray jsonArray) {
		String[] strings;
		int length = jsonArray.size();
		if (length == 0) {
			return null;
		} else {
			strings = new String[length];
			for (int i = 0; i < length; i++) {
				strings[i] = jsonArray.getString(i);
			}
			return strings;
		}
	}

	/**
	 * 批量检查Key值为空的参数,如果存在一个为空的就返回true
	 * 
	 * @param json
	 * @param keys
	 * @return
	 */
	public static boolean isEmpty(JSONObject json, List<String> keys) {
		for (String key : keys) {
			if (isEmpty(json, key)) {
				LOG.warn("Parameter {} is empty", key);
				return true;
			}
		}

		return false;
	}
}
