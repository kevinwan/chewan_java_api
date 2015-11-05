package com.gongpingjia.carplay.statistic.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.statistic.StatisticUnRegister;
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

    /**
     * 埋点1 统计数据
     *
     * @param jsonObject
     * @return
     */
    public ResponseDo getUnRegisterInfo(JSONObject jsonObject) {
        try {
            String startStr = jsonObject.getString("startTime");
            String endStr = jsonObject.getString("endTime");
            Map<String, Integer> successMap = countService.getCountByDay(startStr, endStr, "statisticUnRegister", StatisticUnRegister.USER_REGISTER_SUCCESS);
            Map<String, Integer> nearByMap = countService.getCountByDay(startStr, endStr, "statisticUnRegister", StatisticUnRegister.UN_REGISTER_NEARBY_INVITED);
            Map<String, Integer> matchMap = countService.getCountByDay(startStr, endStr, "statisticUnRegister", StatisticUnRegister.UN_REGISTER_MATCH_INVITED);
            Map<String, Integer> unRegisterMap = countService.getCountByDay(startStr, endStr, "statisticUnRegister", StatisticUnRegister.USER_REGISTER);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("series", Arrays.asList("成功注册次数", "附近点击次数", "邀请次数", "未注册次数"));
            List<String> betweenStrList = DateBetweenUtil.getBetweenStrList(startStr, endStr);
            List<Map<String, Object>> countList = new ArrayList<>(betweenStrList.size());
            for (String itemDate : betweenStrList) {
                Map<String, Object> dataMap = new HashMap();
                dataMap.put("x", itemDate);
                int successCount = transferNullToZero(successMap, itemDate);
                int nearByCount = transferNullToZero(nearByMap, itemDate);
                int matchCount = transferNullToZero(matchMap, itemDate);
                int unRegisterCount = transferNullToZero(unRegisterMap, itemDate);
                dataMap.put("y", Arrays.asList(successCount, nearByCount, matchCount, unRegisterCount));
                countList.add(dataMap);
            }
            resultMap.put("data", countList);
            return ResponseDo.buildSuccessResponse(resultMap);
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    private int transferNullToZero(Map<String, Integer> sourceMap, String itemName) {
        return sourceMap.get(itemName) == null ? 0 : sourceMap.get(itemName);
    }


}
