package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.IPFetchUtil;
import com.gongpingjia.carplay.dao.statistic.*;
import com.gongpingjia.carplay.entity.statistic.*;
import com.gongpingjia.carplay.statistic.aop.StatisticConstants;
import com.gongpingjia.carplay.statistic.service.RecordService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/11/3 0003.
 */
@Service("recordService")
public class RecordServiceImpl implements RecordService {


    private static final Logger LOG = LoggerFactory.getLogger(RecordServiceImpl.class);


    @Autowired
    private StatisticUnRegisterDao statisticUnRegisterDao;

    @Autowired
    private StatisticDynamicNearbyDao statisticDynamicNearbyDao;

    @Autowired
    private StatisticActivityContactDao statisticActivityContactDao;

    @Autowired
    private StatisticActivityMatchDao statisticActivityMatchDao;

    @Autowired
    private StatisticOfficialActivityDao statisticOfficialActivityDao;


    @Override
    public void recordAll(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("recordAll --------");
        unRegisterNearbyInvited(request, jsonObject);
        unRegisterMatchInvited(request, jsonObject);
        unRegisterDynamicAccept(request, jsonObject);
        dynamicAcceptRegister(request, jsonObject);
        appOpenCount(request, jsonObject);
        dynamicNearbyInvited(request, jsonObject);
        activityDynamicCall(request, jsonObject);
        activityDynamicChat(request, jsonObject);
        activityMatchInvitedCount(request, jsonObject);
        activityMatchCount(request, jsonObject);
        officialActivityBuyTicket(request, jsonObject);
        officialActivityChatJoin(request, jsonObject);
    }

    @Override
    public void unRegisterNearbyInvited(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("unRegisterNearbyInvited --------");

        int count = jsonObject.getInt("unRegisterNearbyInvited");
        StatisticUnRegister statisticUnRegister = new StatisticUnRegister();
        statisticUnRegister.setEvent(StatisticUnRegister.UN_REGISTER_NEARBY_INVITED);
        statisticUnRegister.setCount(count);
        statisticUnRegister.recordTime(DateUtil.getTime());
        statisticUnRegister.setIp(IPFetchUtil.getIPAddress(request));

        statisticUnRegisterDao.save(statisticUnRegister);
    }

    @Override
    public void unRegisterMatchInvited(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("unRegisterMatchInvited --------");

        int count = jsonObject.getInt("unRegisterMatchInvited");
        StatisticUnRegister statisticUnRegister = new StatisticUnRegister();
        statisticUnRegister.setEvent(StatisticUnRegister.UN_REGISTER_MATCH_INVITED);
        statisticUnRegister.setCount(count);
        statisticUnRegister.recordTime(DateUtil.getTime());
        statisticUnRegister.setIp(IPFetchUtil.getIPAddress(request));

        statisticUnRegisterDao.save(statisticUnRegister);
    }

    //埋点7

    @Override
    public void unRegisterDynamicAccept(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("unRegisterDynamicAccept --------");

        int count = jsonObject.getInt("unRegisterDynamicAccept");
        StatisticUnRegister statisticUnRegister = new StatisticUnRegister();
        statisticUnRegister.setEvent(StatisticUnRegister.UN_REGISTER_DYNAMIC_ACCEPT);
        statisticUnRegister.setCount(count);
        statisticUnRegister.recordTime(DateUtil.getTime());
        statisticUnRegister.setIp(IPFetchUtil.getIPAddress(request));

        statisticUnRegisterDao.save(statisticUnRegister);
    }


    @Override
    public void dynamicAcceptRegister(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("dynamicAcceptRegister --------");

        int count = jsonObject.getInt("dynamicAcceptRegister");
        StatisticUnRegister statisticUnRegister = new StatisticUnRegister();
        statisticUnRegister.setEvent(StatisticUnRegister.DYNAMIC_ACCEPT_REGISTER);
        statisticUnRegister.setCount(count);
        statisticUnRegister.recordTime(DateUtil.getTime());
        statisticUnRegister.setIp(IPFetchUtil.getIPAddress(request));

        statisticUnRegisterDao.save(statisticUnRegister);
    }


