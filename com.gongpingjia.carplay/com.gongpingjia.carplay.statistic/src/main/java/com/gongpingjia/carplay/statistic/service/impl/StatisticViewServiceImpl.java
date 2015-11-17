package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.entity.statistic.*;
import com.gongpingjia.carplay.statistic.service.CountService;
import com.gongpingjia.carplay.statistic.service.StatisticViewService;
import com.gongpingjia.carplay.statistic.tool.DateBetweenUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
@Service("statisticViewService")
public class StatisticViewServiceImpl implements StatisticViewService {

    @Autowired
    private CountService countService;

    private Logger logger = LoggerFactory.getLogger(StatisticViewService.class);


    @Override
    public ResponseDo dispatchAllInfo(JSONObject jsonObject, int type) throws ApiException {
        if (!Constants.BuriedPointType.TYPE_LIST.contains(type)) {
            throw new ApiException("参数非法");
        }

//        List<EventInfo> eventInfoList = getEventMapFromType(type);
//        List<String> nameList = new ArrayList<>(eventInfoList.size());
//        Map<String, String> eventMap = new HashMap<>(eventInfoList.size());
//        for (EventInfo eventInfo : eventInfoList) {
//            nameList.add(eventInfo.showName);
//            eventMap.put(eventInfo.eventName, eventInfo.collectionName);
//        }
        return ResponseDo.buildSuccessResponse(extractCommonInfo(jsonObject, getEventMapFromType(type)));
    }


    /**
     * 埋点1 统计数据
     *
     * @param jsonObject
     * @return
     */
    public ResponseDo getUnRegisterInfo(JSONObject jsonObject) {
        try {
            List<String> nameList = Arrays.asList("成功注册次数", "附近点击次数", "邀请次数", "未注册次数");
            Map<String, String> eventTypeMap = new HashMap<>(4);
            eventTypeMap.put(StatisticUnRegister.USER_REGISTER_SUCCESS, "statisticUnRegister");
            eventTypeMap.put(StatisticUnRegister.UN_REGISTER_NEARBY_INVITED, "statisticUnRegister");
            eventTypeMap.put(StatisticUnRegister.UN_REGISTER_MATCH_INVITED, "statisticUnRegister");
            eventTypeMap.put(StatisticUnRegister.USER_REGISTER, "statisticUnRegister");

            return ResponseDo.buildSuccessResponse(getCommonInfo(jsonObject, nameList, eventTypeMap));
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    private int transferNullToZero(Map<String, Integer> sourceMap, String itemName) {
        return sourceMap.get(itemName) == null ? 0 : sourceMap.get(itemName);
    }


    private Map<String, Object> getCommonInfo(JSONObject jsonObject, List<String> nameList, Map<String, String> eventTypeMap) throws ApiException {
        if (nameList.size() != eventTypeMap.size()) {
            throw new ApiException("illegal data");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("series", nameList);

        List<Map<String, Integer>> countMapList = new ArrayList<>(nameList.size());
        String startStr = jsonObject.getString("startTime");
        String endStr = jsonObject.getString("endTime");
        for (Map.Entry<String, String> eventType : eventTypeMap.entrySet()) {
            String eventName = eventType.getKey();
            String collectionName = eventType.getValue();
            Map<String, Integer> eventCountMap = countService.getCountByDay(startStr, endStr, collectionName, eventName);
            countMapList.add(eventCountMap);
        }

        List<String> betweenStrList = DateBetweenUtil.getBetweenStrList(startStr, endStr);
        ArrayList<Map<String, Object>> countList = new ArrayList<>(betweenStrList.size());
        for (String itemDate : betweenStrList) {
            Map<String, Object> dataMap = new HashMap();
            dataMap.put("x", itemDate);
            ArrayList itemCountList = new ArrayList(countList.size());
            for (Map<String, Integer> countMap : countMapList) {
//                itemCountList.add(countMap.get(itemDate));
                itemCountList.add(transferNullToZero(countMap, itemDate));
            }

            dataMap.put("y", itemCountList);
            countList.add(dataMap);
        }
        resultMap.put("data", countList);
        return resultMap;
    }

    private Map<String, Object> extractCommonInfo(JSONObject jsonObject, List<EventInfo> eventInfoList) throws ApiException {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> nameList = new ArrayList<>(eventInfoList.size());
        List<Map<String, Integer>> countMapList = new ArrayList<>(eventInfoList.size());
        String startStr = jsonObject.getString("startTime");
        String endStr = jsonObject.getString("endTime");
        for (EventInfo eventInfo : eventInfoList) {
            Map<String, Integer> eventCountMap = countService.getCountByDay(startStr, endStr, eventInfo.collectionName, eventInfo.eventName);
            countMapList.add(eventCountMap);
            nameList.add(eventInfo.showName);
        }
        resultMap.put("series", nameList);

        List<String> betweenStrList = DateBetweenUtil.getBetweenStrList(startStr, endStr);
        ArrayList<Map<String, Object>> countList = new ArrayList<>(betweenStrList.size());
        for (String itemDate : betweenStrList) {
            Map<String, Object> dataMap = new HashMap();
            dataMap.put("x", itemDate);
            ArrayList itemCountList = new ArrayList(countList.size());
            for (Map<String, Integer> countMap : countMapList) {
//                itemCountList.add(countMap.get(itemDate));
                itemCountList.add(transferNullToZero(countMap, itemDate));
            }

            dataMap.put("y", itemCountList);
            countList.add(dataMap);
        }
        resultMap.put("data", countList);
        return resultMap;
    }


    @Override
    public ResponseDo getActivityDynamicInfo(JSONObject jsonObject) {
        return null;
    }

    @Override
    public ResponseDo getActivityMatchInfo(JSONObject jsonObject) {
        return null;
    }

    @Override
    public ResponseDo getOfficialInfo(JSONObject jsonObject) {
        return null;
    }

    @Override
    public ResponseDo getDynamicNearByInfo(JSONObject jsonObject) {
        return null;
    }

    @Override
    public ResponseDo getDrivingInfo(JSONObject jsonObject) {
        return null;
    }


    public List<EventInfo> getEventMapFromType(int type) {
        switch (type) {
            case 2:
                return getStatisticActivityContactMap();
            case 3:
                return getStatisticActivityMatchMap();
            case 6:
                return getStatisticDriverAuthMap();
            case 5:
                return getStatisticDynamicNearbyMap();
            case 4:
                return getStatisticOfficialActivityMap();
            case 1:
                return getStatisticUnRegisterMap();
            case 7:
                return getStatisticUnRegisterDynamicMap();
        }
        return null;
    }

    //2
    public List<EventInfo> getStatisticActivityContactMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(2);
        String collectionName = "statisticActivityContact";
        eventInfoList.add(new EventInfo(StatisticActivityContact.ACTIVITY_DYNAMIC_CALL, collectionName, "电话"));
        eventInfoList.add(new EventInfo(StatisticActivityContact.ACTIVITY_DYNAMIC_CHAT, collectionName, "聊天"));
        return eventInfoList;
    }

    //3
    public List<EventInfo> getStatisticActivityMatchMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(4);
        String collectionName = "statisticActivityMatch";
        eventInfoList.add(new EventInfo(StatisticActivityMatch.ACTIVITY_TYPE_MATCH_COUNT, collectionName, "类型匹配"));
        eventInfoList.add(new EventInfo(StatisticActivityMatch.ACTIVITY_MATCH_INVITED_COUNT, collectionName, "邀她点击"));
        eventInfoList.add(new EventInfo(StatisticActivityMatch.ACTIVITY_MATCH_COUNT, collectionName, "附近点击"));
        eventInfoList.add(new EventInfo(StatisticActivityMatch.ACTIVITY_TYPE_CLICK, collectionName, "类型点击"));
        return eventInfoList;
    }

    //6
    public List<EventInfo> getStatisticDriverAuthMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(3);
        String collectionName = "statisticDriverAuth";
        eventInfoList.add(new EventInfo(StatisticDriverAuth.DRIVER_AUTHENTICATION, collectionName, "车主认证"));
        eventInfoList.add(new EventInfo(StatisticDriverAuth.DRIVING_LICENSE_AUTH, collectionName, "行驶证上传"));
        eventInfoList.add(new EventInfo(StatisticDriverAuth.DRIVER_LICENSE_AUTH, collectionName, "驾驶证上传"));
        return eventInfoList;
    }

