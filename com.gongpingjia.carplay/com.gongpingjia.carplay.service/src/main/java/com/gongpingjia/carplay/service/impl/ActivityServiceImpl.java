package com.gongpingjia.carplay.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Autowired
	private CarDao carDao;

	@Override
	public ResponseDo getAvailableSeats(String userId, String token) throws ApiException {

		ParameterCheck.getInstance().checkUserInfo(userId, token);

		LOG.debug("Query car list by userId");
		Map<String, Object> param = new HashMap<String, Object>(1, 1);
		List<Car> carList = carDao.selectByParam(param);

		Map<String, Object> result = new HashMap<String, Object>(3, 1);
		if (carList.isEmpty()) {
			LOG.info("User has not authicate in system");
			result.put("maxValue", PropertiesUtil.getProperty("user.unauth.car.max.seats", 2));
			result.put("minValue", PropertiesUtil.getProperty("user.unauth.car.min.seats", 1));
			result.put("isAuthenticated", 0);
		} else {
			LOG.info("User has already authicated");
			Car car = carList.get(0);
			result.put("maxValue",
					car.getSeat() > 1 ? car.getSeat() - 1 : PropertiesUtil.getProperty("user.unauth.car.max.seats", 2));
			result.put("minValue", PropertiesUtil.getProperty("user.unauth.car.min.seats", 1));
			result.put("isAuthenticated", 1);
		}

		return ResponseDo.buildSuccessResponse(result);
	}
}
