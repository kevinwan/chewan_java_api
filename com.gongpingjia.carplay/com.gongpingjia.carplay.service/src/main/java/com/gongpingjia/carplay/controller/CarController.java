package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.CarService;

/**
 * 2.6获取品牌 2.7获取车型
 * 
 * @author zhou shuofu
 */
@RestController
public class CarController {
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private CarService service;

	/**
	 * 2.6 获取品牌
	 * 
	 * @return 品牌信息
	 */
	@RequestMapping(value = "/car/brand", method = RequestMethod.GET)
	public ResponseDo carBrand() {

		LOG.debug("car/brand is called, no request parameter");

		try {
			return service.getCarBrand();
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}

	/**
	 * 2.7获取车型信息@throws ApiException
	 * 
	 * @param brand
	 *            品牌
	 * @return 此品牌的车型信息
	 */
	@RequestMapping(value = "/car/model", method = RequestMethod.GET)
	public ResponseDo getCarModel(@RequestParam(value = "brand") String brand) {

		LOG.debug("car/model is called, request parameter brand:" + brand);

		try {
			return service.getCarModel(brand);
		} catch (ApiException e) {
			LOG.warn(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
	}
}
