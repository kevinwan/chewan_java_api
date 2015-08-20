package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface CarService {

	/**
	 * 获取名牌信息
	 * 
	 * @return 品牌信息
	 * @throws ApiException
	 */
	ResponseDo getCarBrand() throws ApiException;

	/**
	 * 通过品牌获取车型
	 * 
	 * @param brand
	 *            品牌
	 * @return 此品牌的车型信息
	 * @throws ApiException
	 */
	ResponseDo getCarModel(String brand) throws ApiException;

}
