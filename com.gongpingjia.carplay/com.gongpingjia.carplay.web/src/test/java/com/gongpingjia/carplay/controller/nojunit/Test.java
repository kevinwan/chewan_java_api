package com.gongpingjia.carplay.controller.nojunit;

import com.gongpingjia.carplay.statistic.tool.DateBetweenUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/17.
 */
public class Test {


    public static void main(String args[]) {
//        String starTime = "2015-12-20";
//        String endTime = "2016-01-15";
//
//        List<String> monthBetweenStrList = DateBetweenUtil.getMonthBetweenStrList(starTime, endTime);
//        List<String> weekBetweenStrList = DateBetweenUtil.getWeekBetweenStrList(starTime, endTime);
//        System.out.println(monthBetweenStrList);
//        System.out.println(weekBetweenStrList);
        Date date = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(date);
        System.out.println(nowCalendar.get(Calendar.WEEK_OF_YEAR));

    }
}
