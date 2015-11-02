package com.gongpingjia.carplay.common.util;

import com.gongpingjia.carplay.common.exception.ApiException;

import java.text.SimpleDateFormat;
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
     *
     * @param 当前毫秒数
     * @return 返回当前时间
     */
    public static Date getDate(Long param) {
        return new Date(param);
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
     * 根据formatString的格式字符串输出日期格式
     *
     * @param dateTime     时间参数
     * @param formatString 格式字符串
     * @return 返回格式化之后的字符串
     */
    public static String format(Long dateTime, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        Date date = getDate(dateTime);
        return format.format(date);
    }

    /**
     * 在日期Date上进行加、减算法操作
     *
     * @param date  被加减的日期对象
     * @param type  对时间的年、月、日、时、分、秒操作，如Calendar.DAY_OF_MONTH
     * @param param 需要加、或者减的时间数
     * @return 返回计算后的时间的毫秒数
     */
    public static Long addTime(Date date, int type, int param) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, param);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取date的对应的type类型的值
     *
     * @param date 日期对象
     * @param type 类型，对时间的年、月、日、时、分、秒操作，如Calendar.HOUR
     * @return 返回取值
     */
    public static int getValue(Date date, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(type);
    }

    /**
     * 根据当前时间为过期时间，计算开始时间
     *
     * @return
     */
    public static Long getExpiredLimitTime() {
        int expiredDays = Integer.valueOf(PropertiesUtil.getProperty("carplay.max.expired.days", 7));
        return DateUtil.getTime() - expiredDays * Constants.DAY_MILLISECONDS;
    }

    /**
     * 获取当前日期的零点
     *
     * @param date
     * @return
     */
    public static Long getZeroTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前日期的下一个零点
     *
     * @param date
     * @return
     */
    public static Long getNextZeroTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    /**
     * 将YYYY-MM-DD日期字符串转换成时间
     *
     * @param formatString YYYY-MM-DD日期字符串
     * @return 时间
     */
    public static Long getTimeByDateFormatString(String formatString) throws ApiException {
        String[] date = formatString.split("-");
        if (date.length != 3) {
            return 0L;
        }

        int year = TypeConverUtil.convertToInteger("YYYY", date[0], true);
        int month = TypeConverUtil.convertToInteger("MM", date[1], true);
        int day = TypeConverUtil.convertToInteger("DD", date[2], true);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
