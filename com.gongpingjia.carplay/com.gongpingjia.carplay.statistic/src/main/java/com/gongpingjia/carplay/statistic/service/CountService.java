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


    /**
     * 根据 天 排列 统计信息
     * startStr endStr 必须是 'yyyy-MM-dd'
     * @param startStr
     * @param endStr
     * @param collectionName
     * @param eventType
     * @return
     * @throws ApiException
     */
    public Map<String,Integer> getCountByDay(String startStr, String endStr, String collectionName, String eventType) throws ApiException;

    public Map<String, Integer> getCountByWeek(String startStr, String endStr, String collectionName, String eventType) throws ApiException;

    public Map<String, Integer> getCountByMonth(String startStr, String endStr, String collectionName, String eventType) throws ApiException;
}
