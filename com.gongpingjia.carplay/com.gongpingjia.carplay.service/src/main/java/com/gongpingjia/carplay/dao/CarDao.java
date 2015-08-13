package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.Car;

public interface CarDao {
	int deleteByPrimaryKey(String id);

	int insert(Car record);

	Car selectByPrimaryKey(String id);

	int updateByPrimaryKey(Car record);

	List<Car> selectByParam(Map<String, Object> param);
}