package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.gongpingjia.carplay.common.enums.ApplicationStatus;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.common.util.TypeConverUtil;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.dao.ActivityViewDao;
import com.gongpingjia.carplay.dao.ActivityViewHistoryDao;
import com.gongpingjia.carplay.dao.ApplicationChangeHistoryDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.SeatReservationDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.Activity;
import com.gongpingjia.carplay.po.ActivityApplication;
import com.gongpingjia.carplay.po.ActivityCover;
import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.ActivityView;
import com.gongpingjia.carplay.po.ActivityViewHistory;
import com.gongpingjia.carplay.po.ApplicationChangeHistory;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.SeatReservation;
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

	@Autowired
	private SeatReservationDao seatReservDao;

	@Autowired
	private ActivityApplicationDao applicationDao;

	@Autowired
	private ApplicationChangeHistoryDao historyDao;

	@Autowired
	private ActivityViewDao activityViewDao;

	@Autowired
	private ActivityViewHistoryDao viewHistoryDao;

	@Autowired
	private ActivitySubscriptionDao subscriptionDao;

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

		// 生成公共的部分参数
		String activityId = CodeGenerator.generatorId();
		Long current = DateUtil.getTime();

		Activity activity = saveActivity(request, activityId, current);

		saveActivityCovers(request, activityId, current);

		saveActivityMember(userId, activityId, current);

		saveSeatReservation(userId, activityId, activity.getInitialseat(), current);

		saveActivityApplication(request.getParameter("seat"), activityId, userId, current);

		Map<String, String> data = buildShareData(userId, activity);

		return ResponseDo.buildSuccessResponse(data);
	}

	private Map<String, String> buildShareData(String userId, Activity activity) {
		User user = userDao.selectByPrimaryKey(userId);
		Map<String, String> data = new HashMap<String, String>(5, 1);
		data.put("activityId", activity.getId());
		data.put("shareUrl",
				MessageFormat.format(PropertiesUtil.getProperty("activity.share.url", ""), activity.getId()));
		data.put("shareTitle", MessageFormat.format(PropertiesUtil.getProperty("activity.share.title", ""),
				user.getNickname(), activity.getTitle()));
		String date = DateUtil.format(activity.getStart(), Constants.ACTIVITY_SHARE_DATE_FORMAT);
		data.put(
				"shareContent",
				MessageFormat.format(PropertiesUtil.getProperty("activity.share.content", ""),
						Arrays.asList(date, activity.getLocation(), activity.getPaymenttype())));
		return data;
	}

	/**
	 * 保存活动
	 * 
	 * @param request
	 * @param activityId
	 */
	private void saveActivityApplication(String seat, String activityId, String userId, Long current) {
		LOG.debug("save activity application and application change history");
		ActivityApplication application = new ActivityApplication();
		application.setActivityid(activityId);
		application.setCreatetime(current);
		application.setId(CodeGenerator.generatorId());
		application.setSeat(Integer.valueOf(seat));
		application.setUserid(userId);
		application.setStatus(ApplicationStatus.PENDING_PROCESSED.getName());
		applicationDao.insert(application);

		ApplicationChangeHistory history = new ApplicationChangeHistory();
		history.setApplicationid(application.getId());
		history.setId(CodeGenerator.generatorId());
		history.setStatus(ApplicationStatus.PENDING_PROCESSED.getName());
		history.setTimestamp(current);
		historyDao.insert(history);
	}

	/**
	 * 保存座位预定信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param activityId
	 *            活动ID
	 * @param activity
	 * @return
	 */
	private int saveSeatReservation(String userId, String activityId, int seat, Long current) {
		LOG.debug("Save activity seat reservation");

		Car car = carDao.selectByUserId(userId);
		String carId = (car == null) ? null : car.getId();

		List<SeatReservation> reservationList = new ArrayList<SeatReservation>();
		for (int i = 0; i < seat; i++) {
			SeatReservation seatReserve = new SeatReservation();
			seatReserve.setId(CodeGenerator.generatorId());
			seatReserve.setSeatindex(i);
			seatReserve.setActivityid(activityId);
			if (i == 0) {
				seatReserve.setUserid(userId);
				seatReserve.setBooktime(current);
			}
			seatReserve.setCarid(carId);
			seatReserve.setCreatetime(current);
			reservationList.add(seatReserve);
		}
		return seatReservDao.insert(reservationList);
	}

	private ActivityMember saveActivityMember(String userId, String activityId, Long current) {
		LOG.debug("Save activity member");
		ActivityMember member = new ActivityMember();
		member.setActivityid(activityId);
		member.setUserid(userId);
		member.setJointime(current);
		memberDao.insert(member);

		return member;
	}

	private List<ActivityCover> saveActivityCovers(HttpServletRequest request, String activityId, Long current) {
		String[] covers = request.getParameterValues("cover");
		List<ActivityCover> activityCovers = new ArrayList<ActivityCover>();
		for (String coverId : covers) {
			ActivityCover activityCover = new ActivityCover();
			activityCover.setId(CodeGenerator.generatorId());
			activityCover.setActivityid(activityId);
			activityCover.setUrl(MessageFormat.format(Constants.COVER_PHOTO_KEY, coverId));
			activityCover.setUploadtime(current);
			activityCovers.add(activityCover);
		}
		coverDao.insert(activityCovers);
		return activityCovers;
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

		checkActivityCover(request);
		checkActivityAddress(request);
		checkActivitySeat(request, userId);
	}

	/**
	 * 根据Request对象构建Activity对象
	 * 
	 * @param request
	 *            请求对象
	 * @param activityId
	 * @param current
	 *            当前时间
	 * @return 返回活动对象
	 * @throws ApiException
	 */
	private Activity saveActivity(HttpServletRequest request, String activityId, Long current) throws ApiException {
		LOG.debug("Build activity by request parameters");
		Activity activity = new Activity();
		activity.setId(activityId);
		activity.setOrganizer(request.getParameter("userId"));
		activity.setType(request.getParameter("type"));
		activity.setDescription(request.getParameter("introduction"));
		activity.setLocation(request.getParameter("location"));
		activity.setProvince(request.getParameter("province"));
		activity.setCity(request.getParameter("city"));
		activity.setDistrict(request.getParameter("district"));
		activity.setAddress(request.getParameter("address"));
		activity.setPaymenttype(request.getParameter("pay"));
		activity.setInitialseat(TypeConverUtil.convertToInteger("seat", request.getParameter("seat"), false));
		activity.setLatitude(TypeConverUtil.convertToDouble("latitude", request.getParameter("latitude"), false));
		activity.setLongitude(TypeConverUtil.convertToDouble("longitude", request.getParameter("longitude"), false));

		// 计算start end endTime
		activity.setStart(computeStart(request.getParameter("start"), current));
		activity.setEnd(computeEnd(activity.getStart(), request.getParameter("end")));
		activity.setEndtime(computeEndTime(activity.getStart(), activity.getEnd()));

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

		activity.setCreatetime(current);
		activity.setLastmodifiedtime(current);
		LOG.debug("Save activity");
		activityDao.insert(activity);

		return activity;
	}

	/**
	 * 计算开始时间，当开始时间字符串小于当前时间时，取当前时间
	 * 
	 * @param startString
	 *            开始时间请求字符串
	 * @param current
	 *            当前时间
	 * @return 返回计算结果
	 * @throws ApiException
	 */
	private Long computeStart(String startString, Long current) throws ApiException {
		Long start = TypeConverUtil.convertToLong("start", startString, false);
		if (start < current) {
			return current;
		}
		return start;
	}

	/**
	 * 计算结束时间，如果结束时间没有输入则为null， 如果end时间小于start时间， 返回null
	 * 
	 * @param start
	 *            起始时间
	 * @param endString
	 *            结束时间字符串
	 * @return 返回计算的end时间
	 * @throws ApiException
	 */
	private Long computeEnd(Long start, String endString) throws ApiException {
		// 计算活动截止时间
		Long end = TypeConverUtil.convertToLong("start", endString, false);
		if (end != null && end <= start) {
			end = null;
		}
		return end;
	}

	/**
	 * 根据起始时间和结束时间计算系统控制参数endTime时间
	 * 
	 * @param start
	 *            起始时间
	 * @param end
	 *            结束时间
	 * @return endTime时间
	 */
	private Long computeEndTime(Long start, Long end) {
		if (end == null) {
			int config = PropertiesUtil.getProperty("activity.expired.default.days", 8);
			return DateUtil.addTime(DateUtil.getDate(start), Calendar.DAY_OF_MONTH, config);
		}
		return end;
	}

	@Override
	public ResponseDo getActivityList(HttpServletRequest request) throws ApiException {

		checkCommonQueryParams(request);

		Map<String, Object> param = buildQueryParam(request);

		String key = request.getParameter("key");
		LOG.debug("query activities from database");
		List<ActivityView> activityList = new ArrayList<ActivityView>(0);
		if (Constants.ACTIVITY_KEY_HOTTEST.equals(key)) {
			activityList = activityViewDao.selectHottestActivities(param);
		} else if (Constants.ACTIVITY_KEY_NEARBY.equals(key)) {
			activityList = activityViewDao.selectNearbyActivities(param);
		} else if (Constants.ACTIVITY_KEY_LATEST.equals(key)) {
			activityList = activityViewDao.selectLatestActivities(param);
		}

		LOG.debug("Build response by activities");

		List<Map<String, Object>> data = buildActivitiesData(activityList, request.getParameter("userId"));

		LOG.debug("Return build data");
		return ResponseDo.buildSuccessResponse(data);
	}

	/**
	 * 构造查询返回的数据对象
	 * 
	 * @param activityViewList
	 *            查询的活动列表
	 * @param userId
	 *            请求中的用户ID
	 * @return 返回数据对象data对象
	 */
	private List<Map<String, Object>> buildActivitiesData(List<ActivityView> activityViewList, String userId) {
		LOG.debug("build response data by input activity list");
		String photoPostfix = CommonUtil.getActivityPhotoPostfix();

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(activityViewList.size());
		for (ActivityView item : activityViewList) {
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("activityId", item.getActivityId());
			record.put("publishTime", item.getPublishTime());
			record.put("start", item.getStart());
			record.put("introduction", item.getIntroduction());
			record.put("location", item.getLocation());
			record.put("type", item.getType());
			record.put("pay", item.getPay());

			Map<String, Object> organizer = new HashMap<String, Object>();
			organizer.put("userId", item.getUserId());
			organizer.put("nickname", item.getNickname());
			organizer.put("gender", item.getGender());
			organizer.put("age", item.getAge());
			organizer.put("photo", item.getPhoto() + photoPostfix);
			organizer.put("carBrandLogo", item.getCarBrandLogo());
			organizer.put("carModel", item.getCarModel());
			organizer.put("drivingExperience", item.getDrivingExperience());

			record.put("organizer", organizer);

			record.put("totalSeat", item.getTotalSeat());

			Map<String, Object> param = buildCommonQueryParam(item.getActivityId(), userId);

			List<Map<String, Object>> members = activityViewDao.selectActivityMembers(param);
			record.put("members", members);
			record.put("cover", buildActivityCovers(param));

			record.put("isOrganizer", isOrganizer(userId, item.getUserId()));
			record.put("isMember", isMember(userId, item.getActivityId(), members));
			record.put("isOver", isActivityOver(item.getEndtime()));

			result.add(record);
		}

		return result;
	}

	/**
	 * 构造与活动相关的查询条件参数
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * 
	 * @return 返回构造的查询参数,如果存在多余的参数可以忽略
	 */
	private Map<String, Object> buildCommonQueryParam(String activityId, String userId) {
		Map<String, Object> param = new HashMap<String, Object>(6, 1);
		param.put("assetUrl", CommonUtil.getPhotoServer());
		param.put("photoPostfix", CommonUtil.getActivityPhotoPostfix());
		param.put("gpjImagePrefix", CommonUtil.getGPJImagePrefix());
		param.put("activityId", activityId);
		param.put("userId", userId);

		return param;
	}

	/**
	 * 判断当前用户是否为活动的组织者
	 * 
	 * @param userId
	 *            用户ID
	 * @param activityUserId
	 *            活动创建者的用户ID
	 * @return 是活动创建者返回1， 不是活动创建者返回0
	 */
	private String isOrganizer(String userId, String activityUserId) {
		String isOrganizer = "0";
		if (activityUserId.equals(userId)) {
			isOrganizer = "1";
		}
		return isOrganizer;
	}

	/**
	 * 判断当前用户是否为活动成员
	 * 
	 * @param userId
	 *            用户ID
	 * @param activityId
	 *            活动ID
	 * @param members
	 *            活动所有成员
	 * @return 1代表已经是成员，2代表正在申请成为成员，0代表不是成员也没有提交申请
	 */
	private String isMember(String userId, String activityId, List<Map<String, Object>> members) {
		String isMember = "0";
		for (Map<String, Object> member : members) {
			if (member.containsKey(userId)) {
				isMember = "1";
			}
		}

		Map<String, Object> appParam = new HashMap<String, Object>();
		appParam.put("activityId", activityId);
		appParam.put("userId", userId);
		appParam.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		List<ActivityApplication> appList = applicationDao.selectByParam(appParam);

		if (!appList.isEmpty()) {
			// 在申请待处理中
			isMember = "2";
		}
		return isMember;
	}

	/**
	 * 根据请求参数 构造查询参数Map对象
	 * 
	 * @param request
	 *            请求参数
	 * @return 返回构造好的参数Map
	 * @throws ApiException
	 */
	private Map<String, Object> buildQueryParam(HttpServletRequest request) throws ApiException {
		LOG.debug("build query param map by query parameters");
		Map<String, Object> param = new HashMap<String, Object>(16, 1);
		// 公共参数
		param.put("gpjImagePrefix", CommonUtil.getGPJImagePrefix());
		param.put("assetUrl", CommonUtil.getPhotoServer());
		param.put("timestamp", DateUtil.getTime());

		// 请求参数
		param.put("userId", request.getParameter("userId"));
		param.put("longitude", request.getParameter("longitude"));
		param.put("latitude", request.getParameter("latitude"));
		param.put("city", request.getParameter("city"));
		param.put("province", request.getParameter("province"));
		param.put("district", request.getParameter("district"));
		param.put("type", request.getParameter("type"));
		param.put("gender", request.getParameter("gender"));
		param.put("authenticate", request.getParameter("authenticate"));
		param.put("carLevel", request.getParameter("carLevel"));
		Integer ignore = TypeConverUtil.convertToInteger("ignore", request.getParameter("ignore"), false);
		if (ignore == null) {
			ignore = 0;
		}
		param.put("ignore", ignore);
		Integer limit = TypeConverUtil.convertToInteger("limit", request.getParameter("limit"), false);
		if (limit == null) {
			limit = 20;
		}
		param.put("limit", limit);

		return param;
	}

	/**
	 * 获取活动信息，校验基本参数（注意：这里没有校验token是否有效）
	 * 
	 * @param request
	 *            请求参数
	 * @throws ApiException
	 *             业务异常，参数错误
	 */
	private void checkCommonQueryParams(HttpServletRequest request) throws ApiException {
		LOG.debug("Begin check query parameters");
		String key = request.getParameter("key");
		String userId = request.getParameter("userId");
		String token = request.getParameter("token");
		String longitude = request.getParameter("longitude");
		String latitude = request.getParameter("latitude");

		if (!CommonUtil.isUUID(userId)) {
			LOG.error("Input parameter userId: {} is not UUID", userId);
			throw new ApiException("输入参数有误");
		}

		if (!CommonUtil.isUUID(token)) {
			LOG.error("Input parameter token: {} is not UUID", token);
			throw new ApiException("输入参数有误");
		}

		if (!Constants.ACTIVITY_KEY_LIST.contains(key)) {
			LOG.error("Input parameter key: {} error", key);
			throw new ApiException("输入参数有误");
		}

		if (Constants.ACTIVITY_KEY_NEARBY.equals(key) && (longitude == null || latitude == null)) {
			LOG.error("Input parameter is nearby, but with no longitude or latitude");
			throw new ApiException("未能获取您的位置信息");
		}

		if (Constants.ACTIVITY_KEY_NEARBY.equals(key)) {
			// 如果是查找附近的活动，需要输入经度和纬度，且为Double类型
			TypeConverUtil.convertToDouble("longitude", longitude, true);
			TypeConverUtil.convertToDouble("latitude", latitude, true);
		}
	}

	@Override
	public ResponseDo getActivityInfo(String activityId, String userId, String token) throws ApiException {

		LOG.debug("Begin check input parameters");
		if (!CommonUtil.isUUID(activityId)) {
			LOG.warn("Input parameter activityId is not uuid, activityId:{}", activityId);
			throw new ApiException("参数错误");
		}

		ParameterCheck.getInstance().checkUserInfo(userId, token);

		// 查询活动数据
		Map<String, Object> data = buildActivityInfoData(activityId, userId);

		LOG.debug("Record activity view history log");
		saveActivityViewHistory(activityId, userId);

		// 查询活动相关的数据
		return ResponseDo.buildSuccessResponse(data);
	}

	/**
	 * 保存活动查看历史记录
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 */
	private void saveActivityViewHistory(String activityId, String userId) {
		ActivityViewHistory viewHistory = new ActivityViewHistory();
		viewHistory.setActivityid(activityId);
		viewHistory.setId(CodeGenerator.generatorId());
		viewHistory.setUserid(userId);
		viewHistory.setViewtime(DateUtil.getTime());
		viewHistoryDao.insert(viewHistory);
	}

	/**
	 * 构造返回的活动详情的信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @return 返回构造的数据信息
	 */
	private Map<String, Object> buildActivityInfoData(String activityId, String userId) {
		LOG.debug("build activity datas");
		Activity activity = activityDao.selectByPrimaryKey(activityId);

		Map<String, Object> param = buildCommonQueryParam(activityId, userId);

		List<Map<String, Object>> members = activityViewDao.selectActivityMembers(param);

		List<Map<String, Object>> covers = buildActivityCovers(param);

		Map<String, Object> organizer = activityViewDao.selectActivityOrganizer(param);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("activityId", activity.getId());
		data.put("publishTime", activity.getCreatetime());
		data.put("introduction", CommonUtil.ifNull(activity.getDescription(), ""));
		data.put("location", activity.getLocation());
		data.put("start", activity.getStart());
		data.put("end", activity.getEnd());
		data.put("pay", activity.getPaymenttype());
		data.put("longitude", CommonUtil.ifNull(activity.getLongitude(), "0"));
		data.put("latitude", CommonUtil.ifNull(activity.getLatitude(), "0"));
		data.put("province", CommonUtil.ifNull(activity.getProvince(), ""));
		data.put("city", CommonUtil.ifNull(activity.getCity(), ""));
		data.put("district", CommonUtil.ifNull(activity.getDistrict(), ""));
		data.put("end", activity.getEnd());

		data.put("organizer", organizer);
		data.put("members", members);
		data.put("cover", covers);
		data.put("isSubscribed", null);
		data.put("isOrganizer", isOrganizer(userId, activity.getOrganizer()));
		data.put("seatInfo", activity.getInitialseat());
		data.put("isMember", isMember(userId, activity.getId(), members));
		data.put("isModified", activity.getId());

		Map<String, Object> subParam = new HashMap<String, Object>(2, 1);
		subParam.put("activityId", activityId);
		subParam.put("userId", userId);
		Integer subCount = subscriptionDao.selectCountByParam(subParam);
		data.put("isSubscribed", subCount);
		data.put("isModified", isModified(activity.getCreatetime(), activity.getLastmodifiedtime()));
		data.put("isOver", isActivityOver(activity.getEndtime()));

		return data;
	}

	/**
	 * 检查活动是否修改过
	 * 
	 * @param createTime
	 *            活动创建时间
	 * @param modifiedTime
	 *            活动修改时间
	 * 
	 * @return 如果活动没有修改返回0， 修改过返回1
	 */
	private Integer isModified(Long createTime, Long modifiedTime) {
		Integer isModified = 0;

		if (createTime == null || modifiedTime == null) {
			return isModified;
		}

		if (!createTime.equals(modifiedTime)) {
			isModified = 1;
		}
		return isModified;
	}

	/**
	 * 根据活动endTime时间与当前比较，确认是否过期
	 * 
	 * @param endTime
	 *            结束时间
	 * @return 活动是否过期
	 */
	private Integer isActivityOver(Long endTime) {
		Integer isOver = 0;
		if (endTime < DateUtil.getTime()) {
			isOver = 1;
		}
		return isOver;
	}

	/**
	 * 构造活动信息的封面信息
	 * 
	 * @param param
	 *            查询参数
	 * @return 返回活动封面信息
	 */
	private List<Map<String, Object>> buildActivityCovers(Map<String, Object> param) {
		List<Map<String, Object>> covers = activityViewDao.selectActivityCovers(param);
		for (Map<String, Object> item : covers) {
			String coverUrl = String.valueOf(item.get("original_pic"));
			String coverId = coverUrl.substring(0, coverUrl.lastIndexOf("/"));
			item.put("coverId", coverId.substring(coverId.lastIndexOf("/")));
		}
		return covers;
	}
}
