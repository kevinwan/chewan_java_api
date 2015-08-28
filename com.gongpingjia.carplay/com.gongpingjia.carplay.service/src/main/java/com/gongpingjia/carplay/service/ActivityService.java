package com.gongpingjia.carplay.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	ResponseDo getAvailableSeats(String userId, String token, String activityId) throws ApiException;

	/**
	 * 注册用户信息
	 * 
	 * @param request
	 *            请求参数
	 * @return 返回响应结果对象
	 * @throws ApiException
	 *             业务异常处理
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo registerActivity(HttpServletRequest request) throws ApiException;

	/**
	 * 检查注册的请求参数是否满足注册的要求, 修改活动的时候同样适用 <br/>
	 * 新增加这个接口主要是考虑到事务，如果参数校验也放入到事务中会影响性能
	 * 
	 * @param request
	 *            请求参数集合
	 * @throws ApiException
	 *             参数校验不合格抛出业务异常
	 */
	void checkRegisterActivityParam(HttpServletRequest request) throws ApiException;

	/**
	 * 根据参数查询活动信息
	 * 
	 * @param request
	 *            请求参数
	 * @return 响应结果
	 * @throws ApiException
	 *             业务异常处理
	 */
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(rollbackFor = Exception.class)
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
	@Transactional(readOnly = true)
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
	@Transactional(rollbackFor = Exception.class)
	ResponseDo takeSeat(String activityId, String userId, String token, String carId, Integer seatIndex)
			throws ApiException;

	/**
	 * 拉下座位
	 * 
	 * @param activityId
	 *            活动ID
	 * @param member
	 *            活动中被拉下来的成员
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @return 返回被拉下来的结果
	 * @throws ApiException
	 *             业务异常
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo returnSeat(String activityId, String member, String userId, String token) throws ApiException;

	/**
	 * 移除成员
	 * 
	 * @param activityId
	 *            活动ID
	 * @param member
	 *            活动中被移除的成员
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @return 返回被移除的结果
	 * @throws 业务异常
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo removeMember(String activityId, String member, String userId, String token) throws ApiException;

	/**
	 * 退出活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @return 返回退出结果信息
	 * @throws ApiException
	 *             业务异常
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo quitActivity(String activityId, String userId, String token) throws ApiException;

	/**
	 * 编辑活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话token
	 * @param request
	 *            请求参数
	 * @return 返回响应结果信息
	 * @throws ApiException
	 *             业务异常
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo alterActivityInfo(String activityId, String userId, String token, HttpServletRequest request)
			throws ApiException;

	/**
	 * 取消关注活动
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @return 返回结果对象
	 * @throws ApiException
	 *             业务异常
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo unsubscribeActivity(String activityId, String userId, String token) throws ApiException;

}
