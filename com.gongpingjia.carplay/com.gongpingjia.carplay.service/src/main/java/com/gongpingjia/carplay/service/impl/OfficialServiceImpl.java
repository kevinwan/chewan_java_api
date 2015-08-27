package com.gongpingjia.carplay.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.ActivityViewDao;
import com.gongpingjia.carplay.service.OfficialService;

@Service
public class OfficialServiceImpl implements OfficialService {

	private static final Logger LOG = LoggerFactory.getLogger(OfficialServiceImpl.class);

	@Autowired
	private ActivityViewDao activityViewDao;

	@Override
	public ResponseDo getActivityList() {
		LOG.debug("Query official activity list begin");
		Map<String, String> param = new HashMap<String, String>(2, 1);
		param.put("assetUrl", CommonUtil.getPhotoServer());
		param.put("category", Constants.ActivityCatalog.OFFICIAL);

		List<Map<String, String>> data = activityViewDao.selectActivityByCategory(param);
		return ResponseDo.buildSuccessResponse(data);
	}

}
