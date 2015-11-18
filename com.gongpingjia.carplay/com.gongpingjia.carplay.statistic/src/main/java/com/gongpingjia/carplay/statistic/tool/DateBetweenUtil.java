package com.gongpingjia.carplay.statistic.tool;

import com.gongpingjia.carplay.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public class DateBetweenUtil {

    private static Logger logger = LoggerFactory.getLogger(DateBetweenUtil.class);


    public static List<String> getBetweenList(String startStr, String endStr, int type) {
        if (type == 0) {
            return getDateBetweenList(startStr, endStr);
        } else if (type == 1) {
            return getWeekBetweenStrList(startStr, endStr);
        } else if (type == 2) {
            return getMonthBetweenStrList(startStr, endStr);
        } else {
            return null;
        }
    }


    public static List<String> getDateBetweenList(String startStr, String endStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> betweenList = null;
        try {
            Date startDate = simpleDateFormat.parse(startStr);
            Date endDate = simpleDateFormat.parse(endStr);
            int dayNum = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
            betweenList = new ArrayList<>(dayNum);
            for (int index = 0; index < dayNum; index++) {
                betweenList.add(simpleDateFormat.format(DateUtil.addTime(startDate, Calendar.DATE, index)));
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return betweenList;
    }


    public static List<String> getMonthBetweenStrList(String startStr, String endStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        ArrayList<String> betweenList = null;
        try {
            Date startDate = simpleDateFormat.parse(startStr);
            Date endDate = simpleDateFormat.parse(endStr);
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);

            //两个时间 相距的年份
            int yearGap = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR) - 1;
            int size = endCal.get(Calendar.MONTH) + yearGap * 12 + (12 - startCal.get(Calendar.MONTH) + 1);
            betweenList = new ArrayList<>(size);
            for (int index = 0; index < size; index++) {
                betweenList.add(generateMonthStr(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), index));
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return betweenList;
    }


    private static String generateMonthStr(int year, int month, int addMonthNum) {
        StringBuilder builder = new StringBuilder();
        int targetYear = year + (addMonthNum + month - 1) / 12;
        int targetMonth = (addMonthNum + month) % 12 + 1;
        builder.append(targetYear).append("-").append(targetMonth < 10 ? "0" + targetMonth : targetMonth);
        return builder.toString();
    }


    /**
     * 获取两个日期时间之内的 week
     *
     * @param startStr
     * @param endStr
     * @return
     */
    public static List<String> getWeekBetweenStrList(String startStr, String endStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> betweenList = null;
        try {
            Long startTime = simpleDateFormat.parse(startStr).getTime();
            Long endTime = simpleDateFormat.parse(endStr).getTime();
            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(startTime);
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(endTime);

            int startYear = startCal.get(Calendar.YEAR);
            int endYear = endCal.get(Calendar.YEAR);
            Map<String, Integer> yearWeekCountMap = getYearWeekCountMap(startYear, endYear);
            int count = 0;
            if (startYear == endYear) {
                count = endCal.get(Calendar.WEEK_OF_YEAR) - startCal.get(Calendar.WEEK_OF_YEAR) + 1;
            } else {
                Integer totalCount = yearWeekCountMap.get("totalCount");
                count = totalCount - startCal.get(Calendar.WEEK_OF_YEAR) - (endCal.getWeeksInWeekYear() - endCal.get(Calendar.WEEK_OF_YEAR));
            }
            betweenList = new ArrayList<>(count);
            for (int index = 0; index < count; index++) {
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(DateUtil.addTime(new Date(startTime), Calendar.DATE, index * 7));
                betweenList.add(tempCal.get(Calendar.YEAR) + "-" + tempCal.get(Calendar.WEEK_OF_YEAR));
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return betweenList;
    }

    private static Map<String, Integer> getYearWeekCountMap(int startYear, int endYear) {
        Map<String, Integer> countMap = new HashMap<>(endYear - startYear + 2);
        int totalCount = 0;
        for (int index = startYear; index <= endYear; index++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, index);
            countMap.put(String.valueOf(index), calendar.getWeeksInWeekYear());
            totalCount += calendar.getWeeksInWeekYear();
        }
        countMap.put("totalCount", totalCount);
        return countMap;
    }

}