    /**
     * app 打开的次数  jsonObject中只需要  userId
     *
     * @param request
     * @param jsonObject
     */
    @Override
    public void appOpenCount(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("appOpenCount --------");

        StatisticDynamicNearby statisticDynamicNearby = new StatisticDynamicNearby();
        statisticDynamicNearby.setIp(IPFetchUtil.getIPAddress(request));
        statisticDynamicNearby.setEvent(StatisticDynamicNearby.APP_OPEN_COUNT);
        statisticDynamicNearby.setCount(jsonObject.getInt("appOpenCount"));
        statisticDynamicNearby.recordTime(DateUtil.getTime());

        statisticDynamicNearbyDao.save(statisticDynamicNearby);
    }

    @Override
    public void dynamicNearbyInvited(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("dynamicNearbyInvited --------");

        JSONArray jsonArray = getJsonArray(jsonObject, "dynamicNearbyInvited");
        if (jsonArray == null) return;
        String userId = getUserId(request);
        String ip = IPFetchUtil.getIPAddress(request);
        Long nowTime = DateUtil.getTime();
        Map<String, Integer> countMap = buildCountMap(jsonArray);
        Set<Map.Entry<String, Integer>> entrySet = countMap.entrySet();
        for (Map.Entry<String, Integer> item : entrySet) {
            StatisticDynamicNearby statisticDynamicNearby = new StatisticDynamicNearby();
            if (null != userId) {
                statisticDynamicNearby.setUserId(userId);
            }
            statisticDynamicNearby.setIp(ip);
            statisticDynamicNearby.setActivityId(item.getKey());
            statisticDynamicNearby.setEvent(StatisticDynamicNearby.DYNAMIC_NEARBY_INVITED);
            statisticDynamicNearby.setCount(item.getValue());
            statisticDynamicNearby.recordTime(nowTime);

            statisticDynamicNearbyDao.save(statisticDynamicNearby);
        }
    }

    @Override
    public void activityDynamicCall(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("activityDynamicCall --------");

        String userId = getUserId(request);
        if (null == userId) return;
        JSONArray appointmentIdArr = getJsonArray(jsonObject, "activityDynamicCall");
        if (null == appointmentIdArr || appointmentIdArr.isEmpty()) return;
        Map<String, Integer> callCountMap = buildCountMap(appointmentIdArr);
        Long nowTime = DateUtil.getTime();
        for (Map.Entry<String, Integer> countItem : callCountMap.entrySet()) {
            StatisticActivityContact saveItem = new StatisticActivityContact();
            saveItem.setUserId(userId);
            saveItem.setAppointmentId(countItem.getKey());
            saveItem.setEvent(StatisticActivityContact.ACTIVITY_DYNAMIC_CALL);
            saveItem.setCount(countItem.getValue());
            saveItem.recordTime(nowTime);

            statisticActivityContactDao.save(saveItem);
        }
    }

    @Override
    public void activityDynamicChat(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("activityDynamicCall --------");

        String userId = getUserId(request);
        if (null == userId) return;
        JSONArray appointmentIdArr = getJsonArray(jsonObject, "activityDynamicChat");
        if (null == appointmentIdArr || appointmentIdArr.isEmpty()) return;
        Map<String, Integer> callCountMap = buildCountMap(appointmentIdArr);
        Long nowTime = DateUtil.getTime();
        for (Map.Entry<String, Integer> countItem : callCountMap.entrySet()) {
            StatisticActivityContact saveItem = new StatisticActivityContact();
            saveItem.setUserId(userId);
            saveItem.setAppointmentId(countItem.getKey());
            saveItem.setEvent(StatisticActivityContact.ACTIVITY_DYNAMIC_CHAT);
            saveItem.setCount(countItem.getValue());
            saveItem.recordTime(nowTime);

            statisticActivityContactDao.save(saveItem);
        }
    }

    @Override
    public void activityMatchInvitedCount(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("activityMatchInvitedCount --------");

        String userId = getUserId(request);
        String ip = IPFetchUtil.getIPAddress(request);
        Long nowTime = DateUtil.getTime();
        JSONArray activityIdArr = getJsonArray(jsonObject, "activityDynamicChat");
        if (null == activityIdArr || activityIdArr.isEmpty()) return;
        Map<String, Integer> countMap = buildCountMap(activityIdArr);
        for (Map.Entry<String, Integer> countItem : countMap.entrySet()) {
            StatisticActivityMatch statisticActivityMatch = new StatisticActivityMatch();
            statisticActivityMatch.setActivityId(countItem.getKey());
            if (null != userId) {
                statisticActivityMatch.setUserId(userId);
            }
            statisticActivityMatch.setIp(ip);
            statisticActivityMatch.setEvent(StatisticActivityMatch.ACTIVITY_MATCH_INVITED_COUNT);
            statisticActivityMatch.setCount(countItem.getValue());
            statisticActivityMatch.recordTime(nowTime);

            statisticActivityMatchDao.save(statisticActivityMatch);
        }
    }

