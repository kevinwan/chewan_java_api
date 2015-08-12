package com.gongpingjia.carplay.common.util;

import java.util.UUID;

import org.springframework.util.StringUtils;

/**
 * 生成随机验证码和主键信息
 * 
 * @author licheng
 *
 */
public class CodeGenerator {

	private static final String UUID_REGIX = "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$";

	/**
	 * 生成数据表的主键，采用UUID生成
	 * 
	 * @return 主键字符串
	 */
	public static String generatorId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 生成手机验证码
	 * 
	 * @return 返回四位验证码
	 */
	public static String generatorVerifyCode() {
		int code = (int) Math.floor(Math.random() * 10000);
		String codeStr = "0000" + code;
		return codeStr.substring(codeStr.length() - 4);
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
		return uuid.matches(UUID_REGIX);
	}
}
