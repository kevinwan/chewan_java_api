package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.EmchatTokenService;
import com.gongpingjia.carplay.util.ActivityUtil;
import com.gongpingjia.carplay.util.ActivityWeight;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    private ParameterChecker parameterChecker;


    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private ActivityUtil activityUtil;

    @Autowired
    private AppointmentDao appointmentDao;

    @Override
    public ResponseDo activityRegister(String userId, String token, Activity activity) throws ApiException {
        parameterChecker.checkUserInfo(userId, token);
        activity.setActivityId(null);
        activity.setUserId(userId);
        List<String> memberIds = new ArrayList<String>(1);
        memberIds.add(userId);
        activity.setMembers(memberIds);
        activityDao.save(activity);
//        createEmchatGroup(activity);
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getActivityInfo(String userId, String token, String activityId) throws ApiException {
        parameterChecker.checkUserInfo(userId, token);
        Activity activity = activityDao.findById(activityId);
        User organizer = userDao.findById(activity.getUserId());
        JSONObject jsonObject = JSONObject.fromObject(activity);
        jsonObject.put("organizer", organizer);
        return ResponseDo.buildSuccessResponse(jsonObject.toString());
    }

    /**
     * 基础参数转换规则；例如 province 需要转换成 destAddress.province 进行查询
     *
     * @param transParams
     * @param request     step 1: 将基础转换参数 转换成查询参数；
     *                    step 2：添加 周边最大距离 以及 最近时间内的 查询参数；
     *                    step 3： 查询出基础Activity List
     *                    step 4：对技术list 中的activity进行 权重计算 以及排序；
     *                    step 5： 根据 limit 和 ignore 信息 取出 排序后的 activity list
     */
    @Override
    public ResponseDo getNearActivityList(Map<String, String> transParams, HttpServletRequest request) throws ApiException {
        //从request读取初始化信息；
        //limit 是分页参数
        int limit = 10;
        //ignore是跳过的参数
        int ignore = 0;
        //默认的最大距离参数
        double maxDistance = ActivityWeight.DEFAULT_MAX_DISTANCE;
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
            throw new ApiException("param not match");
        }
        String maxDistanceStr = request.getParameter("maxDistance");
        if (StringUtils.isNotEmpty(maxDistanceStr)) {
            maxDistance = Double.parseDouble(maxDistanceStr);
        }
        Landmark landmark = new Landmark();
        landmark.setLatitude(Double.parseDouble(latitude));
        landmark.setLongitude(Double.parseDouble(longitude));

        //从request读取基础查询参数；
        Query query = initQuery(request, transParams);
        // 添加 距离  时间 查询参数；
        Criteria criteria = new Criteria();
        //查询创建在此时间之前的活动；
        long gtTime = DateUtil.addTime(new Date(), Calendar.MINUTE, (0 - (int) ActivityWeight.MAX_PUB_TIME));
        //查询在最大距离内的 活动；
        Criteria.where("createTime").gte(gtTime).where("landMark").near(new Point(landmark.getLongitude(), landmark.getLatitude())).maxDistance(maxDistance);
        query.addCriteria(criteria);
        //获得所有的满足基础条件的活动；
        List<Activity> allActivityList = activityDao.find(query);
        //TODO
        /**
         *  对所有的基础条件活动进行权重打分，并且排序
         * 此处是查询条件下的内存分页；现业务下没有更好的方式；需要全部排序 就需要将所有的数据读入到内存中进行计算；
         * 优化方式可以 实用缓存方式 ，对于用户重复的请求可以缓存起来，利用version 更改机制 探讨一下；
         */
        List<ActivityWeight> rltList = activityUtil.getPageInfo(activityUtil.sortActivityList(allActivityList, new Date(), landmark, maxDistance), ignore, limit);
        JSONArray jsonArray = new JSONArray();
        Set<String> userIds = new HashSet<>();
        for (ActivityWeight activityWeight : rltList) {
            userIds.add(activityWeight.getActivity().getUserId());
        }
        List<User> userList = userDao.findByIds((String[]) userIds.toArray());
        for (ActivityWeight activityWeight : rltList) {
            JSONObject item = JSONObject.fromObject(activityWeight.getActivity());
            item.put("organizer", findById(userList, activityWeight.getActivity().getUserId()));
            item.put("distance", activityWeight.getDistance());
            jsonArray.add(item);
        }
        return ResponseDo.buildSuccessResponse(jsonArray.toString());
    }

    @Override
    public Query initQuery(HttpServletRequest request, Map<String, String> transMap) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        for (Map.Entry<String, String> transItem : transMap.entrySet()) {
            String requestVal = request.getParameter(transItem.getKey());
            if (StringUtils.isNotEmpty(requestVal)) {
                criteria.where(transItem.getValue()).is(requestVal);
            }
        }
        query.addCriteria(criteria);
        return query;
    }

    @Override
    public ResponseDo sendAppointment(String activityId, String userId, String token) throws ApiException {
        parameterChecker.checkUserInfo(userId, token);
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
        Appointment appointment = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId).and("applyUserId").is(userId).and("status").is(Constants.AppointmentStatus.APPLYING)));
        if (appointment != null) {
            LOG.warn("already applying for this activity");
            throw new ApiException("该活动已处于申请中，请勿重复申请");
        }

        appointment = new Appointment();
        appointment.setActivityId(activity.getActivityId());
        appointment.setApplyUserId(userId);
        appointment.setInvitedUserId(activity.getUserId());
        appointment.setCreateTime(DateUtil.getTime());
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setCreateTime(DateUtil.getTime());
        appointment.setModifyTime(DateUtil.getTime());

        appointmentDao.save(appointment);

        return ResponseDo.buildSuccessResponse();
    }


    /**
     * 根据活动信息创建聊天群
     *
     * @param activity 活动信息
     * @throws ApiException 创建群聊失败时
     */
    private void createEmchatGroup(Activity activity) throws ApiException {
        LOG.debug("Begin create chat group");
        User owner = userDao.findById(activity.getUserId());
        JSONObject json = chatThirdPartyService.createChatGroup(emchatTokenService.getToken(), activity.getType(), activity.getActivityId(), owner.getNickname(), activity.getMembers());
        if (json.isEmpty()) {
            LOG.warn("Failed to create chat group");
            throw new ApiException("创建聊天群组失败");
        }
    }

    private User findById(List<User> users, String id) {
        for (User user : users) {
            if (user.getUserId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
