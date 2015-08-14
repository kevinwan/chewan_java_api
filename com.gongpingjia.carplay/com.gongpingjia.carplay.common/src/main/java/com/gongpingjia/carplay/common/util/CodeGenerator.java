package com.gongpingjia.carplay.common.util;

import java.util.UUID;

/**
 * 生成随机验证码和主键信息
 * 
 * @author licheng
 *
 */
public class CodeGenerator {

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

}
