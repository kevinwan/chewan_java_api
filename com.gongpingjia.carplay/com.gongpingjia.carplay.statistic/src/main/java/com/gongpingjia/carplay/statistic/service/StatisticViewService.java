package com.gongpingjia.carplay.statistic.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public interface StatisticViewService {

    public ResponseDo getUnRegisterInfo(JSONObject jsonObject);
}
