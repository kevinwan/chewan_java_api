package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface CarService {
	ResponseDo getCarBrand() throws ApiException;

	ResponseDo getCarModel(String brand) throws ApiException;

}
