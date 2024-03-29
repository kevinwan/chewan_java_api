package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.service.CarService;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);

    @Autowired
    private CacheManager cacheManager;

    /**
     * 获取名牌信息
     *
     * @return 品牌信息
     */
    public ResponseDo getCarBrand() throws ApiException {
        LOG.debug("Query data from cache first");
        JSONObject jsonCache = cacheManager.getCarBrand();
        if (jsonCache != null) {
            return ResponseDo.buildSuccessResponse(jsonCache.get("brand"));
        }

        LOG.debug("Query data from gongpingjia if no data exist in the cache");
        JSONArray dataJson = new JSONArray();
        String gpjUrl = PropertiesUtil.getProperty("gongpingjia.brand.url", "");

        Header header = new BasicHeader("Accept", "application/json");

        LOG.debug("Begin request gongpingjia server");
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtil.get(gpjUrl, new HashMap<String, String>(0), Arrays.asList(header), Constants.Charset.UTF8);

            String data = HttpClientUtil.parseResponse(response);

            JSONObject json = JSONObject.fromObject(data);
            dataJson = json.getJSONArray("brand");

            for (int i = 0; i < dataJson.size(); i++) {
                dataJson.getJSONObject(i).put("logo_img", CommonUtil.getGPJBrandLogoPrefix() + dataJson.getJSONObject(i).getString("slug") + ".png");
            }

            LOG.debug("Refresh brand info in cache server");

            json.put("brand", dataJson);
            cacheManager.setCarBrand(json.toString());
        } finally {
            HttpClientUtil.close(response);
        }

        return ResponseDo.buildSuccessResponse(dataJson);
    }

    /**
     * 通过品牌获取车型
     *
     * @param brand 品牌
     * @return 此品牌的车型信息
     */
    public ResponseDo getCarModel(String brand) throws ApiException {

        LOG.debug("Query data from cache first");
        JSONObject jsonCache = cacheManager.getCarModel(brand);
        if (jsonCache != null) {
            return ResponseDo.buildSuccessResponse(jsonCache.get("model_list"));
        }

        LOG.debug("Query data from gongpingjia if no data exist in the cache");
        JSONObject json = new JSONObject();

        Map<String, String> params = new HashMap<String, String>(1, 1);
        params.put("brand", brand);

        String gpjUrl = PropertiesUtil.getProperty("gongpingjia.mode.url", "");
        Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");
        CloseableHttpResponse response = null;

        JSONArray dataJson = new JSONArray();

        try {
            response = HttpClientUtil.get(gpjUrl, params, Arrays.asList(header), Constants.Charset.UTF8);

            String data = HttpClientUtil.parseResponse(response);
            json = JSONObject.fromObject(data);

            try {
                dataJson = json.getJSONArray("model_list");

                LOG.debug("Refresh data in the cache server");

                cacheManager.setCarMode(brand, data);
            } catch (JSONException ex) {
                LOG.warn("get model_list from gongpingjia error, brand:{}, json:{}", brand, json);
                LOG.warn(ex.getMessage(), ex);
            }
        } finally {
            HttpClientUtil.close(response);
        }

        return ResponseDo.buildSuccessResponse(dataJson);
    }

}
