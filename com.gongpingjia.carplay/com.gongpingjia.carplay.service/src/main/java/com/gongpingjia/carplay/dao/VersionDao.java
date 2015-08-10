package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.Version;

public interface VersionDao {
	int deleteByPrimaryKey(String product);

	int insert(Version record);

	Version selectByPrimaryKey(String product);

	int updateByPrimaryKey(Version record);
}