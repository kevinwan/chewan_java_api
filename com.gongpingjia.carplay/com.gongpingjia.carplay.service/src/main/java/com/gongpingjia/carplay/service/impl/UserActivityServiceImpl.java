package com.gongpingjia.carplay.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.enums.ApplicationStatus;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.service.UserActivityService;

@Service
public class UserActivityServiceImpl implements UserActivityService {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterChecker.class);

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private ActivityMemberDao memberDao;

	@Autowired
	private ActivityCoverDao coverDao;

	@Autowired
	private ActivitySubscriptionDao activitySubscriptionDao;

	@Autowired
	private ActivityMemberDao activityMemberDao;

	@Autowired
	private ActivityApplicationDao activityApplicationDao;

	@Override
	public ResponseDo getUserPost(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException {
		LOG.debug("Query userId1's activities");
		// 根据发布人查找活动列表
		Map<String, Object> param = new HashMap<String, Object>(3, 1);
		param.put("organizer", userId1);
		param.put("ignore", ignore);
		param.put("limit", limit);
		List<Map<String, Object>> activityList = activityDao.selectByOrganizer(param);

		// 将要返回的list
		List<Map<String, Object>> activityMapList = new ArrayList<>(activityList.size());

		// 遍历活动列表
		for (Map<String, Object> activity : activityList) {

			Map<String, Object> map = new HashMap<String, Object>(10, 1);
			map.put("activityId", activity.get("activityId"));
			map.put("introduction", activity.get("introduction"));
			map.put("location", activity.get("location"));
			map.put("pay", activity.get("pay"));
			map.put("publishDate",
					new SimpleDateFormat("MM.dd").format(DateUtil.getDate((Long) activity.get("publishTime"))));
			map.put("startDate", activity.get("start"));
			map.put("isOver", (Long) activity.get("endTime") < DateUtil.getTime() ? 1 : 0);

			map.putAll(membersAndCovers((String) activity.get("activityId"), userId2));

			activityMapList.add(map);
		}

		return ResponseDo.buildSuccessResponse(activityMapList);
	}

	@Override
	public ResponseDo getUserSubscribe(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException {
		LOG.debug("get user subscribe info");
		Map<String, Object> param = new HashMap<String, Object>(5, 1);
		param.put("userId", userId1);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("gpjIMGUrl", PropertiesUtil.getProperty("gongpingjia.brand.logo.url", ""));
		param.put("assetUrl", PropertiesUtil.getProperty("qiniu.server.url", ""));
		List<Map<String, Object>> activityList = activitySubscriptionDao.selectByUserId(param);

		// 将要返回的list
		List<Map<String, Object>> activityMapList = new ArrayList<Map<String, Object>>(activityList.size());

		// 遍历活动列表
		for (Map<String, Object> activity : activityList) {

			Map<String, Object> map = new HashMap<String, Object>(18, 1);

			map.put("activityId", activity.get("activityId"));

			// 存入发布人的信息
			Map<String, Object> organizermap = new HashMap<String, Object>(8, 1);
			organizermap.put("userId", activity.get("organizer"));
			organizermap.put("nickname", activity.get("nickname"));
			organizermap.put("gender", activity.get("gender"));
			organizermap.put("age", activity.get("age"));
			organizermap.put("photo", activity.get("photo"));
			organizermap.put("carBrandLogo", activity.get("carBrandLogo"));
			organizermap.put("carModel", activity.get("carModel"));
			organizermap.put("drivingExperience", activity.get("drivingExperience"));
			map.put("organizer", organizermap);

			map.put("publishTime", activity.get("publishTime"));
			map.put("start", (Long) activity.get("start") != 0 ? activity.get("start") : "不确定");
			map.put("isOver", (Long) activity.get("endTime") < DateUtil.getTime() ? 1 : 0);
			map.put("introduction", activity.get("introduction"));
			map.put("location", activity.get("location"));
			map.put("type", activity.get("type"));
			map.put("pay", activity.get("pay"));
			map.put("totalSeat", activity.get("totalSeat"));
			map.put("holdingSeat", activity.get("holdingSeat"));
			map.put("isOrganizer", activity.get("organizer").equals(userId2) ? 1 : 0);

			map.putAll(membersAndCovers((String) activity.get("activityId"), userId2));

			activityMapList.add(map);
		}

		return ResponseDo.buildSuccessResponse(activityMapList);
	}

	@Override
	public ResponseDo getUserJoin(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException {
		LOG.debug("Begin query user activities");
		Map<String, Object> param = new HashMap<String, Object>(5, 1);
		param.put("userId", userId1);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("gpjIMGUrl", PropertiesUtil.getProperty("gongpingjia.brand.logo.url", ""));
		param.put("assetUrl", PropertiesUtil.getProperty("qiniu.server.url", ""));
		List<Map<String, Object>> activityList = activityMemberDao.selectByUserId(param);

		List<Map<String, Object>> activityMapList = new ArrayList<>();

		for (Map<String, Object> activity : activityList) {
			Map<String, Object> map = new HashMap<String, Object>(18, 1);

			map.put("activityId", activity.get("activityId"));

			map.put("publishTime", activity.get("publishTime"));
			map.put("start", (Long) activity.get("start") != 0 ? activity.get("start") : "不确定"); // 接口文档
			map.put("isOver", (Long) activity.get("endTime") < DateUtil.getTime() ? 1 : 0);
			map.put("introduction", activity.get("introduction"));
			map.put("location", activity.get("location"));
			map.put("type", activity.get("type"));
			map.put("pay", activity.get("pay"));

			Map<String, Object> organizermap = new HashMap<String, Object>(8, 1);
			organizermap.put("userId", activity.get("organizer"));
			organizermap.put("nickname", activity.get("nickname"));
			organizermap.put("gender", activity.get("gender"));
			organizermap.put("age", activity.get("age"));
			organizermap.put("photo", activity.get("photo"));
			organizermap.put("carBrandLogo", activity.get("carBrandLogo"));
			organizermap.put("carModel", activity.get("carModel"));
			organizermap.put("drivingExperience", activity.get("drivingExperience"));
			map.put("organizer", organizermap);
			map.put("totalSeat", activity.get("totalSeat"));
			map.put("holdingSeat", activity.get("holdingSeat"));
			map.put("isOrganizer", activity.get("organizer").equals(userId2) ? 1 : 0);

			map.putAll(membersAndCovers((String) activity.get("activityId"), userId2));
			activityMapList.add(map);
		}

		return ResponseDo.buildSuccessResponse(activityMapList);

	}

	/**
	 * 查看是否是活动成员
	 * 
	 * @param activityId
	 *            活动id
	 * 
	 * @param membersListMap
	 *            此活动的成员列表
	 * 
	 * @param userId
	 *            查看的这个ID
	 * 
	 * @return 0代表不是成员也没有提交申请，1代表已经是成员，2代表正在申请成为成员
	 */
	private int isMember(String activityId, List<Map<String, String>> membersListMap, String userId)
			throws ApiException {
		int isMember = 0;

		for (Map<String, String> member : membersListMap) {
			if (member.get("userId").equals(userId)) {
				isMember = 1;
				break;
			}
		}
		if (isMember == 0) {
			Map<String, Object> param = new HashMap<String, Object>(5, 1);
			param.put("activityId", activityId);
			param.put("userId", userId);
			param.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
			List<Map<String, Long>> rows = activityApplicationDao.selectByCountOfActivityUserAndStatus(param);
			if (rows.size() == 0) {
				LOG.warn("Fail to get application count");
				throw new ApiException("未能成功获取申请信息");
			}
			isMember = rows.get(0).get("count") > 0 ? 2 : 0;
		}

		return isMember;
	}

	/**
	 * 查找活动成员与封面
	 * 
	 * @param activityId
	 *            活动ID
	 * 
	 * @param userId2
	 *            访问者的Id
	 * 
	 * @return 活动成员与活动封面的map结果
	 */
	private Map<String, Object> membersAndCovers(String activityId, String userId2) throws ApiException {

		Map<String, Object> map = new HashMap<String, Object>(3, 1);

		// 根据活动，查找活动成员
		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("activityId", activityId);
		param.put("assetUrl", PropertiesUtil.getProperty("qiniu.server.url", ""));
		List<Map<String, String>> membersList = memberDao.selectByActivity(param);

		map.put("members", membersList);

		map.put("isMember", isMember(activityId, membersList, userId2));
		// 根据活动，查找活动封面
		List<Map<String, String>> coverList = coverDao.selectByActivity(param);

		// 处理活动封面
		List<Map<String, String>> coverAllList = new ArrayList<>(coverList.size());
		for (Map<String, String> cover : coverList) {
			// cover.put("original_pic", cover.get("original_pic"));
			cover.put("thumbnail_pic", cover.get("original_pic") + CommonUtil.getActivityPhotoPostfix());
			coverAllList.add(cover);
		}
		map.put("cover", coverAllList);

		return map;
	}

}
