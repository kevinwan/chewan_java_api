package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface ActivityService {
	/**
	 * 获取可提供的空座位数
	 * 
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话token
	 * @return 返回查询结果信息
	 */
	ResponseDo getAvailableSeats(String userId, String token) throws ApiException;
}
