package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.common.AreaDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Area;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.OfficialService;
import com.gongpingjia.carplay.service.util.ActivityUtil;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.Object;
import java.util.*;

/**
 * Created by Administrator on 2015/9/29.
 */
@Service
public class OfficialServiceImpl implements OfficialService {

    private static Logger LOG = LoggerFactory.getLogger(OfficialServiceImpl.class);

    @Autowired
    private OfficialActivityDao officialActivityDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private ChatCommonService chatCommonService;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public ResponseDo getActivityInfo(String officialActivityId, String userId) throws ApiException {
        LOG.debug("getActivityInfo");

        OfficialActivity officialActivity = officialActivityDao.findById(officialActivityId);
        if (null == officialActivity) {
            LOG.warn("Input officialActivityId:{} is not exist in the database", officialActivityId);
            throw new ApiException("输入参数有误");
        }

        if (officialActivity.getDeleteFlag()) {
            LOG.warn("Official activity already deleted, _id:{}", officialActivityId);
            throw new ApiException("活动信息不存在");
        }

        String localServer = CommonUtil.getLocalPhotoServer();
        String gpjServer = CommonUtil.getGPJBrandLogoPrefix();

        //构造组织者的信息
        User organizer = userDao.findById(officialActivity.getUserId());
        officialActivity.setOrganizer(new HashMap<String, Object>(2));
        officialActivity.getOrganizer().put("nickname", organizer.getNickname());
        officialActivity.getOrganizer().put("avatar", localServer + organizer.getAvatar());
        officialActivity.setCovers(new String[]{CommonUtil.getThirdPhotoServer() + officialActivity.getCover().getKey()});

        //构造返回的JSON对象
        JSONObject jsonObject = JSONObject.fromObject(officialActivity);
        if (officialActivity.getMembers() == null || officialActivity.getMembers().isEmpty()) {
            jsonObject.put("isMember", false);
            jsonObject.put("members", new ArrayList<>(0));
        } else {
            jsonObject.put("isMember", officialActivity.getMembers().contains(userId));
            //获取当前用户和成员信息
            User queryUser = userDao.findById(userId);
            LOG.debug("Build members and organizer");
            List<User> users = userDao.findByIds(officialActivity.getMembers());
            List<Map<String, Object>> members = new ArrayList<>(users.size());
            for (User user : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserId());
                map.put("nickname", user.getNickname());
                map.put("avatar", localServer + user.getAvatar());
                map.put("gender", user.getGender());
                map.put("licenseAuthStatus", user.getLicenseAuthStatus());
                map.put("distance", DistanceUtil.getDistance(queryUser.getLandmark().getLongitude(), queryUser.getLandmark().getLatitude(),
                        user.getLandmark().getLongitude(), user.getLandmark().getLatitude()));
                if (user.getCar() != null) {
                    user.getCar().setLogo(gpjServer + user.getCar().getLogo());
                    map.put("car", user.getCar());
                } else {
                    map.put("car", "");
                }
                members.add(map);
            }
            jsonObject.put("members", members);
        }

