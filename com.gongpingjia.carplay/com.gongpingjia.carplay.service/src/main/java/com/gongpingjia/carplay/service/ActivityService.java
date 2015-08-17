package com.gongpingjia.carplay.service;

import javax.servlet.http.HttpServletRequest;

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

	/**
	 * 注册用户信息
	 * 
	 * @param request
	 *            请求参数
	 * @return 返回响应结果对象
	 * @throws ApiException
	 *             业务异常处理
	 */
	ResponseDo registerActivity(HttpServletRequest request) throws ApiException;

	/**
	 * 根据参数查询活动信息
	 * 
	 * @param request
	 *            请求参数
	 * @return 响应结果
	 * @throws ApiException
	 *             业务异常处理
	 */
	ResponseDo getActivityList(HttpServletRequest request) throws ApiException;

	/**
	 * 获取活动详情
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回响应结果信息
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo getActivityInfo(String activityId, String userId, String token) throws ApiException;
}
