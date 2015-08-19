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

	/**
	 * 获取活动评论信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @param ignore
	 *            忽略行数
	 * @param limit
	 *            限制获取行数
	 * @return 返回查询活动评论结果信息
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo getActivityComments(String activityId, String userId, String token, Integer ignore, Integer limit)
			throws ApiException;

	/**
	 * 评论活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话token
	 * @param replyUserId
	 *            若不传，代表不回复任何用户
	 * @param comment
	 *            评论内容
	 * @return 返回评论响应结果
	 */
	ResponseDo publishComment(String activityId, String userId, String token, String replyUserId, String comment)
			throws ApiException;

	/**
	 * 关注活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 关注结果
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo subscribeActivity(String activityId, String userId, String token) throws ApiException;

	/**
	 * 申请加入活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param seat
	 *            出几个座位，不出座位传0
	 * @return 返回加入结果信息
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo joinActivity(String activityId, String userId, String token, Integer seat) throws ApiException;

	/**
	 * 同意/拒绝 活动申请
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @param action
	 *            对申请的处理，0代表拒绝， 1代表同意
	 * @return 返回处理结果
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo processApplication(String applicationId, String userId, String token, Integer action)
			throws ApiException;

	/**
	 * 获取车座/成员信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回结果响应对象
	 * @throws ApiException
	 *             业务异常
	 */
	ResponseDo getMemberAndSeatInfo(String activityId, String userId, String token) throws ApiException;

	/**
	 * 立即抢座
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param carId
	 *            车辆ID
	 * @param seatIndex
	 *            座位索引
	 * @return 返回抢座结果
	 * @throws ApiException
	 *             抛出业务异常信息
	 */
	ResponseDo takeSeat(String activityId, String userId, String token, String carId, Integer seatIndex)
			throws ApiException;
}
