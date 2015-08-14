package com.gongpingjia.carplay.common.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * 获取当前时间
	 * 
	 * @return 返回当前时间
	 */
	public static Date getDate() {
		return new Date();
	}

	/**
	 * 获取当前时间
	 * @param 当前毫秒数
	 * @return 返回当前时间
	 */
	public static Date getDate(Long s) {
		return new Date(s);
	}
	
	/**
	 * 获取当前时间毫秒数
	 * 
	 * @return 返回当前时间毫秒数
	 */
	public static Long getTime() {
		return getDate().getTime();
	}

	/**
	 * 在日期Date上进行加、减算法操作
	 * 
	 * @param date
	 *            被加减的日期对象
	 * @param type
	 *            对时间的年、月、日、时、分、秒操作，如Calendar.DAY_OF_MONTH
	 * @param param
	 *            需要加、或者减的时间数
	 * @return 返回计算后的时间的毫秒数
	 */
	public static Long addTime(Date date, int type, int param) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(type, param);
		return calendar.getTimeInMillis();
	}
}
