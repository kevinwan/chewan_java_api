package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface MessageService {

	/**
	 * 2.26 获取申请列表
	 * 
	 * @param userId
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动申请列表信息
	 * 
	 */
	ResponseDo getApplicationList(String userId, String token, int ignore, int limit) throws ApiException;

}
