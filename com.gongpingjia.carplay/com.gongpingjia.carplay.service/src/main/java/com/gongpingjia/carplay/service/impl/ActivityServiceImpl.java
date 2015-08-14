package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.Activity;
import com.gongpingjia.carplay.po.ActivityCover;
import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Autowired
	private CarDao carDao;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private ActivityCoverDao coverDao;

	@Autowired
	private ActivityMemberDao memberDao;

	@Override
	public ResponseDo getAvailableSeats(String userId, String token) throws ApiException {

		ParameterCheck.getInstance().checkUserInfo(userId, token);

		LOG.debug("Query car list by userId");
		Car car = carDao.selectByUserId(userId);

		Map<String, Object> result = new HashMap<String, Object>(3, 1);

		if (car == null) {
			LOG.info("User has not authicate in system");
			result.put("maxValue", getMaxCarSeat(null));
			result.put("isAuthenticated", 0);
		} else {
			LOG.info("User has already authicated");
			result.put("maxValue", getMaxCarSeat(car));
			result.put("isAuthenticated", 1);
		}

		return ResponseDo.buildSuccessResponse(result);
	}

	/**
	 * 获取该车提供的最大座位数
	 * 
	 * @param car
	 *            车
	 * @return 返回可提供的最大座位数
	 */
	private int getMaxCarSeat(Car car) {
		int config = PropertiesUtil.getProperty("user.unauth.car.max.seats", 2);
		if (car != null && car.getSeat() > 1) {
			return car.getSeat() - 1;
		}
		return config;
	}

	/**
	 * 获取最小的Car的提供座位数
	 * 
	 * @return 最小可提供的座位数
	 */
	private int getMinCarSeat() {
		return PropertiesUtil.getProperty("user.unauth.car.min.seats", 1);
	}

	@Override
	public ResponseDo registerActivity(HttpServletRequest request) throws ApiException {

		String userId = request.getParameter("userId");

		checkActivityParam(request);
		checkActivityCover(request);
		checkActivityAddress(request);
		checkActivitySeat(request, userId);

		String activityId = CodeGenerator.generatorId();
		saveActivity(request, activityId);

		saveActivityCovers(request, activityId);

		saveActivityMember(userId, activityId);

		// seat_reservation
		
		
		
		LOG.debug("Save activity reservation");

		return null;
	}

	private void saveActivityMember(String userId, String activityId) {
		LOG.debug("Save activity member");
		ActivityMember member = new ActivityMember();
		member.setActivityid(activityId);
		member.setUserid(userId);
		member.setJointime(DateUtil.getTime());
		memberDao.insert(member);
	}

	private void saveActivityCovers(HttpServletRequest request, String activityId) {
		String[] covers = request.getParameterValues("cover");
		List<ActivityCover> activityCovers = new ArrayList<ActivityCover>();
		for (String coverId : covers) {
			ActivityCover activityCover = new ActivityCover();
			activityCover.setId(CodeGenerator.generatorId());
			activityCover.setActivityid(activityId);
			activityCover.setUrl(MessageFormat.format(Constants.COVER_PHOTO_KEY, coverId));
			activityCovers.add(activityCover);
		}
		coverDao.insert(activityCovers);
	}

	/**
	 * 检查座位数是否正确
	 * 
	 * @param request
	 *            请求参数对象
	 * @param userId
	 *            用户ID
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkActivitySeat(HttpServletRequest request, String userId) throws ApiException {
		LOG.debug("Check request seats is in range or not");
		Car car = carDao.selectByUserId(userId);
		Integer seat = Integer.valueOf(request.getParameter("seat"));
		if (seat > getMaxCarSeat(car) || seat < getMinCarSeat()) {
			LOG.error("The offered car seat is override range");
			throw new ApiException("提供的空座数超出范围");
		}
	}

	/**
	 * 检查活动的省市区，地址信息是否为空
	 * 
	 * @param request
	 *            请求参数信息
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkActivityAddress(HttpServletRequest request) throws ApiException {
		LOG.debug("Check activity location and address");
		// 要么省市区全部都不为空，要么地址不为空，当二者都为空的时候需要报错
		if ((StringUtils.isEmpty(request.getParameter("province")) || StringUtils.isEmpty(request.getParameter("city")) || StringUtils
				.isEmpty(request.getParameter("district"))) && StringUtils.isEmpty(request.getParameter("address"))) {
			LOG.error("Province, city and district cannot be empty, or the same with address");
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查活动的图片信息
	 * 
	 * @param request
	 *            请求参数信息
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkActivityCover(HttpServletRequest request) throws ApiException {
		LOG.debug("Check activity cover is exist or not");
		String[] covers = request.getParameterValues("cover");
		if (covers == null || covers.length < 1
				|| covers.length > PropertiesUtil.getProperty("user.album.photo.max.count", 9)) {
			LOG.error("Input covers length is {}, out of the range", covers.length);
			throw new ApiException("输入参数有误");
		}

		for (String coverId : covers) {
			if (!photoService.isExist(MessageFormat.format(Constants.COVER_PHOTO_KEY, coverId))) {
				LOG.error("Activity cover is not exist");
				throw new ApiException("输入参数有误");
			}
		}
	}

	/**
	 * 检查创建活动的活动参数信息
	 * 
	 * @param request
	 *            请求参数
	 * @throws ApiException
	 *             业务异常信息
	 */
	private void checkActivityParam(HttpServletRequest request) throws ApiException {
		LOG.debug("Check input parameters");
		ParameterCheck check = ParameterCheck.getInstance();
		String userId = request.getParameter("userId");
		String token = request.getParameter("token");
		check.checkUserInfo(userId, token);
		check.checkParameterEmpty("type", request.getParameter("type"));
		check.checkParameterEmpty("introduction", request.getParameter("introduction"));
		check.checkParameterEmpty("location", request.getParameter("location"));
		check.checkParameterEmpty("pay", request.getParameter("pay"));
		check.checkParameterLongType("start", request.getParameter("start"));
	}

	/**
	 * 根据Request对象构建Activity对象
	 * 
	 * @param request
	 *            请求对象
	 * @param activityId
	 * @return 返回活动对象
	 */
	private void saveActivity(HttpServletRequest request, String activityId) {
		LOG.debug("Build activity by request parameters");
		Activity activity = new Activity();
		activity.setId(activityId);
		activity.setType(request.getParameter("type"));
		activity.setDescription(request.getParameter("introduction"));
		activity.setLocation(request.getParameter("location"));
		activity.setProvince(request.getParameter("province"));
		activity.setCity(request.getParameter("city"));
		activity.setDistrict(request.getParameter("district"));
		activity.setAddress(request.getParameter("address"));
		activity.setInitialseat(Integer.valueOf(request.getParameter("seat")));

		Long current = DateUtil.getTime();
		Long start = Long.valueOf(request.getParameter("start"));
		if (start < current) {
			activity.setStart(current);
		}

		Long end = current;
		try {
			end = Long.valueOf(request.getParameter("end"));
		} catch (NumberFormatException e) {
			LOG.warn("Input parameter end is not Long type param, use default, request end: {}",
					request.getParameter("end"));
			LOG.error(e.getMessage(), e);
		}

		if (end <= current) {
			int config = PropertiesUtil.getProperty("activity.endtime.default.days", 7);
			end = DateUtil.addTime(DateUtil.getDate(), Calendar.DAY_OF_MONTH, config);
		}
		activity.setEnd(end);

		int length = PropertiesUtil.getProperty("activity.title.length.limit", 7);
		activity.setTitle((activity.getDescription().length() <= length) ? activity.getDescription() : activity
				.getDescription().substring(0, length));

		LOG.debug("Build activity province, city, district if they are empty");
		User user = userDao.selectByPrimaryKey(request.getParameter("userId"));
		if (StringUtils.isEmpty(activity.getProvince()) || StringUtils.isEmpty(activity.getCity())
				|| StringUtils.isEmpty(activity.getDistrict())) {
			activity.setProvince(user.getProvince());
			activity.setCity(user.getCity());
			activity.setDistrict(user.getDistrict());
		}

		LOG.debug("Save activity");
		activityDao.insert(activity);
	}

}
