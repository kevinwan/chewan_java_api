package com.gongpingjia.carplay.service.impl;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.service.CarService;

import net.sf.json.JSONObject;

@Service
public class CarServiceImpl implements CarService {

	private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);

	public ResponseDo getCarBrand() throws ApiException {
		
		JSONObject json=new JSONObject();
		try {
			String gpjUrl = PropertiesUtil.getProperty("gongpingjia.brand.url", "");
			Header header = new BasicHeader("Accept", "application/json");
			CloseableHttpResponse response = HttpClientUtil.get(gpjUrl, new HashMap<String,String>(), Arrays.asList(header), "UTF-8");
			String data=HttpClientUtil.parseResponse(response);
			json=JSONObject.fromObject(data);
			HttpClientUtil.close(response);
		} catch (Exception e) {
			LOG.error("Failed to get brand information");
			throw new ApiException("未能成功获取品牌信息");
		}
		return ResponseDo.buildSuccessResponse(json);
	}
	
	public ResponseDo getCarModel(String brand) throws ApiException{
		if(brand.equals(""))throw new ApiException("请输入品牌信息");
		JSONObject json=new JSONObject();
		try {
			String gpjUrl = PropertiesUtil.getProperty("gongpingjia.mode.url", "");
			Map<String, String> params = new HashMap<String, String>(1,1);
			params.put("brand", brand);
			Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");
			CloseableHttpResponse response = HttpClientUtil.get(gpjUrl, params, Arrays.asList(header), "GBK");
			String data=HttpClientUtil.parseResponse(response);
			json=JSONObject.fromObject(data);
			HttpClientUtil.close(response);
		} catch (Exception e) {
			LOG.error("Failed to get model information");
			throw new ApiException("未能成功获取车型信息");
		}
		return ResponseDo.buildSuccessResponse(json);
	}
	
	

}