    @Override
    public void activityMatchCount(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("activityMatchCount --------");

        String userId = getUserId(request);
        String ip = IPFetchUtil.getIPAddress(request);
        StatisticActivityMatch statisticActivityMatch = new StatisticActivityMatch();
        if (null != userId) {
            statisticActivityMatch.setUserId(userId);
        }
        statisticActivityMatch.setIp(ip);
        statisticActivityMatch.setEvent(StatisticActivityMatch.ACTIVITY_MATCH_COUNT);
        statisticActivityMatch.setCount(jsonObject.getInt("activityMatchCount"));

        statisticActivityMatchDao.save(statisticActivityMatch);
    }

    @Override
    public void officialActivityBuyTicket(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("officialActivityBuyTicket --------");

        String userId = getUserId(request);
        if (null == userId) return;
        JSONArray jsonArray = getJsonArray(jsonObject, "officialActivityBuyTicket");
        if (null == jsonArray || jsonArray.isEmpty()) return;
        String ip = IPFetchUtil.getIPAddress(request);
        Map<String, Integer> officialActivityIdCountMap = buildCountMap(jsonArray);
        Long nowTime = DateUtil.getTime();
        for (Map.Entry<String, Integer> countItem : officialActivityIdCountMap.entrySet()) {
            StatisticOfficialActivity saveItem = new StatisticOfficialActivity();
            saveItem.setUserId(userId);
            saveItem.setOfficialActivityId(countItem.getKey());
            saveItem.setIp(ip);
            saveItem.setEvent(StatisticOfficialActivity.OFFICIAL_ACTIVITY_BUY_TICKET);
            saveItem.setCount(countItem.getValue());
            saveItem.recordTime(nowTime);

            statisticOfficialActivityDao.save(saveItem);
        }
    }

    @Override
    public void officialActivityChatJoin(HttpServletRequest request, JSONObject jsonObject) {
        LOG.info("officialActivityChatJoin --------");

        String userId = getUserId(request);
        if (null == userId) return;
        JSONArray jsonArray = getJsonArray(jsonObject, "officialActivityChatJoin");
        if (null == jsonArray || jsonArray.isEmpty()) return;
        String ip = IPFetchUtil.getIPAddress(request);
        Map<String, Integer> officialActivityIdCountMap = buildCountMap(jsonArray);
        Long nowTime = DateUtil.getTime();
        for (Map.Entry<String, Integer> countItem : officialActivityIdCountMap.entrySet()) {
            StatisticOfficialActivity saveItem = new StatisticOfficialActivity();
            saveItem.setUserId(userId);
            saveItem.setOfficialActivityId(countItem.getKey());
            saveItem.setIp(ip);
            saveItem.setEvent(StatisticOfficialActivity.OFFICIAL_ACTIVITY_CHAT_JOIN);
            saveItem.setCount(countItem.getValue());
            saveItem.recordTime(nowTime);

            statisticOfficialActivityDao.save(saveItem);
        }
    }


    private String getUserId(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        if (StringUtils.isEmpty(userId)) {
            return null;
        } else {
            return userId;
        }
    }

    private JSONArray getJsonArray(JSONObject jsonObject, String arrayName) {
        if (!jsonObject.containsKey(arrayName)) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray(arrayName);
        if (null == jsonArray || jsonArray.size() == 0) {
            return null;
        }
        return jsonArray;
    }

    private Map<String, Integer> buildCountMap(JSONArray jsonArray) {
        HashMap<String, Integer> countMap = new HashMap<>(jsonArray.size());
        for (int index = 0; index < jsonArray.size(); index++) {
            String itemId = (String) jsonArray.get(index);
            if (countMap.get(itemId) == null) {
                countMap.put(itemId, 1);
            } else {
                countMap.put(itemId, countMap.get(itemId) + 1);
            }
        }
        return countMap;
    }


}
