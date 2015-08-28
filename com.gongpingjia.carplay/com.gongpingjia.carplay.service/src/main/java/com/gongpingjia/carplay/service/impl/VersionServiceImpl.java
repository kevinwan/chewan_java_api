package com.gongpingjia.carplay.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.dao.VersionDao;
import com.gongpingjia.carplay.po.Version;
import com.gongpingjia.carplay.service.VersionService;

@Service
public class VersionServiceImpl implements VersionService {

	private static final Logger LOG = LoggerFactory.getLogger(VersionServiceImpl.class);

	@Autowired
	private VersionDao dao;

	@Override
	public ResponseDo getVersion(String product) throws ApiException {
		LOG.debug("Request parameter product: {}", product);
		Version version = dao.selectByPrimaryKey(product);
		if (version == null) {
			LOG.warn("No version info exist, product: {}", product);
			throw new ApiException("获取版本信息失败");
		}
		if (version.getRemarks() == null) {
			version.setRemarks("");
		}
		return ResponseDo.buildSuccessResponse(version);
	}
}
