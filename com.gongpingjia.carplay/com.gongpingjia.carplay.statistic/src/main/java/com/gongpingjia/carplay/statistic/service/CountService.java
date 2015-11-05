package com.gongpingjia.carplay.statistic.service;

import com.gongpingjia.carplay.common.exception.ApiException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public interface CountService {

    public List<Map> getCountMap(long startTime,long endTime,String collectionName,String eventType,String ...timeFieldName);


    public Map<String,Integer> getCountByDay(String startStr, String endStr, String collectionName, String eventType) throws ApiException;
}
