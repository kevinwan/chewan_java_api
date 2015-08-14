package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Car;

public interface CarDao {
	int deleteByPrimaryKey(String id);

	int insert(Car record);

	Car selectByPrimaryKey(String id);

	int updateByPrimaryKey(Car record);

	public Car selectByUserId(String userId);
}