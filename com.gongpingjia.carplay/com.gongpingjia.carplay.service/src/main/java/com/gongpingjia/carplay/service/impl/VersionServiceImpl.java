package com.gongpingjia.carplay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.dao.VersionDao;
import com.gongpingjia.carplay.po.Version;
import com.gongpingjia.carplay.service.VersionService;

@Service
public class VersionServiceImpl implements VersionService {

	@Autowired
	private VersionDao dao;

	@Override
	public Version getVersion(String product) {
		return dao.selectByPrimaryKey(product);
	}

}
