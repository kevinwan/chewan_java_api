package com.gongpingjia.carplay.official.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import net.sf.json.JSONObject;

/**
 * Created by licheng on 2015/9/28.
 * 官方活动接口
 */
public interface OfficialActivityService {

    /**
     * 创建官方活动
     *
     * @param activity 活动对象
     * @param json     请求参数
     * @return 返回创建结果信息
     * @throws ApiException
     */
    ResponseDo registerActivity(OfficialActivity activity, JSONObject json) throws ApiException;


    ResponseDo getActivityList(String   userId,JSONObject json) throws ApiException;
}
