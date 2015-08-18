package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface MessageService {

	/**
	 * 2.26 获取申请列表
	 * 
	 * @param userId
	 *            访问者的userId
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动申请列表信息
	 * 
	 */
	ResponseDo getApplicationList(String userId, int ignore, int limit) throws ApiException;

	/**
	 * 2.41 获取最新消息数
	 * 
	 * @param userId
	 *            访问者的userId
	 * 
	 * @return 未读的消息数量和最新的一条消息信息
	 * 
	 */
	ResponseDo getMessageCount(String userId) throws ApiException;

	/**
	 * 2.42 获取消息列表
	 * 
	 * @param userId
	 *            用户Id
	 * @param type
	 *            留言类型conmment 或 application
	 * @param ignore
	 *            返回结果将扔掉的条数, 不填默认为 0
	 * @param limit
	 *            返回的条数, 默认为 10
	 * 
	 */
	ResponseDo getMessageList(String userId, String type, int ignore, int limit)throws ApiException ;

}
