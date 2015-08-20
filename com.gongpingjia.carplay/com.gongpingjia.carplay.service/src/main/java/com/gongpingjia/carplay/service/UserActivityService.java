package com.gongpingjia.carplay.service;

import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;

public interface UserActivityService {

	/**
	 * 2.21 我(TA)的发布
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动发布列表信息
	 */
	@Transactional(readOnly = true)
	ResponseDo getUserPost(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException;

	/**
	 * 2.22 我(TA)的关注
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动关注活动列表信息
	 */
	@Transactional(readOnly = true)
	ResponseDo getUserSubscribe(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException;

	/**
	 * 2.23 我(TA)的参与
	 * 
	 * @param userId1
	 *            被访问用户的userId
	 * @param userId2
	 *            访问者的userId
	 * @param token
	 *            访问者的 token
	 * @param ignore
	 *            返回结果将扔掉的条数，例如是 1000， 代表前1000条记录不考虑。 不填默认为 0
	 * @param limit
	 *            返回的条数。默认为 10，可以不传
	 * 
	 * @return 活动参加的活动列表信息
	 */
	@Transactional(readOnly = true)
	ResponseDo getUserJoin(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException;

}
