package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;

public interface OfficialService {

	/**
	 * 获取官方活动
	 * 
	 * @return 返回活动信息
	 */
	ResponseDo getActivityList();

}