        LOG.debug("Finished build data");
        return ResponseDo.buildSuccessResponse(jsonObject);
    }

    /**
     * @param address
     * @param limit
     * @param ignore
     * @return
     */
    @Override
    public ResponseDo getActivityList(Address address, int limit, int ignore) {
        LOG.debug("getActivityList");
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(address.getProvince())) {
            criteria.and("destination.province").is(address.getProvince());
        }
        if (StringUtils.isNotEmpty(address.getCity())) {
            criteria.and("destination.city").is(address.getCity());
        }
        criteria.and("deleteFlag").is(false);//过滤已经删除了的官方活动

        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.limit(limit).skip(ignore);
        List<OfficialActivity> activityList = officialActivityDao.find(query);

        LOG.debug("Begin query organizer info");
        Map<String, User> map = new HashMap<>(activityList.size());
        for (OfficialActivity activity : activityList) {
            map.put(activity.getUserId(), null);
        }
        List<User> users = userDao.findByIds(map.keySet());
        for (User user : users) {
            map.put(user.getUserId(), user);
        }

        String localServer = CommonUtil.getLocalPhotoServer();
        String thirdServer = CommonUtil.getThirdPhotoServer();
        for (OfficialActivity activity : activityList) {
            Map<String, Object> organizer = new HashMap<String, Object>(2);
            User user = map.get(activity.getUserId());
            organizer.put("nickname", user.getNickname());
            organizer.put("avatar", localServer + user.getAvatar());
            activity.setOrganizer(organizer);
            activity.setCovers(new String[]{thirdServer + activity.getCover().getKey()});
        }


        LOG.debug("Finished build data");
        return ResponseDo.buildSuccessResponse(activityList);
    }

    /**
     * @param activityId
     * @param userId
     * @return
     * @throws ApiException 申请加入官方活动中；
     */
    @Override
    public ResponseDo applyJoinActivity(String activityId, String userId) throws ApiException {
        LOG.debug("applyJoinActivity starts check input paramters");

        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
        if (null == officialActivity) {
            LOG.warn("no activity match activityId");
            throw new ApiException("没有对应的官方活动");
        }

        List<String> members = officialActivity.getMembers();
        if (members != null && members.contains(userId)) {
            LOG.warn("Already be a member");
            throw new ApiException("已是成员，不能重复申请加入活动");
        }

        User applyUser = userDao.findById(userId);
        if (null == applyUser) {
            LOG.warn("user not exist");
            throw new ApiException("用户不存在");
        }

        checkUserOverflowed(officialActivity, applyUser);

        //加入到环信群组中
        try {
            chatThirdPartyService.addUserToChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(), applyUser.getEmchatName());
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            throw new ApiException("加入到聊天群组失败");
        }

        //加入到 members中;
        Update update = new Update();
        update.addToSet("members", userId);
        if (StringUtils.equals(applyUser.getGender(), Constants.UserGender.MALE)) {
            update.inc("maleNum", 1);
        } else {
            update.inc("femaleNum", 1);
        }
        update.inc("nowJoinNum", 1);
        officialActivityDao.update(activityId, update);

        return ResponseDo.buildSuccessResponse();
    }

    private void checkUserOverflowed(OfficialActivity officialActivity, User applyUser) throws ApiException {
        LOG.debug("Check user is already overflowed or not");
        if (Constants.OfficialActivityLimitType.TOTAL_LIMIT == officialActivity.getLimitType()) {
            //总人数限制
            if (officialActivity.getMembers() != null && officialActivity.getMembers().size() >= officialActivity.getTotalLimit()) {
                LOG.warn("Official activity is already overflow");
                throw new ApiException("活动人数已满");
            }
        } else if (Constants.OfficialActivityLimitType.GENDER_LIMIT == officialActivity.getLimitType()) {
            //性别人数限制
            if (StringUtils.equals(applyUser.getGender(), Constants.UserGender.MALE)) {
                //检查男性用户
                if (officialActivity.getMaleNum() >= officialActivity.getMaleLimit()) {
                    LOG.warn("Official activity male limit is already overflow");
                    throw new ApiException("活动人数已满");
                }
            } else {
                //检查女性用户
                if (officialActivity.getFemaleNum() >= officialActivity.getFemaleLimit()) {
                    LOG.warn("Official activity male limit is already overflow");
                    throw new ApiException("活动人数已满");
                }
            }
        }
    }

    @Override
    public ResponseDo inviteUserTogether(String activityId, String fromUserId, String toUserId, boolean transfer) throws ApiException {

        //查询是否已经邀请过了
        Appointment toFind = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId).and("applyUserId").is(fromUserId).and("invitedUserId").is(toUserId)
                .and("activityCategory").is(Constants.ActivityCatalog.OFFICIAL)));
        if (null != toFind) {
            throw new ApiException("已经邀请过此用户");
        }
        Appointment appointment = new Appointment();
        appointment.setActivityId(activityId);
        appointment.setActivityCategory(Constants.ActivityCatalog.OFFICIAL);
        appointment.setApplyUserId(fromUserId);
        appointment.setInvitedUserId(toUserId);
        appointment.setCreateTime(DateUtil.getTime());
        appointment.setModifyTime(DateUtil.getTime());
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setTransfer(transfer);

        appointmentDao.save(appointment);

        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getAreaList(Integer parentId) {
        LOG.debug("Load area data from cache first");
        JSONArray jsonArray = cacheManager.getAreaList(parentId);
        if (jsonArray != null) {
            //直接从缓存中获取
            return ResponseDo.buildSuccessResponse(jsonArray);
        }

        LOG.debug("No data exist in the cache, query database");
        List<Map<String, Object>> data = getValidAreasFromDb(parentId);
        cacheManager.setAreaList(parentId, data);

        return ResponseDo.buildSuccessResponse(data);
    }

    /**
     * 从数据库中获取该ParentId 对应的数据的信息
     *
     * @param parentId 父Id
     * @return 该父Id下面所有的区域信息
     */
    private List<Map<String, Object>> getValidAreasFromDb(Integer parentId) {
        Query query = Query.query(Criteria.where("parentId").is(parentId));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        List<Area> areas = areaDao.find(query);
        List<Map<String, Object>> validAreas = new ArrayList<>(areas.size());
        for (Area area : areas) {
            if (area.getValiad()) {
                Map<String, Object> areaMap = new HashMap<>(4, 1);
                areaMap.put("code", area.getCode());
                areaMap.put("name", area.getName());
                areaMap.put("level", area.getLevel());
                validAreas.add(areaMap);
            }
        }
        return validAreas;
    }
}