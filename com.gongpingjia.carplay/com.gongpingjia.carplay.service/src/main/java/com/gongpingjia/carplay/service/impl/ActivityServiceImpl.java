package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.ActivityIntention;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.EmchatTokenService;
import com.gongpingjia.carplay.service.util.ActivityUtil;
import com.gongpingjia.carplay.service.util.ActivityWeight;
import com.gongpingjia.carplay.service.util.FetchUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by heyongyu on 2015/9/22.
 */
@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private EmchatTokenService emchatTokenService;

    @Autowired
    private UserDao userDao;


    @Autowired
    private ActivityDao activityDao;


    @Autowired
    private AppointmentDao appointmentDao;

    @Override
    public ResponseDo activityRegister(String userId, Activity activity) throws ApiException {
        LOG.debug("activityRegister");
        activity.setActivityId(null);
        //设置活动的创建人ID
        activity.setUserId(userId);
        List<String> memberIds = new ArrayList<String>(1);
        //创建人默认加入到活动成员列表中
        memberIds.add(userId);
        activity.setMembers(memberIds);
        activity.setCreateTime(new Date().getTime());

        activityDao.save(activity);
        try {
            String groupId = createEmchatGroup(activity);
            activityDao.update(Query.query(Criteria.where("_id").is(activity.getActivityId())), Update.update("emchatGroupId", groupId));
        } catch (ApiException e) {
            //创建环信群组失败的时候，需要删除mongodb中的对应的群组；
            activityDao.deleteById(activity.getActivityId());
            LOG.error(e.getMessage(), e);
            throw new ApiException("创建环信群组失败");
        }
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getActivityInfo(String userId, String activityId) throws ApiException {
        LOG.debug("getActivityInfo");
        Activity activity = activityDao.findById(activityId);
        if (null == activity) {
            LOG.warn("activity not exist");
            throw new ApiException("活动id 找不到对应的活动");
        }
        User organizer = userDao.findById(activity.getUserId());
        if (null == organizer) {
            LOG.warn("organizer is null");
            throw new ApiException("该活动找不到对应的 User");
        }
        organizer.hideSecretInfo();
        activity.setOrganizer(organizer);
        return ResponseDo.buildSuccessResponse(activity);
    }

    /**
     * 基础参数转换规则；例如 province 需要转换成 destAddress.province 进行查询
     *
     * @param transParams
     * @param request     request中需要其他的信息 ；limit ignore 分页信息；longitude latitude 当前的经纬度；maxDistance 最大搜索距离 不是必须的；
     *                    step 1: 将基础转换参数 转换成查询参数；
     *                    step 2：添加 周边最大距离 以及 最近时间内的 查询参数；
     *                    step 3： 查询出基础Activity List
     *                    step 4：对技术list 中的activity进行 权重计算 以及排序；
     *                    step 5： 根据 limit 和 ignore 信息 取出 排序后的 activity list
     */
    @Override
    public ResponseDo getNearActivityList(Map<String, String> transParams, HttpServletRequest request, String userId) throws ApiException {
        LOG.debug("getNearActivityList");
        //从request读取初始化信息；
        //limit 是分页参数
        int limit = 10;
        //ignore是跳过的参数
        int ignore = 0;
        //默认的最大距离参数 如果没有传递最大距离 则实用默认的最大距离

        double maxDistance = Double.parseDouble(PropertiesUtil.getProperty("activity.default_max_distance", String.valueOf(ActivityWeight.DEFAULT_MAX_DISTANCE)));

        String limitStr = request.getParameter("limit");
        String ignoreStr = request.getParameter("ignore");
        if (StringUtils.isNotEmpty(limitStr)) {
            limit = Integer.parseInt(limitStr);
        }
        if (StringUtils.isNotEmpty(ignoreStr)) {
            ignore = Integer.parseInt(ignoreStr);
        }
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            LOG.error("longitude or latitude has not init");
            throw new ApiException("param not match");
        }
        LOG.debug("longitude is:" + longitude);
        LOG.debug("latitude is:" + latitude);
        String maxDistanceStr = request.getParameter("maxDistance");
        LOG.debug("maxDistanceStr is:" + maxDistanceStr);
        if (StringUtils.isNotEmpty(maxDistanceStr)) {
            maxDistance = Double.parseDouble(maxDistanceStr);
        }
        Landmark landmark = new Landmark();
        landmark.setLatitude(Double.parseDouble(latitude));
        landmark.setLongitude(Double.parseDouble(longitude));

        LOG.debug("init Query");
        //从request读取基础查询参数；
        Criteria criteria = initQuery(request, transParams);


        // 添加 距离  时间 查询参数；

        long maxPubTime = Long.parseLong(PropertiesUtil.getProperty("activity.default_max_pub_time", String.valueOf(ActivityWeight.MAX_PUB_TIME)));

        //查询创建在此时间之前的活动；
        long gtTime = DateUtil.addTime(new Date(), Calendar.MINUTE, (0 - (int) maxPubTime));
        criteria.and("createTime").gte(gtTime);

        //查询在最大距离内的 活动；
        criteria.and("estabPoint").near(new Point(landmark.getLongitude(), landmark.getLatitude())).maxDistance(maxDistance);

        //非用户自己创建的活动
        criteria.and("userId").ne(userId);

        //获得所有的满足基础条件的活动；
        List<Activity> allActivityList = activityDao.find(Query.query(criteria));
        if (allActivityList.isEmpty()) {
            LOG.warn("all Activity list is empty");
            return ResponseDo.buildSuccessResponse("[]");
        }

        LOG.debug("allActivityList size is:" + allActivityList.size());

        //查询出Activity的 组织者，并初始化
        initOrganizer(allActivityList);

        /**
         *  对所有的基础条件活动进行权重打分，并且排序
         * 此处是查询条件下的内存分页；现业务下没有更好的方式；需要全部排序 就需要将所有的数据读入到内存中进行计算；
         * 优化方式可以 实用缓存方式 ，对于用户重复的请求可以缓存起来，利用version 更改机制 探讨一下；
         */
        List<Activity> rltList = ActivityUtil.getSortResult(allActivityList, new Date(), landmark, maxDistance, maxPubTime, ignore, limit);
        LOG.debug("rltList size is:" + rltList.size());

        JSONArray jsonArray = new JSONArray();
        for (Activity activity : rltList) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("type", activity.getType());
            jsonItem.put("activityId", activity.getActivityId());
            jsonItem.put("distance", activity.getDistance());
            jsonItem.put("pay", activity.getPay());
            jsonItem.put("transfer", activity.isTransfer());

            JSONObject itemOrganizer = new JSONObject();
            itemOrganizer.put("userId", activity.getOrganizer().getUserId());
            itemOrganizer.put("nickname", activity.getOrganizer().getNickname());
            itemOrganizer.put("avatar", activity.getOrganizer().getAvatar());
            itemOrganizer.put("gender", activity.getOrganizer().getGender());
            itemOrganizer.put("age", activity.getOrganizer().getAge());
            jsonItem.put("organizer", itemOrganizer);
            jsonArray.add(jsonItem);
        }
        return ResponseDo.buildSuccessResponse(jsonArray);
    }

    @Override
    public Criteria initQuery(HttpServletRequest request, Map<String, String> transMap) {
        //初始化query信息
        Criteria criteria = new Criteria();
        for (Map.Entry<String, String> transItem : transMap.entrySet()) {
            String requestVal = request.getParameter(transItem.getKey());
            if (StringUtils.isNotEmpty(requestVal)) {
                //付款类型转换 请我变成 请客  请客变成 请我 AA不变；
                if (transItem.getKey().equals("pay")) {
                    if (requestVal.equals(Activity.PAY_TYPE_INVITED)) {
                        criteria.and(transItem.getValue()).is(Activity.PAY_TYPE_TREAT);
                    } else if (requestVal.equals(Activity.PAY_TYPE_TREAT)) {
                        criteria.and(transItem.getKey()).is(Activity.PAY_TYPE_INVITED);
                    } else {
                        criteria.and(transItem.getKey()).is(Activity.PAY_TYPE_AA);
                    }
                } else {
                    criteria.and(transItem.getValue()).is(requestVal);
                }
            }
        }
        return criteria;
    }

    @Override
    public ResponseDo sendAppointment(String activityId, String userId, Appointment appointment) throws ApiException {
        Activity activity = activityDao.findOne(Query.query(Criteria.where("activityId").is(activityId)));
        if (activity == null) {
            LOG.warn("No activity exist : {}", activityId);
            throw new ApiException("未找到该活动");
        }

        List<String> members = activity.getMembers();
        for (String member : members) {
            if (member.equals(userId)) {
                LOG.warn("Already be a member");
                throw new ApiException("已是成员，不能重复申请加入活动");
            }
        }

        Appointment appointmentData = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId)
                .and("applyUserId").is(userId)
                .and("status").is(Constants.AppointmentStatus.APPLYING)));
        if (appointmentData != null) {
            LOG.warn("already applying for this activity");
            throw new ApiException("该活动已处于申请中，请勿重复申请");
        }

        Long current = DateUtil.getTime();
        appointment.setActivityId(activity.getActivityId());
        appointment.setApplyUserId(userId);
        appointment.setInvitedUserId(activity.getUserId());
        appointment.setCreateTime(current);
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setModifyTime(current);

        appointment.setActivityCategory(Constants.ActivityCatalog.COMMON);

        appointmentDao.save(appointment);

        return ResponseDo.buildSuccessResponse();
    }


    /**
     * 根据活动信息创建聊天群
     *
     * @param activity 活动信息
     * @throws ApiException 创建群聊失败时
     */
    private String createEmchatGroup(Activity activity) throws ApiException {
        LOG.debug("Begin create chat group");
        User owner = userDao.findById(activity.getUserId());
        JSONObject json = chatThirdPartyService.createChatGroup(emchatTokenService.getToken(), activity.getType(), activity.getActivityId(), owner.getEmchatName(), null);
        if (json.isEmpty()) {
            LOG.warn("Failed to create chat group");
            throw new ApiException("创建聊天群组失败");
        }
        String groupId = json.getJSONObject("data").getString("groupid");
        return groupId;

    }

    /**
     * 申请加入活动
     *
     * @param appointmentId
     * @param userId
     * @param acceptFlag
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo processAppointment(String appointmentId, String userId, boolean acceptFlag) throws ApiException {
        LOG.debug("applyJoinActivity");
        Appointment appointment = checkAppointment(appointmentId, userId);

//        Activity activity = checkAppointmentActivity(appointment);
//
//        //判断申请用户是否存在
//        User applyUser = userDao.findById(appointment.getApplyUserId());
//        if (applyUser == null) {
//            LOG.warn("申请用户不存在");
//            throw new ApiException("申请用户不存在");
//        }

        //不同意
        if (!acceptFlag) {
            appointmentDao.update(appointment.getAppointmentId(), Update.update("status", Constants.AppointmentStatus.ACCEPT));
            return ResponseDo.buildSuccessResponse();
        }

//        //同意
//        //添加到环信群组中；
//        try {
//            chatThirdPartyService.addUserToChatGroup(emchatTokenService.getToken(), activity.getEmchatGroupId(), applyUser.getEmchatName());
//        } catch (ApiException e) {
//            LOG.warn(e.getMessage(), e);
//            //添加环信用户失败；
//            throw new ApiException("添加到聊天群组失败");
//        }

//        //activity 中members 中添加该用户;
//        Update activityUpdate = new Update();
//        activityUpdate.addToSet("members", appointment.getApplyUserId());
//        activityDao.update(activity.getActivityId(), activityUpdate);

        //appointment中 更新status字段；
        appointmentDao.update(appointment.getAppointmentId(), Update.update("status", Constants.AppointmentStatus.ACCEPT));

        return ResponseDo.buildSuccessResponse();
    }

    /**
     * 检查邀请的申请对应的活动信息
     *
     * @param appointment
     * @return
     * @throws ApiException
     */
    private Activity checkAppointmentActivity(Appointment appointment) throws ApiException {
        //判断活动是否存在
        Activity activity = activityDao.findById(appointment.getActivityId());
        if (activity == null) {
            LOG.warn("activity not exist");
            throw new ApiException("活动不存在");
        }

        for (String memberId : activity.getMembers()) {
            if (memberId.equals(appointment.getApplyUserId())) {
                LOG.warn("user has in the activity");
                throw new ApiException("用户已经在当前活动中");
            }
        }

        return activity;
    }

    /**
     * 检查appointment对象与user是否相符，如果不相符就不能操作
     *
     * @param appointmentId
     * @param userId
     * @return
     * @throws ApiException
     */
    private Appointment checkAppointment(String appointmentId, String userId) throws ApiException {
        //判断申请appointment 是否存在
        Appointment appointment = appointmentDao.findById(appointmentId);
        //
        if (appointment == null) {
            LOG.warn("appoint not exist");
            throw new ApiException("该邀请不存在");
        }

        if (!userId.equals(appointment.getInvitedUserId())) {
            LOG.warn("appoint not belong this user");
            throw new ApiException("输入参数有误");
        }

        if (!Constants.AppointmentStatus.APPLYING.equals(appointment.getStatus())) {
            LOG.warn("appoint has done");
            throw new ApiException("该邀请已经处理过了");
        }

        return appointment;
    }


    /**
     * 将activityList中的User信息从数据库中查询出来，并初始化
     *
     * @param activityList
     * @throws ApiException
     */
    private void initOrganizer(List<Activity> activityList) throws ApiException {
        Set<String> userIdSet = new HashSet<>(activityList.size());
        for (Activity activity : activityList) {
            userIdSet.add(activity.getUserId());
        }
        List<User> users = userDao.findByIds(userIdSet);

        for (Activity activity : activityList) {
            User organizer = FetchUtil.getUserFromList(users, activity.getUserId());
            if (null == organizer) {
                throw new ApiException("数据非法 该Activity没有找到对应的Organizer");
            }
//            organizer.hideSecretInfo();
            activity.setOrganizer(organizer);
        }
    }
}
