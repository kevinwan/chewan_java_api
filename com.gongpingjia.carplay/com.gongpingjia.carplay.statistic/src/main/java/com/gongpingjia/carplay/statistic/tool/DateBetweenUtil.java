package com.gongpingjia.carplay.statistic.tool;

import com.gongpingjia.carplay.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public class DateBetweenUtil {

    private static Logger logger = LoggerFactory.getLogger(DateBetweenUtil.class);

    public static List<String> getBetweenStrList(String startStr,String endStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> betweenList = null;
        try {
            Date startDate = simpleDateFormat.parse(startStr);
            Date endDate = simpleDateFormat.parse(endStr);
            int dayNum = (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
            betweenList = new ArrayList<>(dayNum);
            for (int index = 0; index < dayNum; index++) {
                betweenList.add(simpleDateFormat.format(DateUtil.addTime(startDate, Calendar.DATE,index)));
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage(),e);
        }

        return betweenList;
    }
}
