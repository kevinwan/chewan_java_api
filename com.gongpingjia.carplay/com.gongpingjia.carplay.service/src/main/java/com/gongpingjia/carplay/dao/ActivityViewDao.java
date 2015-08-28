package com.gongpingjia.carplay.dao;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.po.ActivityView;

/**
 * 获取活动相关的信息
 * 
 * @author licheng
 *
 */
public interface ActivityViewDao {

	/**
	 * 获取最新的活动
	 * 
	 * @param param
	 *            查询参数
	 * @return 返回活动列表
	 */
	List<ActivityView> selectLatestActivities(Map<String, Object> param);

	/**
	 * 获取附件的活动
	 * 
	 * @param param
	 *            查询参数
	 * @return 返回活动列表
	 */
	List<ActivityView> selectNearbyActivities(Map<String, Object> param);

	/**
	 * 获取热门的活动
	 * 
	 * @param param
	 *            查询参数
	 * @return 返回活动列表
	 */
	List<ActivityView> selectHottestActivities(Map<String, Object> param);

	/**
	 * 获取活动相关的成员
	 * 
	 * @param param
	 *            查询参数
	 * @return 活动相关的成员信息
	 */
	List<Map<String, Object>> selectActivityMembers(Map<String, Object> param);

	/**
	 * 获取活动的图片信息
	 * 
	 * @param param
	 *            请求参数
	 * @return 返回查询的活动图片信息
	 */
	List<Map<String, Object>> selectActivityCovers(Map<String, Object> param);

	/**
	 * 获取活动组织者的车辆信息
	 * 
	 * @param userId
	 *            用户ID
	 * @return 查询结果信息
	 */
	Map<String, Object> selectActivityOrganizer(Map<String, Object> param);

	/**
	 * 根据活动ID获取各种车型空余作为信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @return 返回活动的车座空余信息
	 */
	List<Map<String, Object>> selectReservSeatInfoByActivityId(String activityId);

	/**
	 * 根据请求参数，获取活动评论详情
	 * 
	 * @param param
	 *            请求参数
	 * @return 活动评论详情
	 */
	List<Map<String, Object>> selectActivityComments(Map<String, Object> param);

	/**
	 * 获取活动相关的申请信息
	 * 
	 * @param param
	 *            活动参数
	 * @return 返回活动相关的信息集合
	 */
	List<Map<String, Object>> selectActivityApplication(Map<String, String> param);

	/**
	 * 获取活动成员信息列表
	 * 
	 * @param param
	 *            活动参数
	 * @return 返回活动成员信息列表
	 */
	List<Map<String, Object>> selectActivityMemberCarInfo(Map<String, Object> param);

	/**
	 * 获取活动座位预定的车次信息
	 * 
	 * @param param
	 *            活动参数
	 * @return 返回车次信息
	 */
	List<Map<String, Object>> selectSeatReservationCarInfo(Map<String, Object> param);

	/**
	 * 获取活动成员每辆车子 座位预定信息
	 * 
	 * @param param
	 *            活动参数
	 * @return 返回每辆车子 座位预定信息
	 */
	List<Map<String, Object>> selectSeatReservationInfo(Map<String, Object> param);

	/**
	 * 获取活动基本信息以及分享的一些信息
	 * 
	 * @param param
	 *            活动参数
	 * @return 返回活动基本信息
	 */
	Map<String, Object> selectActivityShareInfo(Map<String, Object> param);

	/**
	 * 根据活动类别，获取活动信息，主要为获取"官方活动"接口使用
	 * 
	 * @param param
	 *            参数
	 * @return 返回活动信息
	 */
	List<Map<String, String>> selectActivityByCategory(Map<String, String> param);

}
