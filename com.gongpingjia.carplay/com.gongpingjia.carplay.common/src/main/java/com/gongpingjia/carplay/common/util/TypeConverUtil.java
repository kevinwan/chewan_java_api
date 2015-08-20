package com.gongpingjia.carplay.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gongpingjia.carplay.common.exception.ApiException;

/**
 * 类型转换公共类
 * 
 * @author licheng
 *
 */
public class TypeConverUtil {

	private static final Logger LOG = LoggerFactory.getLogger(TypeConverUtil.class);

	/**
	 * 讲字符串变量转换成 Double
	 * 
	 * @param paramName
	 *            参数名称
	 * @param param
	 *            参数字符串
	 * @param throwException
	 *            是否抛异常，如果为true就抛异常，为false不抛异常
	 * @return 返回Double类型值
	 */
	public static Double convertToDouble(String paramName, String param, boolean throwException) throws ApiException {
		try {
			return Double.valueOf(param);
		} catch (NumberFormatException e) {
			LOG.warn("Parameter {} is not Double type, which value is {}", paramName, param);
			if (throwException) {
				LOG.warn(e.getMessage(), e);
				throw new ApiException("参数错误");
			}
		}
		return 0D;
	}

	/**
	 * 讲字符串变量转换成 Long
	 * 
	 * @param paramName
	 *            参数名称
	 * @param param
	 *            参数字符串
	 * @param throwException
	 *            是否抛异常，如果为true就抛异常，为false不抛异常
	 * @return 返回Double类型值
	 */
	public static Long convertToLong(String paramName, String param, boolean throwException) throws ApiException {
		try {
			return Long.valueOf(param);
		} catch (NumberFormatException e) {
			LOG.warn("Parameter {} is not Long type, which value is {}", paramName, param);
			if (throwException) {
				LOG.warn(e.getMessage(), e);
				throw new ApiException("参数错误");
			}
		}
		return 0L;
	}

	/**
	 * 讲字符串变量转换成 Integer
	 * 
	 * @param paramName
	 *            参数名称
	 * @param param
	 *            参数字符串
	 * @param throwException
	 *            是否抛异常，如果为true就抛异常，为false不抛异常
	 * @return 返回Double类型值
	 */
	public static Integer convertToInteger(String paramName, String param, boolean throwException) throws ApiException {
		try {
			return Integer.valueOf(param);
		} catch (NumberFormatException e) {
			LOG.warn("Parameter {} is not Integer type, which value is {}", paramName, param);
			if (throwException) {
				LOG.warn(e.getMessage(), e);
				throw new ApiException("参数错误");
			}
		}
		return 0;
	}
}
