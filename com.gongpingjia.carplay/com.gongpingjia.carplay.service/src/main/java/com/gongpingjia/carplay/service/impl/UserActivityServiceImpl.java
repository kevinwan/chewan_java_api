package com.gongpingjia.carplay.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.UserActivityService;

@Service
public class UserActivityServiceImpl implements UserActivityService {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterCheck.class);

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private ActivityMemberDao memberDao;
	
	@Autowired
	private ActivityCoverDao coverDao;
	
	@Autowired
	private ActivitySubscriptionDao activitySubscriptionDao;
	
	@Override
	public ResponseDo getUserPost(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException {
		String IMGUrl = PropertiesUtil.getProperty("qiniu.server.url", "")+"asset";
		
		try {
			ParameterCheck.getInstance().checkUserInfo(userId2, token);
		} catch (ApiException e) {
			LOG.error("Token expired or token not correct");
			throw new ApiException("口令已过期，请重新登录获取新口令");
		}
		
		Map<String, Object> param = new HashMap<String, Object>(3, 1);
		param.put("organizer", userId1);
		param.put("ignore", ignore);
		param.put("limit", limit);
		List<LinkedHashMap<String,Object>> activityList=activityDao.selectByOrganizer(param);
		
		List<Map<String,Object>> activityMapList = new ArrayList<>();
		for(Map<String,Object> activity:activityList){
			
			Map<String,Object> map=new LinkedHashMap<>();
			map.put("activityId", activity.get("activityId"));
			map.put("introduction", activity.get("introduction"));
			map.put("location", activity.get("location"));
			map.put("pay",activity.get("pay"));
			
			map.put("publishDate",new SimpleDateFormat("MM.dd").format(DateUtil.getDate((Long)activity.get("publishTime"))));
			map.put("startDate",(Long)activity.get("start"));
			
			param.clear();
			param.put("activityId", activity.get("activityId"));
			param.put("AssetUrl", IMGUrl);
			List<LinkedHashMap<String,String>> membersList=memberDao.selectByActivity(param);
			map.put("members",membersList);
			
			param.clear();
			param.put("activityId", activity.get("activityId"));
			param.put("AssetUrl", IMGUrl);
			List<LinkedHashMap<String,String>> coverList=coverDao.selectByActivity(param);
			
			List<LinkedHashMap<String,String>> coverAllList=new ArrayList<>();
			for(LinkedHashMap<String,String> cover:coverList){
				cover.put("original_pic", cover.get("original_pic"));
				cover.put("thumbnail_pic", cover.get("original_pic")+"?imageView2/1/w/200");
				coverAllList.add(cover);
			}
			map.put("cover",coverAllList);
			
			activityMapList.add(map);
		}
		
		return ResponseDo.buildSuccessResponse(activityMapList);
	}


	@Override
	public ResponseDo getUserSubscribe(String userId1, String userId2, String token, Integer ignore, Integer limit)
			throws ApiException {
		String AssetUrl = PropertiesUtil.getProperty("qiniu.server.url", "")+"asset";
		String gpjIMGUrl = PropertiesUtil.getProperty("gongpingjia.mode.url", "");
		try {
			ParameterCheck.getInstance().checkUserInfo(userId2, token);
		} catch (ApiException e) {
			LOG.error("Token expired or token not correct");
			throw new ApiException("口令已过期，请重新登录获取新口令");
		}
		Map<String, Object> param = new HashMap<String, Object>(5, 1);
		param.put("userId", userId1);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("gpjIMGUrl", gpjIMGUrl);
		param.put("AssetUrl", AssetUrl);
		List<LinkedHashMap<String,Object>> activityList=activitySubscriptionDao.selectByUserId(param);
		
		List<LinkedHashMap<String,Object>> activityMapList = new ArrayList<>();
		
		LinkedHashMap<String,Object> activityLinkMap=new LinkedHashMap<>();
		
		for(Map<String,Object> activity:activityList){
			
			activityLinkMap.put("activityId", activity.get("activityId"));
			
			LinkedHashMap<String,Object> organizermap=new LinkedHashMap<>();
			organizermap.put("userId", activity.get("organizer"));
			organizermap.put("nickname", activity.get("nickname"));
			organizermap.put("gender", activity.get("gender"));
			organizermap.put("age", activity.get("age"));
			organizermap.put("photo", activity.get("photo"));
			organizermap.put("carBrandLogo", activity.get("carBrandLogo"));
			organizermap.put("carModel", activity.get("carModel"));
			organizermap.put("drivingExperience", activity.get("drivingExperience"));
			
			activityLinkMap.put("organizer", organizermap);
			
			activityLinkMap.put("publishTime", activity.get("publishTime"));
			activityLinkMap.put("start", activity.get("start"));
			activityLinkMap.put("introduction", activity.get("introduction"));
			activityLinkMap.put("location", activity.get("location"));
			activityLinkMap.put("type", activity.get("type"));
			activityLinkMap.put("pay", activity.get("pay"));
			
			activityLinkMap.put("totalSeat", activity.get("totalSeat"));
			activityLinkMap.put("holdingSeat", activity.get("holdingSeat"));
			
			activityLinkMap.put("isOrganizer",activity.get("organizer")==userId2?1:0);
			activityLinkMap.put("isMember", 0);
			
			param.clear();
			param.put("activityId", activity.get("activityId"));
			param.put("AssetUrl", AssetUrl);
			List<LinkedHashMap<String,String>> membersLinkMap=memberDao.selectByActivity(param);
			for(LinkedHashMap<String,String> member:membersLinkMap){
				if(member.get("userId")==userId2){
					activityLinkMap.put("isMember", 0);
					break;
				}
			}
			activityLinkMap.put("members", membersLinkMap);
			
			param.clear();
			param.put("activityId", activity.get("activityId"));
			param.put("AssetUrl", AssetUrl);
			List<LinkedHashMap<String,String>> coverList=coverDao.selectByActivity(param);
			
			List<LinkedHashMap<String,String>> coverAllList=new ArrayList<>();
			for(LinkedHashMap<String,String> cover:coverList){
				cover.put("original_pic", cover.get("original_pic"));
				cover.put("thumbnail_pic", cover.get("original_pic")+"?imageView2/1/w/200");
				coverAllList.add(cover);
			}
			activityLinkMap.put("cover",coverAllList);
			
			activityMapList.add(activityLinkMap);
		}

		return ResponseDo.buildSuccessResponse(activityMapList);
	}

}
