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
import com.gongpingjia.carplay.controller.VersionController;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	private static final Logger LOG = LoggerFactory.getLogger(VersionController.class);

	
	@Autowired
	private ActivityApplicationDao activityApplicationDao;
	
	@Override
	public ResponseDo getApplicationList(String userId, String token, int ignore, int limit) throws ApiException {
		
		String statusPendingProcessed= "待处理";
		String assetUrl = PropertiesUtil.getProperty("qiniu.server.url", "")+"asset";
		String gpjImgUrl = PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "");
		
		Map<String, Object> param=new HashMap<>(5,1);
		param.put("userId", userId);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("status", statusPendingProcessed);
		param.put("assertUrl", assetUrl);
		param.put("gpjImgUrl",gpjImgUrl);
		List<Map<String,Object>>  activityApplicationList=activityApplicationDao.selectByOrganizer(param);
		return ResponseDo.buildSuccessResponse(activityApplicationList);
	}

}
