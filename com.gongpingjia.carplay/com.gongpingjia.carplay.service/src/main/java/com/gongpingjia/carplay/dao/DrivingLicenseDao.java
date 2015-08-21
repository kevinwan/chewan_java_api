package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.DrivingLicense;

public interface DrivingLicenseDao {
	int deleteByPrimaryKey(String userid);

	int insert(DrivingLicense record);

	DrivingLicense selectByPrimaryKey(String userid);

	int updateByPrimaryKey(DrivingLicense record);
}