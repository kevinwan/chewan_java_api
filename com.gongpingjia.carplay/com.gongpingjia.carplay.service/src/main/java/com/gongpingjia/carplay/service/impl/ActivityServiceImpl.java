package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.enums.ApplicationStatus;
import com.gongpingjia.carplay.common.enums.MessageType;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.common.util.TypeConverUtil;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.dao.ActivityCommentDao;
import com.gongpingjia.carplay.dao.ActivityCoverDao;
import com.gongpingjia.carplay.dao.ActivityDao;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.dao.ActivitySubscriptionDao;
import com.gongpingjia.carplay.dao.ActivityViewDao;
import com.gongpingjia.carplay.dao.ActivityViewHistoryDao;
import com.gongpingjia.carplay.dao.ApplicationChangeHistoryDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.MessageDao;
import com.gongpingjia.carplay.dao.SeatReservationDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.Activity;
import com.gongpingjia.carplay.po.ActivityApplication;
import com.gongpingjia.carplay.po.ActivityComment;
import com.gongpingjia.carplay.po.ActivityCover;
import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.ActivityMemberKey;
import com.gongpingjia.carplay.po.ActivitySubscription;
import com.gongpingjia.carplay.po.ActivitySubscriptionKey;
import com.gongpingjia.carplay.po.ActivityView;
import com.gongpingjia.carplay.po.ActivityViewHistory;
import com.gongpingjia.carplay.po.ApplicationChangeHistory;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.Message;
import com.gongpingjia.carplay.po.SeatReservation;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Autowired
	private ParameterChecker checker;

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

	@Autowired
	private ActivityCommentDao commentDao;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private ChatThirdPartyService chatThirdService;

	@Autowired
	private ChatCommonService chatCommonService;

	@Override
	public ResponseDo getAvailableSeats(String userId, String token, String activityId) throws ApiException {

		checker.checkUserInfo(userId, token);

		LOG.debug("Query car list by userId");
		Car car = carDao.selectByUserId(userId);

		Map<String, Object> result = new HashMap<String, Object>(3, 1);

		int maxValue = 0;
		if (car == null) {
			LOG.info("User has not authicate in system");
			maxValue = getMaxCarSeat(null);
			result.put("maxValue", maxValue);
			result.put("isAuthenticated", 0);
		} else {
			LOG.info("User has already authicated");
			maxValue = getMaxCarSeat(car);
			result.put("maxValue", maxValue);
			result.put("isAuthenticated", 1);
		}

		int minValue = getMinCarSeat();
		// activID不为空
		if (!StringUtils.isEmpty(activityId)) {
			LOG.debug("Get seat mimValue of activityId:{}", activityId);
			Integer noCarSeats = seatReservDao.selectActivityJoinSeatCount(activityId);
			if (noCarSeats > minValue && noCarSeats < maxValue) {
				minValue = noCarSeats;
			}
		}
		result.put("minValue", minValue);

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

		// 生成公共的部分参数
		String activityId = CodeGenerator.generatorId();
		Long current = DateUtil.getTime();

		Activity activity = saveActivity(request, activityId, current);

		saveActivityCovers(request, activityId, current);

		saveActivityMember(userId, activityId, current);

		saveSeatReservation(userId, activityId, activity.getInitialseat(), current);

		saveActivityApplication(request.getParameter("seat"), activityId, userId, current);

		// 创建环信聊天群,更新活动字段
		createEmchatGroup(activity);

		Map<String, String> data = buildShareData(userId, activity);

		return ResponseDo.buildSuccessResponse(data);
	}

	/**
	 * 根据活动信息创建聊天群
	 * 
	 * @param activity
	 *            活动信息
	 * @throws ApiException
	 *             创建群聊失败时
	 */
	private void createEmchatGroup(Activity activity) throws ApiException {
		LOG.debug("Begin create chat group");
		JSONObject json = chatThirdService.createChatGroup(chatCommonService.getChatToken(), activity.getTitle(),
				activity.getId().replace("-", "_"), chatCommonService.getUsernameByUserid(activity.getOrganizer()),
				null);
		if (json.isEmpty()) {
			LOG.warn("Failed to create chat group");
			throw new ApiException("创建聊天群组失败");
		}

		// 完成群组创建之后，创建群聊组
		activity.setEmchatgroupid(json.getJSONObject("data").getString("groupid"));
		activity.setMembersize(PropertiesUtil.getProperty("huanxin.group.maxusers", 500));
		activityDao.updateByPrimaryKey(activity);
	}

	/**
	 * 根据活动构建分享的信息
	 * 
	 * @param userId
	 *            活动创建者
	 * @param activity
	 *            活动信息
	 * @return 返回构造的分享信息的Map
	 */
	private Map<String, String> buildShareData(String userId, Activity activity) {
		User user = userDao.selectByPrimaryKey(userId);
		Map<String, String> data = new HashMap<String, String>(5, 1);
		data.put("activityId", activity.getId());
		data.put("shareUrl",
				MessageFormat.format(PropertiesUtil.getProperty("activity.share.url", ""), activity.getId()));
		data.put("shareTitle", MessageFormat.format(PropertiesUtil.getProperty("activity.share.title", ""),
				user.getNickname(), activity.getTitle()));
		String date = DateUtil.format(activity.getStart(), Constants.DateFormat.ACTIVITY_SHARE);

		data.put(
				"shareContent",
				MessageFormat.format(PropertiesUtil.getProperty("activity.share.content", ""), new Object[] { date,
						activity.getLocation(), activity.getPaymenttype() }));
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

		List<SeatReservation> reservationList = new ArrayList<SeatReservation>(seat);
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
			activityCover.setUrl(MessageFormat.format(Constants.PhotoKey.COVER_KEY, coverId));
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
			LOG.warn("The offered car seat is override range");
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
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String district = request.getParameter("district");
		// 要么省市区全部都不为空，要么地址不为空，当二者都为空的时候需要报错
		if (isActivityDetailCityEmpty(province, city, district) && StringUtils.isEmpty(request.getParameter("address"))) {
			LOG.warn("Province, city and district cannot be empty, or the same with address");
			throw new ApiException("输入参数有误");
		}
	}

	/**
	 * 检查省、市、区信息是否存在空值，存在空字符串就返回false，都不为空返回
	 * 
	 * @param province
	 * @param city
	 * @param district
	 * @return
	 */
	private boolean isActivityDetailCityEmpty(String province, String city, String district) {
		if (StringUtils.isEmpty(province)) {
			return true;
		}
		// 当为直辖市的时候，city为空值，不能检查
		// if (StringUtils.isEmpty(city)) {
		// return true;
		// }
		if (StringUtils.isEmpty(district)) {
			return true;
		}
		return false;
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
			LOG.warn("Input parameter covers length is {}, out of the range", (covers == null) ? 0 : covers.length);
			throw new ApiException("输入参数有误");
		}

		for (String coverId : covers) {
			if (!photoService.isExist(MessageFormat.format(Constants.PhotoKey.COVER_KEY, coverId))) {
				LOG.warn("Activity cover is not exist, cover:{}", coverId);
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
	@Override
	public void checkRegisterActivityParam(HttpServletRequest request) throws ApiException {
		LOG.debug("Check input parameters");
		String userId = request.getParameter("userId");
		String token = request.getParameter("token");
		checker.checkUserInfo(userId, token);

		checker.checkParameterEmpty("type", request.getParameter("type"));
		checker.checkParameterEmpty("introduction", request.getParameter("introduction"));
		checker.checkParameterEmpty("location", request.getParameter("location"));
		checker.checkParameterEmpty("pay", request.getParameter("pay"));
		checker.checkParameterLongType("start", request.getParameter("start"));

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

		buildActivityCommon(request, current, activity);

		activity.setCreatetime(current);
		activity.setCategory(Constants.ActivityCatalog.COMMON);
		LOG.debug("Save activity");
		activityDao.insert(activity);

		return activity;
	}

	/**
	 * 根据请求参数构造Activity活动对象公共部分属性
	 * 
	 * @param request
	 *            请求参数
	 * @param current
	 *            当前时间
	 * @param activity
	 *            活动对象
	 * @throws ApiException
	 *             业务异常
	 */
	private void buildActivityCommon(HttpServletRequest request, Long current, Activity activity) throws ApiException {
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
		if (activity.getDescription().length() <= length) {
			activity.setTitle(activity.getDescription());
		} else {
			activity.setTitle(activity.getDescription().substring(0, length));
		}

		if (isActivityDetailCityEmpty(activity.getProvince(), activity.getCity(), activity.getDistrict())) {
			LOG.debug("Build activity province, city, district if they are empty");
			User user = userDao.selectByPrimaryKey(request.getParameter("userId"));
			activity.setProvince(user.getProvince());
			activity.setCity(user.getCity());
			activity.setDistrict(user.getDistrict());
		}

		activity.setLastmodifiedtime(current);
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
		if (Constants.ActivityKey.HOTTEST.equals(key)) {
			activityList = activityViewDao.selectHottestActivities(param);
		} else if (Constants.ActivityKey.NEARBY.equals(key)) {
			activityList = activityViewDao.selectNearbyActivities(param);
		} else if (Constants.ActivityKey.LATEST.equals(key)) {
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
			Map<String, Object> record = new HashMap<String, Object>(14, 1);
			record.put("activityId", item.getActivityId());
			record.put("publishTime", item.getPublishTime());
			record.put("start", item.getStart());
			record.put("introduction", item.getIntroduction());
			record.put("location", item.getLocation());
			record.put("type", item.getType());
			record.put("pay", item.getPay());

			Map<String, Object> organizer = new HashMap<String, Object>(8, 1);
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
	private Integer isOrganizer(String userId, String activityUserId) {
		Integer isOrganizer = 0;
		if (activityUserId.equals(userId)) {
			isOrganizer = 1;
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
	private Integer isMember(String userId, String activityId, List<Map<String, Object>> members) {
		Integer isMember = 0;
		for (Map<String, Object> member : members) {
			if (member.containsKey(userId)) {
				isMember = 1;
			}
		}

		Map<String, Object> appParam = new HashMap<String, Object>(3, 1);
		appParam.put("activityId", activityId);
		appParam.put("userId", userId);
		appParam.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		List<ActivityApplication> appList = applicationDao.selectByParam(appParam);

		if (!appList.isEmpty()) {
			// 在申请待处理中
			isMember = 2;
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
			LOG.warn("Input parameter userId: {} is not UUID", userId);
			throw new ApiException("输入参数有误");
		}

		if (!CommonUtil.isUUID(token)) {
			LOG.warn("Input parameter token: {} is not UUID", token);
			throw new ApiException("输入参数有误");
		}

		if (!Constants.ActivityKey.KEY_LIST.contains(key)) {
			LOG.warn("Input parameter key: {} error", key);
			throw new ApiException("输入参数有误");
		}

		if (Constants.ActivityKey.NEARBY.equals(key) && (longitude == null || latitude == null)) {
			LOG.warn("Input parameter is nearby, but with no longitude or latitude");
			throw new ApiException("未能获取您的位置信息");
		}

		if (Constants.ActivityKey.NEARBY.equals(key)) {
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

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Fail to get activity info, activityId:{}", activityId);
			throw new ApiException("未能成功获取活动详情");
		}

		// 当输入的userID不为空的时候，需要校验
		if (!StringUtils.isEmpty(userId)) {
			checker.checkUserInfo(userId, token);

			LOG.debug("Record activity view history log");
			saveActivityViewHistory(activityId, userId);
		}

		// 查询活动数据
		Map<String, Object> data = buildActivityInfoData(activity, userId);

		// 查询活动相关的数据
		return ResponseDo.buildSuccessResponse(data);
	}

	/**
	 * 根据活动ID获取当前作为预定的信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @return 作为预定信息列表
	 */
	private List<SeatReservation> getSeatReservations(String activityId) {
		Map<String, Object> seatParam = new HashMap<String, Object>(1);
		seatParam.put("activityId", activityId);
		return seatReservDao.selectListByParam(seatParam);
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
	 * @param activity
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @return 返回构造的数据信息
	 */
	private Map<String, Object> buildActivityInfoData(Activity activity, String userId) {
		LOG.debug("build activity datas");
		Map<String, Object> param = buildCommonQueryParam(activity.getId(), userId);

		List<Map<String, Object>> members = activityViewDao.selectActivityMembers(param);

		List<Map<String, Object>> covers = buildActivityCovers(param);

		Map<String, Object> organizer = activityViewDao.selectActivityOrganizer(param);

		Map<String, Object> data = new HashMap<String, Object>(24, 1);
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

		data.put("isMember", isMember(userId, activity.getId(), members));
		data.put("isModified", activity.getId());

		Map<String, Object> subParam = new HashMap<String, Object>(2, 1);
		subParam.put("activityId", activity.getId());
		subParam.put("userId", userId);

		Integer subCount = subscriptionDao.selectCountByParam(subParam);
		data.put("isSubscribed", subCount);

		data.put("isModified", isModified(activity.getCreatetime(), activity.getLastmodifiedtime()));
		data.put("isOver", isActivityOver(activity.getEndtime()));

		data.put("seatInfo", buildSeateInfo(activity.getId()));

		return data;
	}

	/**
	 * 构造SeatInfo信息
	 * 
	 * @param activityId
	 *            活动ID
	 * @return
	 */
	private String buildSeateInfo(String activityId) {
		List<SeatReservation> seatList = getSeatReservations(activityId);
		int availableSeats = getAvailableSeats(seatList);
		int noCarSeats = getNoCarSeats(seatList);

		if (noCarSeats == 0) {
			// 都有车
			final String format = "{0}（{1}空座）";
			StringBuilder build = new StringBuilder();
			List<Map<String, Object>> seatInfoList = activityViewDao.selectReservSeatInfoByActivityId(activityId);
			for (Map<String, Object> item : seatInfoList) {
				build.append(MessageFormat.format(format, item.get("model"), item.get("count"))).append(",");
			}
			if (build.length() > 0) {
				return build.substring(0, build.lastIndexOf(",")).toString();
			}
		}

		return availableSeats + "个空座";
	}

	/**
	 * 获取空余座位数, 如果没有空余作为数返回0
	 * 
	 * @param seatList
	 *            作为预定列表
	 * @return 返回空余座位数
	 */
	private int getAvailableSeats(List<SeatReservation> seatList) {
		int availableSeats = 0;
		for (SeatReservation seat : seatList) {
			if (seat.getUserid() == null) {
				// 剩余空座位数
				availableSeats++;
			}
		}
		return availableSeats;
	}

	/**
	 * 获取没有车的座位数信息，如果预定人员都有车，则返回0
	 * 
	 * @param seatList
	 *            作为列表
	 * @return 返回没有车的座位数
	 */
	private int getNoCarSeats(List<SeatReservation> seatList) {
		int noCarSeats = 0;
		for (SeatReservation seat : seatList) {
			if (seat.getCarid() == null) {
				// 有车的
				noCarSeats++;
			}
		}
		return noCarSeats;
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

	@Override
	public ResponseDo getActivityComments(String activityId, String userId, String token, Integer ignore, Integer limit)
			throws ApiException {

		LOG.debug("Check input parameters");
		if (!CommonUtil.isUUID(activityId)) {
			LOG.warn("Input parameter activityId is not an uuid, activityId:{}", activityId);
			throw new ApiException("输入参数有误");
		}

		LOG.debug("Query comments from database");
		Map<String, Object> param = new HashMap<String, Object>(4, 1);
		param.put("activityId", activityId);
		param.put("ignore", ignore);
		param.put("limit", limit);
		param.put("assetUrl", CommonUtil.getPhotoServer());

		return ResponseDo.buildSuccessResponse(activityViewDao.selectActivityComments(param));
	}

	@Override
	public ResponseDo publishComment(String activityId, String userId, String token, String replyUserId, String comment)
			throws ApiException {

		LOG.debug("Check input parameters");
		if (!CommonUtil.isUUID(activityId)) {
			LOG.warn("input parameter activityId is not uuid, activityId:{}", activityId);
			throw new ApiException("参数错误");
		}

		checker.checkUserInfo(userId, token);

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Fail to get activity info, activityID:{}", activityId);
			throw new ApiException("未能获取活动创建者信息");
		}

		Long current = DateUtil.getTime();

		if (replyUserId != null) {
			if (!CommonUtil.isUUID(replyUserId)) {
				LOG.warn("input replyUserId is not uuid, replyUserId:{}", replyUserId);
				throw new ApiException("参数错误");
			}
			if (userId.equals(replyUserId)) {
				LOG.warn("input userId is the same with replyUserId, cannot reply self");
				throw new ApiException("不能回复自己的评论");
			}

			if (!checker.isUserExist(replyUserId)) {
				LOG.warn("Reply user is not exist");
				throw new ApiException("回复的用户不存在");
			}

			Message replyMessage = new Message();
			replyMessage.setId(CodeGenerator.generatorId());
			replyMessage.setFromuser(userId);
			replyMessage.setTouser(replyUserId);
			replyMessage.setType(MessageType.COMMENT.getName());
			replyMessage.setContent(comment);
			replyMessage.setCreatetime(current);
			replyMessage.setExtra1(activityId);
			messageDao.insert(replyMessage);
		}

		LOG.debug("Record message");

		ActivityComment activityComment = new ActivityComment();
		activityComment.setId(CodeGenerator.generatorId());
		activityComment.setActivityid(activityId);
		activityComment.setComment(comment);
		activityComment.setCreatetime(current);
		activityComment.setReplyuserid(replyUserId);
		activityComment.setUserid(userId);
		commentDao.insert(activityComment);

		Message message = new Message();
		message.setId(CodeGenerator.generatorId());
		message.setFromuser(userId);
		message.setTouser(activity.getOrganizer());
		message.setType(MessageType.COMMENT.getName());
		message.setContent(comment);
		message.setCreatetime(current);
		message.setExtra1(activityId);
		messageDao.insert(message);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo subscribeActivity(String activityId, String userId, String token) throws ApiException {
		LOG.debug("Check input parameters");
		checker.checkUserInfo(userId, token);

		if (!CommonUtil.isUUID(activityId)) {
			LOG.warn("Input parameter activityId is not uuid, activityId:{}", activityId);
			throw new ApiException("参数错误");
		}

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null || userId.equals(activity.getOrganizer())) {
			LOG.warn("No activity exist or user cannot focus on self create activity");
			throw new ApiException("未找到活动，无法关注");
		}

		LOG.debug("Save activity subscription info");
		ActivitySubscription subscription = new ActivitySubscription();
		subscription.setActivityid(activityId);
		subscription.setSubscribetime(DateUtil.getTime());
		subscription.setUserid(userId);
		subscriptionDao.insert(subscription);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo joinActivity(String activityId, String userId, String token, Integer seat) throws ApiException {
		Activity activity = checkInputParameters(activityId, userId, token, seat);

		LOG.debug("Check input parameters is satisfied business logic");
		checkApplicationInfo(activityId, userId);

		checkActivityMemberInfo(activityId, userId);

		checkSeatInfo(activityId, userId, seat);

		saveJoinApplication(userId, seat, activity);

		return ResponseDo.buildSuccessResponse();
	}

	/**
	 * 保存申请人申请信息，创建历史信息，创建相应的消息
	 * 
	 * @param userId
	 *            用户ID
	 * @param seat
	 *            申请人提供的作为信息
	 * @param activity
	 *            活动
	 */
	private void saveJoinApplication(String userId, Integer seat, Activity activity) {
		LOG.debug("Begin save join application");
		Long current = DateUtil.getTime();
		ActivityApplication application = new ActivityApplication();
		application.setActivityid(activity.getId());
		application.setCreatetime(current);
		application.setId(CodeGenerator.generatorId());
		application.setSeat(seat);
		application.setUserid(userId);
		application.setStatus(ApplicationStatus.PENDING_PROCESSED.getName());
		applicationDao.insert(application);

		ApplicationChangeHistory history = new ApplicationChangeHistory();
		history.setApplicationid(application.getId());
		history.setId(CodeGenerator.generatorId());
		history.setStatus(ApplicationStatus.PENDING_PROCESSED.getName());
		history.setTimestamp(current);
		historyDao.insert(history);

		Message message = new Message();
		message.setId(CodeGenerator.generatorId());
		message.setFromuser(userId);
		message.setTouser(activity.getOrganizer());
		message.setType(MessageType.APPLICATION_PROCESS.getName());
		message.setContent(activity.getDescription());
		message.setCreatetime(current);
		message.setExtra1(activity.getId());
		message.setExtra2(seat);
		message.setExtra3(application.getId());
		message.setIsdeleted((byte) 0);
		messageDao.insert(message);
	}

	/**
	 * 检查如果申请人提供车座，提供车座的数量是否与车主认证的车子信息匹配
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param seat
	 *            提供的作为信息，如果为0则忽略
	 * @throws ApiException
	 *             检查车主提供信息是否合格，不合格抛出业务异常
	 */
	private void checkSeatInfo(String activityId, String userId, Integer seat) throws ApiException {
		if (seat > 0) {
			// 如果提供车辆
			LOG.info("Join activity user offer car, userId:{}", userId);
			Car car = carDao.selectByUserId(userId);
			if (car == null) {
				LOG.warn("Cannot obtain user cat infomation");
				throw new ApiException("未能成功返回该用户的车辆信息");
			}

			if (seat > car.getSeat()) {
				LOG.warn("Fail to provide enough seats");
				throw new ApiException("不能提供超出车座总数的座位");
			}

			Integer noCarSeats = seatReservDao.selectActivityJoinSeatCount(activityId);
			if (seat < noCarSeats) {
				LOG.warn("Cannot provide enough seats for this activity");
				throw new ApiException("您认证的车辆需要提供至少" + noCarSeats + "个空座才可以申请加入");
			}
		}
	}

	/**
	 * 检查活动成员信息，主要是检查当前申请者是否已经是活动成员，不能重复申请
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkActivityMemberInfo(String activityId, String userId) throws ApiException {
		ActivityMemberKey key = new ActivityMemberKey();
		key.setActivityid(activityId);
		key.setUserid(userId);
		ActivityMember member = memberDao.selectByPrimaryKey(key);
		if (member != null) {
			LOG.warn("Already be a member");
			throw new ApiException("已是成员，不能重复申请加入活动");
		}
	}

	/**
	 * 检查申请人员申请信息，主要防止重复申请
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkApplicationInfo(String activityId, String userId) throws ApiException {
		Map<String, Object> param = new HashMap<String, Object>(3, 1);
		param.put("userId", userId);
		param.put("activityId", activityId);
		param.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		List<ActivityApplication> applicationList = applicationDao.selectByParam(param);
		if (!applicationList.isEmpty()) {
			// 不为空表明已经申请过了
			LOG.warn("already applied for this activity");
			throw new ApiException("之前已申请过参加该活动，请勿重复申请");
		}
	}

	/**
	 * 检查输入参数是否正确，仅作参数的合法性基础校验
	 * 
	 * @param activityId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            会话Token
	 * @param seat
	 *            能提供的作为数
	 * @return 返回 活动信息
	 * @throws ApiException
	 *             参数校验不合法，抛业务异常
	 */
	private Activity checkInputParameters(String activityId, String userId, String token, Integer seat)
			throws ApiException {
		LOG.debug("Begin check input parameters");

		checker.checkParameterUUID("activityId", activityId);

		checker.checkUserInfo(userId, token);

		if (seat < 0 || seat > PropertiesUtil.getProperty("user.auth.car.max.seats", 5)) {
			LOG.warn("Input parameter seat error, seat:{}", seat);
			throw new ApiException("参数错误");
		}

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Activity is not found in the system, activityId:{}", activityId);
			throw new ApiException("未找到活动，无法加入");
		}
		return activity;
	}

	@Override
	public ResponseDo processApplication(String applicationId, String userId, String token, Integer action)
			throws ApiException {

		checkProcessParameters(applicationId, userId, token, action);

		Map<String, Object> appActInfo = checkApplyInfo(applicationId, userId);

		List<SeatReservation> seats = checkMemberSeatReservation(String.valueOf(appActInfo.get("activityId")));

		Long current = DateUtil.getTime();

		updateApplication(applicationId, action, current);

		if (action == 1) {
			// 批准了活动申请
			updateSeatReservationInfo(applicationId, userId, appActInfo, seats, current);

			addActivityMember(applicationId, userId, appActInfo, current);

			addSingleMemberIntoGroup(String.valueOf(appActInfo.get("activityId")),
					String.valueOf(appActInfo.get("appliedUser")));
		}

		return ResponseDo.buildSuccessResponse();
	}

	private void addSingleMemberIntoGroup(String activityId, String applicationUserId) throws ApiException {
		LOG.debug("Add user:{} into chat member group, activityId:{}", applicationUserId, activityId);

		Activity activity = activityDao.selectByPrimaryKey(activityId);

		JSONObject json = chatThirdService.addUserToChatGroup(chatCommonService.getChatToken(),
				activity.getEmchatgroupid(), chatCommonService.getUsernameByUserid(applicationUserId));
		if (json.isEmpty()) {
			LOG.warn("Add user to chat group failure, groupId:{}", activity.getEmchatgroupid());
			throw new ApiException("加入群聊失败");
		}

		// 检查结果 重复添加也是返回成功的
		boolean result = json.getJSONObject("data").getBoolean("result");
		if (!result) {
			// 添加用户失败
			LOG.warn("Add user to chat group failure, chat serrver response:{}", json.toString());
			throw new ApiException("加入群聊失败");
		}

		// 发送消息
		LOG.debug("Send message after add user");
		User user = userDao.selectByPrimaryKey(applicationUserId);
		final String format = "欢迎 '{0}' 加入活动";
		String textMessage = MessageFormat.format(format, user.getNickname());

		JSONObject sendResult = chatThirdService.sendChatGroupTextMessage(chatCommonService.getChatToken(),
				chatCommonService.getUsernameByUserid(user.getId()), activity.getEmchatgroupid(), textMessage);
		if (sendResult.isEmpty()) {
			LOG.warn("Send messge failure, result:{}", sendResult.toString());
			throw new ApiException("未能成功发送文本消息");
		}
	}

	/**
	 * 将申请人员添加到活动成员中
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param userId
	 *            用户ID
	 * @param appActInfo
	 *            活动申请信息
	 * @param current
	 *            当前时间
	 */
	private void addActivityMember(String applicationId, String userId, Map<String, Object> appActInfo, Long current) {
		LOG.debug("Begin add member to the activity");
		String activityId = String.valueOf(appActInfo.get("activityId"));
		String appliedUser = String.valueOf(appActInfo.get("appliedUser"));

		ActivityMember member = new ActivityMember();
		member.setActivityid(activityId);
		member.setUserid(appliedUser);
		member.setJointime(current);
		memberDao.insert(member);

		Message updateMessage = new Message();
		updateMessage.setExtra3(applicationId);
		updateMessage.setRemarks(ApplicationStatus.APPROVED.getName());
		messageDao.updateRemarksByExtra3(updateMessage);

		Message insertMessage = new Message();
		insertMessage.setId(CodeGenerator.generatorId());
		insertMessage.setFromuser(userId);
		insertMessage.setTouser(appliedUser);
		insertMessage.setType(MessageType.APPLICATION_RESULT.getName());
		insertMessage.setContent(String.valueOf(appActInfo.get("introduction")));
		insertMessage.setCreatetime(current);
		insertMessage.setExtra1(activityId);
		insertMessage.setExtra2(0);
		insertMessage.setExtra3(applicationId);
		messageDao.insert(insertMessage);
	}

	/**
	 * 当同意申请人员加入时，更新座位预定信息
	 * 
	 * @param applicationId
	 *            活动ID
	 * @param userId
	 *            用户ID
	 * @param appActInfo
	 *            活动申请信息
	 * @param seats
	 *            与活动相关的作为预定信息
	 * @param current
	 *            当前时间
	 * @throws ApiException
	 *             业务异常
	 */
	private void updateSeatReservationInfo(String applicationId, String userId, Map<String, Object> appActInfo,
			List<SeatReservation> seats, Long current) throws ApiException {
		LOG.debug("Begin update seatReservation iformation");
		String activityId = String.valueOf(appActInfo.get("activityId"));
		int totalSeats = seats.size();
		// 申请者能提供的座位数
		int seatCount = TypeConverUtil.convertToInteger("seat", String.valueOf(appActInfo.get("seat")), false);

		int noCarSeatCount = getNoCarSeats(seats);
		if (getAvailableSeats(seats) == 0) {
			LOG.warn("No available seat for this user");
			String errorMessage = "请选择“提供空座”的人加入活动";
			if (noCarSeatCount == totalSeats) {
				errorMessage = "还没有人提供车，" + errorMessage;
			}
			throw new ApiException(errorMessage);
		}

		// 申请人可以提供座位
		if (seatCount > 0) {
			LOG.debug("Application user can offer {} seats , applicationId:{}", seatCount, applicationId);
			if (seatCount < noCarSeatCount) {
				LOG.warn("Need to provide at least {} seats", noCarSeatCount);
				throw new ApiException("该用户必须提供不少于 " + noCarSeatCount + " 个空座");
			}

			Car car = carDao.selectByUserId(String.valueOf(appActInfo.get("appliedUser")));
			if (car == null) {
				LOG.warn("Fail to find car of applied user");
				throw new ApiException("未能成功返回该用户的车辆信息");
			}

			if (seatCount > car.getSeat()) {
				LOG.warn("Fail to provide enough seats");
				throw new ApiException("不能提供超出车座总数的座位");
			}

			if (noCarSeatCount > 0) {
				// 车主，没有车的人 座位往后推
				Map<String, Object> updateParam = new HashMap<String, Object>(2, 1);
				updateParam.put("carId", car.getId());
				updateParam.put("activityId", activityId);
				seatReservDao.updateByOfferdCar(updateParam);
			}

			List<SeatReservation> reservList = new ArrayList<SeatReservation>(seatCount);
			// 有车的
			reservList.add(buildSeatReservation(userId, activityId, current, car.getId(), 0));
			// 没有车的
			for (int i = noCarSeatCount + 1; i <= seatCount; ++i) {
				reservList.add(buildSeatReservation(null, activityId, current, car.getId(), i));
			}
			seatReservDao.insert(reservList);
		}

	}

	/**
	 * 构建座位预定对象
	 * 
	 * @param userId
	 *            用户ID
	 * @param activityId
	 *            活动ID
	 * @param current
	 *            当前时间
	 * @param carId
	 *            车主carID
	 * @param index
	 *            如果是车主的话，索引为0
	 * @return 返回构建的对象
	 */
	private SeatReservation buildSeatReservation(String userId, String activityId, Long current, String carId,
			Integer index) {
		SeatReservation reservation = new SeatReservation();
		reservation.setId(CodeGenerator.generatorId());
		reservation.setActivityid(activityId);
		reservation.setUserid(userId);
		if (index == 0) {
			reservation.setCreatetime(current);
			reservation.setBooktime(current);
		}
		reservation.setCarid(carId);
		reservation.setSeatindex(index);
		return reservation;
	}

	/**
	 * 根据审批结果更新application信息
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param action
	 *            是否同意，0 拒绝，1同意
	 * @param current
	 *            当前时间
	 */
	private void updateApplication(String applicationId, Integer action, Long current) {
		LOG.debug("Update application iformation");
		ApplicationStatus targetStatus = ApplicationStatus.DECLINED;
		if (action == 1) {
			targetStatus = ApplicationStatus.APPROVED;
		}
		ActivityApplication application = applicationDao.selectByPrimaryKey(applicationId);
		application.setStatus(targetStatus.getName());
		applicationDao.updateByPrimaryKey(application);

		ApplicationChangeHistory history = new ApplicationChangeHistory();
		history.setId(CodeGenerator.generatorId());
		history.setApplicationid(applicationId);
		history.setStatus(targetStatus.getName());
		history.setTimestamp(current);
		historyDao.insert(history);
	}

	/**
	 * 检查入参格式是否正确
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param userId
	 *            用户ID
	 * @param token
	 *            用户会话Token
	 * @param action
	 *            是否同意
	 * @throws ApiException
	 *             业务异常
	 */
	private void checkProcessParameters(String applicationId, String userId, String token, Integer action)
			throws ApiException {
		LOG.debug("Begin check input parameters");

		checker.checkParameterUUID("applicationId", applicationId);

		if (action != 0 && action != 1) {
			LOG.warn("Input parameter action is 0 or 1, action:{}", action);
			throw new ApiException("参数错误");
		}

		checker.checkUserInfo(userId, token);
	}

	/**
	 * 检查申请信息业务上是否满足要求
	 * 
	 * @param applicationId
	 *            申请ID
	 * @param userId
	 *            用户ID
	 * @return 返回申请信息
	 * @throws ApiException
	 *             业务异常
	 */
	private Map<String, Object> checkApplyInfo(String applicationId, String userId) throws ApiException {
		LOG.debug("Check input parameter business logic");
		Map<String, String> param = new HashMap<String, String>(3, 1);
		param.put("applicationId", applicationId);
		param.put("userId", userId);
		param.put("status", ApplicationStatus.PENDING_PROCESSED.getName());
		List<Map<String, Object>> applicationActivitys = activityViewDao.selectActivityApplication(param);
		if (applicationActivitys.isEmpty()) {
			LOG.warn("The apploication activity not found");
			throw new ApiException("未找到待处理的请求");
		}

		Map<String, Object> appActivity = applicationActivitys.get(0);
		if (userId.equals(appActivity.get("appliedUser"))) {
			LOG.warn("Cannot process application of yourself");
			throw new ApiException("不能处理自己提出的申请");
		}
		return appActivity;
	}

	/**
	 * 检查成员是否已满达到座位预定数
	 * 
	 * @param activityId
	 *            活动ID
	 * @return 返回
	 * @throws ApiException
	 */
	private List<SeatReservation> checkMemberSeatReservation(String activityId) throws ApiException {
		Map<String, Object> activityParam = new HashMap<String, Object>(1);
		activityParam.put("activityId", activityId);
		List<ActivityMember> members = memberDao.selectByParam(activityParam);
		if (members.isEmpty()) {
			LOG.warn("Fail to get total members");
			throw new ApiException("获取成员信息失败");
		}

		List<SeatReservation> seats = getSeatReservations(activityId);
		if (members.size() >= seats.size()) {
			LOG.warn("Member count = {}, totalSeats = {}", members.size(), seats.size());
			throw new ApiException("座位数已满");
		}

		return seats;
	}

	@Override
	public ResponseDo getMemberAndSeatInfo(String activityId, String userId, String token) throws ApiException {

		LOG.debug("Check input parameters");

		checker.checkParameterUUID("activityId", activityId);
		checker.checkUserInfo(userId, token);

		LOG.debug("Query member car information");
		Map<String, Object> param = new HashMap<String, Object>(4, 1);
		param.put("assetUrl", CommonUtil.getPhotoServer());
		param.put("gpjImagePrefix", CommonUtil.getGPJImagePrefix());
		param.put("photoPostfix", CommonUtil.getActivityPhotoPostfix());
		param.put("activityId", activityId);
		param.put("status", ApplicationStatus.APPROVED.getName());
		List<Map<String, Object>> members = activityViewDao.selectActivityMemberCarInfo(param);
		List<Map<String, Object>> cars = activityViewDao.selectActivityMemberCarInfo(param);
		for (Map<String, Object> car : cars) {
			param.put("carId", car.get("carId"));
			List<Map<String, Object>> users = activityViewDao.selectSeatReservationInfo(param);
			car.put("users", users);
		}

		Map<String, Object> activityShareInfo = activityViewDao.selectActivityShareInfo(param);
		if (activityShareInfo == null) {
			activityShareInfo = new HashMap<String, Object>(8, 1);
		}
		activityShareInfo.put("members", members);
		activityShareInfo.put("cars", cars);
		activityShareInfo.put("activityId", activityId);
		Activity activity = activityDao.selectByPrimaryKey(activityId);
		activityShareInfo.putAll(buildShareData(activity.getOrganizer(), activity));

		LOG.debug("Finished build response data");
		return ResponseDo.buildSuccessResponse(activityShareInfo);
	}

	@Override
	public ResponseDo takeSeat(String activityId, String userId, String token, String carId, Integer seatIndex)
			throws ApiException {
		LOG.debug("Begin check input parameters");
		checker.checkUserInfo(userId, token);
		checker.checkParameterUUID("activityId", activityId);

		if (!StringUtils.isEmpty(carId)) {
			checker.checkParameterUUID("carId", carId);
		} else {
			carId = null;
		}

		if (seatIndex < 1) {
			LOG.debug("Input parameter seatIndex:{} is not correct", seatIndex);
			throw new ApiException("输入参数有误");
		}

		Map<String, Object> param = new HashMap<String, Object>(4, 1);
		param.put("activityId", activityId);
		param.put("userId", userId);
		List<ActivityMember> members = memberDao.selectByParam(param);
		if (members.isEmpty()) {
			// 非活动成员，不能占座
			LOG.warn("only member has privilege of taking seat");
			throw new ApiException("只有活动成员才可以占座");
		}

		List<SeatReservation> alreadyTakeSeats = seatReservDao.selectListByParam(param);
		if (!alreadyTakeSeats.isEmpty()) {
			// 说明已经占过座位了，不能重复抢座
			LOG.warn("cannot take seat if already had a seat in the same activity");
			throw new ApiException("不能在一个活动中重复占座");
		}

		LOG.debug("update seat reservation iformation");
		SeatReservation reservation = new SeatReservation();
		reservation.setActivityid(activityId);
		reservation.setBooktime(DateUtil.getTime());
		reservation.setCarid(carId);
		reservation.setSeatindex(seatIndex);
		reservation.setUserid(userId);
		int affectRows = seatReservDao.updateByTakeSeat(reservation);

		if (affectRows != 1) {
			LOG.warn("fail to take seat");
			throw new ApiException("未能成功占座");
		}

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo returnSeat(String activityId, String member, String userId, String token) throws ApiException {
		LOG.debug("Begin check input parameters");
		checker.checkParameterUUID("activityId", activityId);
		checker.checkParameterUUID("member", member);
		if (userId.equals(member)) {
			LOG.warn("User cannot pull self down");
			throw new ApiException("输入参数有误");
		}
		checker.checkUserInfo(userId, token);

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null || !activity.getOrganizer().equals(userId)) {
			LOG.warn("not activity organizer, cannot perform return seat");
			throw new ApiException("只有活动创建者可以将成员拉下座位");
		}

		Map<String, Object> param = new HashMap<String, Object>(2, 1);
		param.put("activityId", activityId);
		param.put("userId", member); // 这里是user需要拉下其他成员
		List<SeatReservation> seatList = seatReservDao.selectListByParam(param);
		if (seatList.isEmpty()) {
			LOG.warn("No related activity user exist in seat_reservation, activityId:{}, userId:{}", activityId, userId);
			throw new ApiException("未能成功拉下座位");
		}

		LOG.debug("Begin update seat reservation");
		SeatReservation seatReservation = seatList.get(0);
		seatReservation.setBooktime(null);
		seatReservation.setUserid(null);
		seatReservDao.updateByPrimaryKey(seatReservation);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo removeMember(String activityId, String member, String userId, String token) throws ApiException {
		LOG.debug("Begin check input parameters");
		checker.checkParameterUUID("activityId", activityId);
		checker.checkParameterUUID("member", member);
		if (userId.equals(member)) {
			LOG.warn("User cannot pull self down");
			throw new ApiException("活动创建者不能被移除");
		}
		checker.checkUserInfo(userId, token);

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null || !activity.getOrganizer().equals(userId)) {
			LOG.warn("not activity organizer, cannot remove member");
			throw new ApiException("只有活动创建者可以移除成员");
		}

		LOG.debug("Begin remove member from chat group");
		// 将用户从聊天群组中移除
		JSONObject json = chatThirdService.deleteUserFromChatGroup(chatCommonService.getChatToken(),
				activity.getEmchatgroupid(), chatCommonService.getUsernameByUserid(member));
		if (json.isEmpty()) {
			// 移除用户失败
			LOG.warn("Remove member:{} from chat group failure, groupId:{}", member, activity.getEmchatgroupid());
			throw new ApiException("未能将该成员退出群聊");
		}

		LOG.debug("Begin update system data");
		ActivityMemberKey key = new ActivityMemberKey();
		key.setActivityid(activityId);
		key.setUserid(member);
		memberDao.deleteByPrimaryKey(key);

		Car car = carDao.selectByUserId(userId);
		if (car != null) {
			SeatReservation reservation = new SeatReservation();
			reservation.setActivityid(activityId);
			reservation.setBooktime(null);
			reservation.setCarid(car.getId());
			reservation.setUserid(null);
			// 调用更新占座接口，设置对应的userID和Booktime字段为Null
			seatReservDao.updateByTakeSeat(reservation);

			Map<String, Object> param = new HashMap<String, Object>(2, 1);
			param.put("activityId", activityId);
			param.put("carId", car.getId());
			seatReservDao.deleteByParam(param);
		}

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo quitActivity(String activityId, String userId, String token) throws ApiException {
		LOG.debug("Begin check input parameters");
		checker.checkParameterUUID("activityId", activityId);
		checker.checkUserInfo(userId, token);

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Activity is not exist with activityId:{}", activityId);
			throw new ApiException("活动不存在");
		}

		if (activity.getOrganizer().equals(userId)) {
			LOG.warn("Activity creator cannot exist activity, userId:{}", userId);
			throw new ApiException("活动创建者不能退出活动");
		}

		LOG.debug("Begin remove member from chat group");
		// 将用户从聊天群组中移除
		JSONObject json = chatThirdService.deleteUserFromChatGroup(chatCommonService.getChatToken(),
				activity.getEmchatgroupid(), chatCommonService.getUsernameByUserid(userId));
		if (json.isEmpty()) {
			// 移除用户失败
			LOG.warn("Remove member:{} from chat group failure, groupId:{}", userId, activity.getEmchatgroupid());
			throw new ApiException("未能将该成员退出群聊");
		}

		LOG.debug("Refresh system data");
		Car car = carDao.selectByUserId(userId);
		if (car != null) {
			List<String> activityCars = seatReservDao.selectCarIdsByActivityId(activityId);
			if (activityCars.contains(car.getId()) && activityCars.size() == 1) {
				LOG.debug("The only exist car cannot quit the activity");
				throw new ApiException("最后一辆车的车主不能退出活动");
			}

			Map<String, Object> param = new HashMap<String, Object>(2, 1);
			param.put("activityId", activityId);
			param.put("carId", car.getId());
			seatReservDao.deleteByParam(param);
		}

		ActivityMemberKey key = new ActivityMemberKey();
		key.setActivityid(activityId);
		key.setUserid(userId);
		memberDao.deleteByPrimaryKey(key);

		Map<String, Object> quitParam = new HashMap<String, Object>(2, 1);
		quitParam.put("activityId", activityId);
		quitParam.put("userId", userId);
		seatReservDao.updateByQuitActivity(quitParam);

		return ResponseDo.buildSuccessResponse();
	}

	@Override
	public ResponseDo alterActivityInfo(String activityId, String userId, String token, HttpServletRequest request)
			throws ApiException {

		// 只有活动的创建者才能编辑活动
		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Activity is not exist, which activityId:{}", activityId);
			throw new ApiException("输入参数有误");
		}

		if (!activity.getOrganizer().equals(userId)) {
			LOG.warn("Fail to find activity");
			throw new ApiException("只有活动创建者可以编辑活动");
		}

		LOG.debug("refresh data");
		coverDao.deleteByActivityId(activityId);

		String oldTitle = activity.getTitle();

		Long current = DateUtil.getTime();
		buildActivityCommon(request, current, activity);
		activityDao.updateByPrimaryKey(activity);

		modifyEmchatGroup(activity, oldTitle);

		saveActivityCovers(request, activityId, current);

		Map<String, String> data = buildShareData(userId, activity);

		return ResponseDo.buildSuccessResponse(data);
	}

	private void modifyEmchatGroup(Activity activity, String oldTitle) throws ApiException {
		String newTitle = activity.getTitle();
		if (!oldTitle.equals(newTitle)) {
			LOG.debug("Begin modify chat group");
			JSONObject json = chatThirdService.modifyChatGroup(chatCommonService.getChatToken(),
					activity.getEmchatgroupid(), newTitle, activity.getId().replace("-", "_"));
			if (json.isEmpty()) {
				LOG.warn("Failed to modify chat group");
				throw new ApiException("修改聊天群组失败");
			}
		}
	}

	@Override
	public ResponseDo unsubscribeActivity(String activityId, String userId, String token) throws ApiException {
		LOG.debug("Check input parameters");
		checker.checkParameterUUID("activityId", activityId);
		checker.checkUserInfo(userId, token);

		Activity activity = activityDao.selectByPrimaryKey(activityId);
		if (activity == null) {
			LOG.warn("Cannot find activity with acitityId:{}", activityId);
			throw new ApiException("未找到活动，无法取消关注");
		}

		if (activity.getOrganizer().equals(userId)) {
			// 不能取消关注自己创建的活动
			LOG.warn("Organizer cannot quit activity create by self, activityId:{}", activityId);
			throw new ApiException("未找到活动，无法取消关注");
		}

		LOG.debug("Begin delete subscription data");
		ActivitySubscriptionKey key = new ActivitySubscriptionKey();
		key.setActivityid(activityId);
		key.setUserid(userId);
		subscriptionDao.deleteByPrimaryKey(key);

		return ResponseDo.buildSuccessResponse();
	}

}
