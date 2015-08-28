package com.gongpingjia.carplay.service;

import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	ResponseDo getMessageCount(String userId) throws ApiException;

	/**
	 * 2.42 获取消息列表<br/>
	 * 这里涉及到变更消息的状态由未读改为已读，状态变更涉及update操作，需要事务管理
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
	@Transactional(rollbackFor = Exception.class)
	ResponseDo getMessageList(String userId, String type, int ignore, int limit) throws ApiException;

	/**
	 * 2.44 提交反馈信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param content
	 *            反馈信息
	 * 
	 * @param photos
	 *            反馈图片id
	 * 
	 * @return 提交成功
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo submitFeedback(String userId, String content, String[] photos) throws ApiException;

	/**
	 * 2.47批量删除消息
	 * 
	 * @param userId
	 *            用户Id
	 * 
	 * @param messages
	 *            消息ID
	 *
	 * @return 删除成功
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo removeMessages(String userId, String[] messages) throws ApiException;

	/**
	 * 2.48批量删除评论
	 * 
	 * @param userId
	 *            用户Id
	 * 
	 * @param comments
	 *            评论ID
	 *
	 * @return 删除成功
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = Exception.class)
	ResponseDo removeComments(String userId, String[] comments) throws ApiException;

}