    //5
    public List<EventInfo> getStatisticDynamicNearbyMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(3);
        String collectionName = "statisticDynamicNearby";
        eventInfoList.add(new EventInfo(StatisticDynamicNearby.APP_OPEN_COUNT, collectionName, "App打开次数"));
        eventInfoList.add(new EventInfo(StatisticDynamicNearby.DYNAMIC_NEARBY_CLICK, collectionName, "附近活动推送次数"));
        eventInfoList.add(new EventInfo(StatisticDynamicNearby.DYNAMIC_NEARBY_INVITED, collectionName, "动态附近邀请"));
        return eventInfoList;
    }

    //4
    public List<EventInfo> getStatisticOfficialActivityMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(4);
        String collectionName = "statisticOfficialActivity";
        eventInfoList.add(new EventInfo(StatisticOfficialActivity.OFFICIAL_ACTIVITY_JOIN, collectionName, "官方活动加入次数"));
        eventInfoList.add(new EventInfo(StatisticOfficialActivity.OFFICIAL_ACTIVITY_COUNT, collectionName, "官方活动点击次数"));
        eventInfoList.add(new EventInfo(StatisticOfficialActivity.OFFICIAL_ACTIVITY_BUY_TICKET, collectionName, "购票点击次数"));
        eventInfoList.add(new EventInfo(StatisticOfficialActivity.OFFICIAL_ACTIVITY_CHAT_JOIN, collectionName, "群聊点击次数"));
        return eventInfoList;
    }

    //1
    public List<EventInfo> getStatisticUnRegisterMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(4);
        String collectionName = "statisticUnRegister";
        eventInfoList.add(new EventInfo(StatisticUnRegister.USER_REGISTER_SUCCESS, collectionName, "成功注册次数"));
        eventInfoList.add(new EventInfo(StatisticUnRegister.UN_REGISTER_NEARBY_INVITED, collectionName, "邀她点击次数"));
        eventInfoList.add(new EventInfo(StatisticUnRegister.UN_REGISTER_MATCH_INVITED, collectionName, "活动匹配次数"));
        eventInfoList.add(new EventInfo(StatisticUnRegister.USER_REGISTER, collectionName, "注册点击次数"));
        return eventInfoList;
    }

    //7
    public List<EventInfo> getStatisticUnRegisterDynamicMap() {
        List<EventInfo> eventInfoList = new ArrayList<>(2);
        String collectionName = "statisticUnRegister";
        eventInfoList.add(new EventInfo(StatisticUnRegister.UN_REGISTER_DYNAMIC_ACCEPT, collectionName, "未注册用户同意邀请"));
        eventInfoList.add(new EventInfo(StatisticUnRegister.DYNAMIC_ACCEPT_REGISTER, collectionName, "未注册用户同意后注册"));
        return eventInfoList;
    }

    private class EventInfo {
        public String eventName;
        public String collectionName;
        public String showName;

        public EventInfo(String eventName, String collectionName, String showName) {
            this.eventName = eventName;
            this.collectionName = collectionName;
            this.showName = showName;
        }
    }
}
