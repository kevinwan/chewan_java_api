package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.po.Car;

@Service
public class CarDaoImpl implements CarDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(Car.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(Car record) {
		return DASUtil.save(Car.class.getName(), "insert", record);
	}

	@Override
	public Car selectByPrimaryKey(String id) {
		return DASUtil.selectOne(Car.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(Car record) {
		return DASUtil.update(Car.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<Car> selectByParam(Map<String, Object> param) {
		return DASUtil.selectList(Car.class.getName(), "selectByParam", param);
	}

}
