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

@RestController
public class CarController {
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	
	@Autowired
	private CarService service;

	
	@RequestMapping(value = "/car/brand", method = RequestMethod.GET)
	public ResponseDo CarBrand(){
		
		LOG.debug("car/brand is called, no request parameter");
		
		try {
			return service.getCarBrand();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
		
		//1、定义服务层接口
		//定义restful名称，以及参数赋值
		//2、调用服务层方法，得到所需信息的data
		//3、将得到信息转为map对象
		//此处得到的carBrand为公平价返回的json数据
		//4、将信息封装为responseDo类，返回
	}
	
	
	@RequestMapping(value = "/car/model", method = RequestMethod.GET)
	public ResponseDo getCarModel(@RequestParam(value ="brand" ,defaultValue="") String brand){

		LOG.debug("car/model is called, request parameter brand:"+brand);
		
		try {
			return service.getCarModel(brand);
		} catch (ApiException e) {
			LOG.error(e.getMessage(), e);
			return ResponseDo.buildFailureResponse(e.getMessage());
		}
		
	}
	
	

}
