package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityViewDao;
import com.gongpingjia.carplay.po.ActivityView;

@Service
public class ActivityViewDaoImpl implements ActivityViewDao {

	@Override
	public List<ActivityView> selectLatestActivities(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectLatestActivities", param);
	}

	@Override
	public List<ActivityView> selectNearbyActivities(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectNearbyActivities", param);
	}

	@Override
	public List<ActivityView> selectHottestActivities(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectHottestActivities", param);
	}

	@Override
	public List<Map<String, Object>> selectActivityMembers(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityMembers", param);
	}

	@Override
	public List<Map<String, Object>> selectActivityCovers(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityCovers", param);
	}

	@Override
	public Map<String, Object> selectActivityOrganizer(Map<String, Object> param) {
		return DASUtil.selectOne(ActivityView.class.getName(), "selectActivityOrganizer", param);
	}

	@Override
	public List<Map<String, Object>> selectReservSeatInfoByActivityId(String activityId) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectReservSeatInfoByActivityId", activityId);
	}

	@Override
	public List<Map<String, Object>> selectActivityComments(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityComments", param);
	}

	@Override
	public List<Map<String, Object>> selectActivityApplication(Map<String, String> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityApplication", param);
	}

	@Override
	public List<Map<String, Object>> selectActivityMemberCarInfo(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityMemberCarInfo", param);
	}

	@Override
	public List<Map<String, Object>> selectSeatReservationCarInfo(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectSeatReservationCarInfo", param);
	}

	@Override
	public List<Map<String, Object>> selectSeatReservationInfo(Map<String, Object> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectSeatReservationInfo", param);
	}

	@Override
	public Map<String, Object> selectActivityShareInfo(Map<String, Object> param) {
		return DASUtil.selectOne(ActivityView.class.getName(), "selectActivityShareInfo", param);
	}

	@Override
	public List<Map<String, String>> selectActivityByCategory(Map<String, String> param) {
		return DASUtil.selectList(ActivityView.class.getName(), "selectActivityByCategory", param);
	}

}
