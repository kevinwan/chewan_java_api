package com.gongpingjia.carplay.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ToolsUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ToolsUtils.class);

	/**
	 * 验证是否是手机号
	 * 
	 * @param phone 手机号
	 * @return 验证结果
	 */
	public static Boolean isPhoneNumber(String phone) {
		LOG.debug("isPhoneNumber phone :" + phone);
		if (StringUtils.isEmpty(phone)) {
			LOG.error("phone number is empty");
			return false;
		}

		// 手机号必须为长度为11的数字字符串
		if (!phone.matches("^[0-9]{11}$")) {
			LOG.error("phone number is not a number sequence which length is 11");
			return false;
		}

		return true;
	}
	
	/**
	 * 验证是否是图片ID
	 * @param uuid 图片ID
	 * @return 验证结果
	 */
	public static Boolean isUuid(String uuid) {
		LOG.debug("isUuid uuid :" + uuid);
		String regExp = "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$";  
		Pattern p = Pattern.compile(regExp);  

		Matcher m = p.matcher(uuid);  
		return m.find();//boolean
	}

}
