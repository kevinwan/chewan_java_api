package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.VersionDao;
import com.gongpingjia.carplay.po.Version;

@Service
public class VersionDaoImpl implements VersionDao {

	@Override
	public int deleteByPrimaryKey(String product) {
		return DASUtil.delete(Version.class.getName(), "deleteByPrimaryKey", product);
	}

	@Override
	public int insert(Version record) {
		return DASUtil.save(Version.class.getName(), "insert", record);
	}

	@Override
	public Version selectByPrimaryKey(String product) {
		return DASUtil.selectOne(Version.class.getName(), "selectByPrimaryKey", product);
	}

	@Override
	public int updateByPrimaryKey(Version record) {
		return DASUtil.update(Version.class.getName(), "updateByPrimaryKey", record);
	}

}
