package com.gongpingjia.carplay.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.service.CarService;

@Service
public class CarServiceImpl implements CarService {

	private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);

	/**
	 * 获取名牌信息
	 * 
	 * @return 品牌信息
	 */
	public ResponseDo getCarBrand() throws ApiException {

		JSONObject json = new JSONObject();
		String gpjUrl = PropertiesUtil.getProperty("gongpingjia.brand.url", "");

		Header header = new BasicHeader("Accept", "application/json");

		LOG.debug("Begin request gongpingjia server");
		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.get(gpjUrl, new HashMap<String, String>(0), Arrays.asList(header),
					Constants.Charset.UTF8);

			String data = HttpClientUtil.parseResponse(response);
			json = JSONObject.fromObject(data);
		} finally {
			HttpClientUtil.close(response);
		}

		return ResponseDo.buildSuccessResponse(json);
	}

	/**
	 * 通过品牌获取车型
	 * 
	 * @param brand
	 *            品牌
	 * @return 此品牌的车型信息
	 */
	public ResponseDo getCarModel(String brand) throws ApiException {
		if (brand.equals("")) {
			LOG.warn("Input brand is empty, brand:{}", brand);
			throw new ApiException("请输入品牌信息");
		}

		JSONObject json = new JSONObject();

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put("brand", brand);

		String gpjUrl = PropertiesUtil.getProperty("gongpingjia.mode.url", "");
		Header header = new BasicHeader("Accept", "application/json; charset=UTF-8");
		CloseableHttpResponse response = null;

		try {
			response = HttpClientUtil.get(gpjUrl, params, Arrays.asList(header), Constants.Charset.UTF8);

			String data = HttpClientUtil.parseResponse(response);
			json = JSONObject.fromObject(data);
		} finally {
			HttpClientUtil.close(response);
		}

		return ResponseDo.buildSuccessResponse(json);
	}

}
