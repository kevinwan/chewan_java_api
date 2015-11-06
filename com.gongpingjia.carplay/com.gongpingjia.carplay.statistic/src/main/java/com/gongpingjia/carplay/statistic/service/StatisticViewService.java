package com.gongpingjia.carplay.statistic.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public interface StatisticViewService {

    public ResponseDo dispatchAllInfo(JSONObject jsonObject, int type) throws ApiException;

    /**
     * 埋点1 统计信息 4组数据
     * @param jsonObject
     * @return
     */
    public ResponseDo getUnRegisterInfo(JSONObject jsonObject);


    public ResponseDo getActivityDynamicInfo(JSONObject jsonObject);

    public ResponseDo getActivityMatchInfo(JSONObject jsonObject);

    public ResponseDo getOfficialInfo(JSONObject jsonObject);

    public ResponseDo getDynamicNearByInfo(JSONObject jsonObject);

    public ResponseDo getDrivingInfo(JSONObject jsonObject);
}
