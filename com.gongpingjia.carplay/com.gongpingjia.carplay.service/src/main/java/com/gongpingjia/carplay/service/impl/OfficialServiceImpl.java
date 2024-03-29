package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.common.AreaDao;
import com.gongpingjia.carplay.dao.common.AreaRangeDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.dao.user.UserShareJoinDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Area;
import com.gongpingjia.carplay.entity.common.AreaRange;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.user.UserShareJoin;
import com.gongpingjia.carplay.service.OfficialService;
import com.gongpingjia.carplay.service.util.DistanceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
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

    @Autowired
    private AreaRangeDao areaRangeDao;

    @Autowired
    private UserShareJoinDao userShareJoinDao;

    @Override
    public ResponseDo getActivityInfo(HttpServletRequest request, String id, Integer idType, String userId) throws ApiException {
        LOG.debug("getActivityInfo");

        OfficialActivity officialActivity = checkParameters(id, idType);

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
            jsonObject.put("memberSize", 0);
            jsonObject.put("members", new ArrayList<>(0));
        } else {
            LOG.debug("Build members and organizer");
            jsonObject.put("isMember", officialActivity.getMembers().contains(userId));
            jsonObject.put("memberSize", officialActivity.getMembers().size());
        }


        LOG.debug("Finished build data");
        //去除掉members 信息；
        jsonObject.remove("members");
        return ResponseDo.buildSuccessResponse(jsonObject);
    }


    @Override
    public ResponseDo getActivityPageMemberInfo(String id, Integer idType, String userId, Integer ignore, Integer limit, Landmark landmark) throws ApiException {
        if (limit <= 0) {
            LOG.warn("limit is {}", limit);
            throw new ApiException("参数异常 limit为正整数");
        }

        OfficialActivity officialActivity = checkParameters(id, idType);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("officialActivityId", officialActivity.getOfficialActivityId());
        jsonObject.put("emchatGroupId", officialActivity.getEmchatGroupId());

        if (officialActivity.getMembers() == null || officialActivity.getMembers().isEmpty()) {
            jsonObject.put("isMember", false);
            jsonObject.put("memberSize", 0);
            jsonObject.put("members", new ArrayList<>(0));
            return ResponseDo.buildSuccessResponse(jsonObject);
        } else {
            //获取当前用户和成员信息
            User queryUser = null;
            if (!StringUtils.isEmpty(userId)) {
                queryUser = userDao.findById(userId);
            }
            jsonObject.put("isMember", officialActivity.getMembers().contains(userId));
            jsonObject.put("memberSize", officialActivity.getMembers().size());
            if (ignore >= officialActivity.getMembers().size()) {
                jsonObject.put("members", new ArrayList<>(0));
                return ResponseDo.buildSuccessResponse(jsonObject);
            }
            //获取所有的成员信息；主要是为了计算距离；
            List<User> userList = userDao.findByIds(officialActivity.getMembers());

            //获取分页用户ID列表
            List<String> pageMemberUserIds = getPageMemberIds(ignore, limit, officialActivity, queryUser, landmark, userList);

            //获取所有成员的 同去参加官方活动 appointment
            List<Appointment> appointmentList = appointmentDao.find(Query.query(Criteria.where("activityId").is(officialActivity.getOfficialActivityId())
                    .orOperator(Criteria.where("applyUserId").in(pageMemberUserIds), Criteria.where("invitedUserId").in(pageMemberUserIds))
                    .and("activityCategory").is(Constants.ActivityCatalog.TOGETHER)));
            Set<String> userIdSet = new HashSet<>(pageMemberUserIds.size() + appointmentList.size() * 2);
            for (String item : pageMemberUserIds) {
                userIdSet.add(item);
            }
            for (Appointment appointment : appointmentList) {
                userIdSet.add(appointment.getApplyUserId());
                userIdSet.add(appointment.getInvitedUserId());
            }

            Map<String, User> userMap = new HashMap<>(userIdSet.size());
            for (User user : userList) {
                if (userIdSet.contains(user.getUserId())) {
                    userMap.put(user.getUserId(), user);
                }
            }
            List<Map<String, Object>> members = new ArrayList<>(pageMemberUserIds.size());
            for (String userItemId : pageMemberUserIds) {
                User user = userMap.get(userItemId);
                Map<String, Object> map = user.buildCommonUserMap();
                User.appendDistance(map, user.getDistance());
                buildInvitedAcceptInfo(userId, appointmentList, userMap, user, map);

                members.add(map);
            }
            LOG.debug("Finished build data");

            jsonObject.put("members", members);
            return ResponseDo.buildSuccessResponse(jsonObject);
        }
    }


    private List<String> getPageMemberIds(int ignore, int limit, OfficialActivity officialActivity, User queryUser, Landmark landmark, List<User> userList) {
        int endIndex = ignore + limit;
        endIndex = endIndex > officialActivity.getMembers().size() ? officialActivity.getMembers().size() : endIndex;
        //根据距离进行排序；
        if (null != queryUser && landmark == null) {
            landmark = queryUser.getLandmark();
        }
        if (null != landmark) {
            for (User itemUser : userList) {
                itemUser.setDistance(DistanceUtil.getDistance(itemUser.getLandmark(), landmark));
            }
            Collections.sort(userList, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.getDistance() > o2.getDistance() ? 1 : -1;
                }
            });
            List<String> pageMemberUserIds = new ArrayList<>(endIndex - ignore);
            for (int index = ignore; index < endIndex; index++) {
                pageMemberUserIds.add(userList.get(index).getUserId());
            }
            return pageMemberUserIds;
        } else {
            return officialActivity.getMembers().subList(ignore, endIndex);
        }
    }

    //构造用户的邀请接收信息
    private void buildInvitedAcceptInfo(String userId, List<Appointment> appointmentList,
                                        Map<String, User> userMap, User user, Map<String, Object> map) {
        //被邀请的次数
        int invitedCount = 0;

        //邀请他人成功的 userId list
        List<String> acceptList = new ArrayList<>(appointmentList.size());

        //是否邀请过该用户
        boolean inviteFlag = false;
        //是否被该用户邀请过
        boolean beInvitedFlag = false;

//        map.put("phone", "");
//        map.put("emchatName", "");

        for (Appointment appointment : appointmentList) {
            //
            if (appointment.getInvitedUserId().equals(user.getUserId())) {
                //别人邀请他的
                invitedCount++;
                if (appointment.getStatus() == Constants.AppointmentStatus.ACCEPT) {
                    acceptList.add(appointment.getApplyUserId());
                }
            }
//            else if (appointment.getApplyUserId().equals(user.getUserId()) && appointment.getStatus() == Constants.AppointmentStatus.ACCEPT) {
//                //他邀请别人的；并且别人接受了
//                acceptList.add(appointment.getInvitedUserId());
//            }

            //我 邀请 该用户
            if (appointment.getApplyUserId().equals(userId) && appointment.getInvitedUserId().equals(user.getUserId())) {
                //邀请过该用户
                inviteFlag = true;
                map.put("inviteStatus", appointment.getStatus());
                map.put("inviteTime", appointment.getModifyTime());
//                if (appointment.getStatus() == Constants.AppointmentStatus.ACCEPT) {
//                    //jackjson 不允许有null 存在
//                    if (user.getPhone() == null) {
//                        user.setPhone("");
//                    }
//                    map.put("phone", user.getPhone());
//                    if (user.getEmchatName() == null) {
//                        user.setEmchatName("");
//                    }
//                    map.put("emchatName", user.getEmchatName());
//                }
            }

            //该用户 邀请 我
            if (appointment.getApplyUserId().equals(user.getUserId()) && appointment.getInvitedUserId().equals(userId)) {
                beInvitedFlag = true;
                map.put("beInvitedStatus", appointment.getStatus());
                map.put("beInvitedTime", appointment.getModifyTime());
//                if (appointment.getStatus() == Constants.AppointmentStatus.ACCEPT) {
//                    //jackjson 不允许有null 存在
//                    if (user.getPhone() == null) {
//                        user.setPhone("");
//                    }
//                    map.put("phone", user.getPhone());
//                    if (user.getEmchatName() == null) {
//                        user.setEmchatName("");
//                    }
//                    map.put("emchatName", user.getEmchatName());
//                }
            }

        }
        //没要邀请过
        if (!inviteFlag) {
            map.put("inviteStatus", Constants.AppointmentStatus.INITIAL);
            map.put("inviteTime", 0);
        }
        //没有被邀请过
        if (!beInvitedFlag) {
            map.put("beInvitedStatus", Constants.AppointmentStatus.INITIAL);
            map.put("beInvitedTime", 0);
        }


        //该用户被别人邀请的次数
        map.put("beInvitedCount", invitedCount);
        //该用户邀请别人并且被同意的次数
        map.put("acceptCount", acceptList.size());

        //接受了他的邀请的用户列表信息
        List<Map<String, Object>> acceptMembers = new ArrayList<>();
        for (String memberId : acceptList) {
//            Map<String, Object> acceptMember = new HashMap<>(4, 1);
            User acceptUser = userMap.get(memberId);
            if (null == acceptUser) {
                LOG.error("the user is not the official activity member userId is {} activityId is:", memberId);
            }
//            acceptMember.put("userId", acceptUser.getUserId());
//            acceptMember.put("nickname", acceptUser.getNickname());
//            acceptMember.put("avatar", localServer + acceptUser.getAvatar());
            acceptMembers.add(acceptUser.buildBaseUserMap());
        }
        map.put("acceptMembers", acceptMembers);
    }

    private OfficialActivity checkParameters(String id, Integer idType) throws ApiException {
        if (!Constants.OfficialInfoIdType.TYPE_LIST.contains(idType)) {
            LOG.warn("idType is illegal idType is :{}", idType);
            throw new ApiException(("idType 值非法"));
        }
        OfficialActivity officialActivity = null;
        if (idType.equals(Constants.OfficialInfoIdType.TYPE_OFFICIAL)) {
            officialActivity = officialActivityDao.findById(id);
        } else {
            officialActivity = officialActivityDao.findOne(Query.query(Criteria.where("emchatGroupId").is(id)));
        }
        if (null == officialActivity) {
            LOG.warn("Input idType:{} id:{} is not exist in the database", idType, id);
            throw new ApiException("活动信息不存在");
        }

        if (officialActivity.getDeleteFlag()) {
            LOG.warn("Official activity already deleted, _id:{}", officialActivity.getOfficialActivityId());
            throw new ApiException("活动信息不存在");
        }

        //未上架
        if (!officialActivity.getOnFlag()) {
            LOG.warn("当前官方活动未上架");
            throw new ApiException("该活动未上架");
        }
        return officialActivity;
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
        criteria.and("onFlag").is(true);       //过滤 未上架的官方活动；
        criteria.and("start").gt(DateUtil.getTime());  //过滤已下架的活动；start>当前时间

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
            throw new ApiException("官方活动不存在");
        }

        if (!officialActivity.getOnFlag()) {
            LOG.warn("the activity onFlag is false");
            throw new ApiException("官方活动未上架");
        }

        //报名时间超过开始时间 该 活动已经下架
        if (DateUtil.getDate().getTime() > officialActivity.getStart()) {
            LOG.warn("the activity is offline");
            throw new ApiException("官方活动已经下架");
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
            JSONObject result = chatThirdPartyService.addUserToChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(), applyUser.getEmchatName());
            LOG.info("Join result:{}", result);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            throw new ApiException("加入到聊天群组失败");
        }

        //将当前活动添加到约会信息中，已经接受，并且是官方活动类型
        saveAppointment(activityId, userId, officialActivity.getUserId(), false, Constants.AppointmentStatus.ACCEPT, Constants.ActivityCatalog.OFFICIAL, "");

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

        LOG.debug("Send emchat message");
        User user = userDao.findById(officialActivity.getUserId());
        String message = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.status", "你已成功加入\"{0}\"活动"),
                officialActivity.getTitle());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.ACTIVITY_STATE,
                applyUser.getEmchatName(), message);

        modifyChatGroup(officialActivity, userId);

        return ResponseDo.buildSuccessResponse();
    }


    @Override
    public ResponseDo quitJoinActivity(String activityId, String userId) throws ApiException {
        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
        if (null == officialActivity) {
            throw new ApiException("活动不存在");
        }
        User user = userDao.findById(userId);
        if (null == user) {
            throw new ApiException("用户不存在");
        }
        if (officialActivity.getMembers() == null || officialActivity.getMembers().isEmpty() || !officialActivity.getMembers().contains(userId)) {
            throw new ApiException("用户没有参加该活动");
        }

        //删除emchatGroup中的信息
        try {
            JSONObject result = chatThirdPartyService.deleteUserFromChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(), user.getEmchatName());
            LOG.info("emchat remove result:{}", result);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            throw new ApiException("退出环信群组失败");
        }

        List<String> members = officialActivity.getMembers();
        //appointment中的信息删除；
        Criteria criteria = Criteria.where("activityId").is(officialActivity.getOfficialActivityId());
        criteria.orOperator(Criteria.where("applyUserId").is(userId), Criteria.where("invitedUserId").is(userId));
        criteria.and("activityCategory").in(Arrays.asList(Constants.ActivityCatalog.OFFICIAL, Constants.ActivityCatalog.TOGETHER));
        criteria.and("deleteFlag").is(false);
        appointmentDao.update(Query.query(criteria), Update.update("deleteFlag", true));

        //群聊信息更改；
        int index = members.indexOf(userId);
        members.remove(index);
        // 头像作为一部分 重置头像
        if (index < Constants.CHATGROUP_MAX_PHOTO_COUNT && index != -1) {
            setChatGroupDescription(members, officialActivity);
        }

        //成员信息更改
        Update update = Update.update("members", members);
        //男女数目 与总人数
        if (StringUtils.equals(user.getGender(), Constants.UserGender.MALE)) {
            update.inc("maleNum", -1);
        } else {
            update.inc("femaleNum", -1);
        }
        update.inc("nowJoinNum", -1);

        officialActivityDao.update(officialActivity.getOfficialActivityId(), update);

        return ResponseDo.buildSuccessResponse();
    }

    private void setChatGroupDescription(List<String> members, OfficialActivity officialActivity) throws ApiException {
        if (null == members || members.isEmpty()) {
            chatThirdPartyService.modifyChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(),
                    officialActivity.getTitle(), "");
        } else {
            StringBuilder builder = new StringBuilder();
            List<User> users = userDao.findByIds(officialActivity.getMembers().size() > Constants.CHATGROUP_MAX_PHOTO_COUNT ?
                    officialActivity.getMembers().subList(0, Constants.CHATGROUP_MAX_PHOTO_COUNT) : officialActivity.getMembers());
            String localServer = CommonUtil.getLocalPhotoServer();
            for (User item : users) {
                builder.append(localServer).append(item.getAvatar()).append(";");
            }
            chatThirdPartyService.modifyChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(),
                    officialActivity.getTitle(), builder.substring(0, builder.length() - 1).replace("/", "|"));
        }
    }

    //修改聊天群组的description信息，用于记录群组的图像
    private void modifyChatGroup(OfficialActivity officialActivity, String joinUserId) throws ApiException {
        if (officialActivity.getMembers() == null || officialActivity.getMembers().isEmpty()) {
            User joinUser = userDao.findById(joinUserId);
            chatThirdPartyService.modifyChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(),
                    officialActivity.getTitle(), (CommonUtil.getLocalPhotoServer() + joinUser.getAvatar()).replace("/", "|"));
        } else {
            //用户群聊的图片数量限制4
            StringBuilder builder = new StringBuilder();
            officialActivity.getMembers().add(joinUserId);
            List<User> users = userDao.findByIds(officialActivity.getMembers().size() > Constants.CHATGROUP_MAX_PHOTO_COUNT ?
                    officialActivity.getMembers().subList(0, Constants.CHATGROUP_MAX_PHOTO_COUNT) : officialActivity.getMembers());
            String localServer = CommonUtil.getLocalPhotoServer();
            for (User item : users) {
                builder.append(localServer).append(item.getAvatar()).append(";");
            }
            chatThirdPartyService.modifyChatGroup(chatCommonService.getChatToken(), officialActivity.getEmchatGroupId(),
                    officialActivity.getTitle(), builder.substring(0, builder.length() - 1).replace("/", "|"));
        }
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
    public ResponseDo inviteUserTogether(String activityId, String fromUserId, String toUserId, boolean transfer, String message) throws ApiException {
        LOG.debug("user:{} invited user:{} together", fromUserId, toUserId);
        //查询是否已经邀请过了
        Appointment toFind = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId)
                .and("applyUserId").is(fromUserId).and("invitedUserId").is(toUserId)
                .and("activityCategory").is(Constants.ActivityCatalog.TOGETHER)));
        Appointment revertFind = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId)
                .and("invitedUserId").is(fromUserId).and("applyUserId").is(toUserId)
                .and("activityCategory").is(Constants.ActivityCatalog.TOGETHER)));
        if (null != toFind) {
            LOG.warn("User already has bean invited by each other");
            throw new ApiException("已经邀请过此用户");
        }
        if (null != revertFind) {
            if (revertFind.getStatus() == Constants.AppointmentStatus.APPLYING) {
                LOG.warn("the other user is invited the current user");
                throw new ApiException("该用户正在邀请你，请在动态中查看");
            }
            if (revertFind.getStatus() == Constants.AppointmentStatus.ACCEPT) {
                throw new ApiException("你已经同意了该用户的同去邀请");
            }
        }

        //校验 只能 参加了该活动的人 能够邀请 和 被邀请
        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
        if (null == officialActivity) {
            LOG.warn("活动不存在");
            throw new ApiException("该活动不存在");
        }

        if (!officialActivity.getOnFlag()) {
            LOG.warn("活动未上架");
            throw new ApiException("该活动未上架");
        }

        if (DateUtil.getDate().getTime() > officialActivity.getStart()) {
            LOG.warn("活动已经开始 即 已下架");
            throw new ApiException("该活动已经下架");
        }

        List<String> members = officialActivity.getMembers();
        if (null == members || members.size() == 0) {
            LOG.warn("members size is 0");
            throw new ApiException("该活动没有参加成员");
        }
        if (!members.contains(fromUserId)) {
            LOG.warn("this userId not in the members userId is:{}", fromUserId);
            throw new ApiException("你没有参加该活动");

        }
        if (!members.contains(toUserId)) {
            LOG.warn("this userId not in the members userId is {}", toUserId);
            throw new ApiException("被邀请的成员并没有参加该活动");
        }

        saveAppointment(activityId, fromUserId, toUserId, transfer, Constants.AppointmentStatus.APPLYING, Constants.ActivityCatalog.TOGETHER, message);

        User fromUser = userDao.findById(fromUserId);
        User toUser = userDao.findById(toUserId);
        String pushMsg = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.official.activity.invite",
                "{0}邀请您同去参加{1}活动"), fromUser.getNickname(), officialActivity.getTitle());
        chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.ACTIVITY_STATE,
                toUser.getEmchatName(), pushMsg);

        return ResponseDo.buildSuccessResponse();
    }

    private void saveAppointment(String activityId, String fromUserId, String toUserId, boolean transfer, int status, String category, String message) {
        User fromUser = userDao.findById(fromUserId);
        User toUser = userDao.findById(toUserId);
        OfficialActivity activity = officialActivityDao.findById(activityId);
        Long current = DateUtil.getTime();

        Appointment appointment = new Appointment();
        appointment.setActivityId(activityId);
        appointment.setActivityCategory(category);
        appointment.setApplyUserId(fromUserId);
        appointment.setInvitedUserId(toUserId);
        appointment.setCreateTime(current);
        appointment.setModifyTime(current);
        appointment.setStatus(status);
        appointment.setTransfer(transfer);
        appointment.setMessage(message);

        appointment.setEstabPoint(fromUser.getLandmark());
        appointment.setDistance(DistanceUtil.getDistance(fromUser.getLandmark(), toUser.getLandmark()));
        appointment.setDestination(activity.getDestination());
        appointment.setType(activity.getTitle());
        appointmentDao.save(appointment);
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


    @Override
    public ResponseDo getAreaRangeInfo(Integer code) throws ApiException {
        AreaRange areaRange = areaRangeDao.findOne(Query.query(Criteria.where("code").is(code)));
        if (null == areaRange) {
            throw new ApiException("未找到区域信息");
        }
        return ResponseDo.buildSuccessResponse(areaRange);
    }

    @Override
    public ResponseDo unregisterRecordUserPhone(String officialActivityId, String phone) {
        LOG.debug("Check inpur param officialActivityId:{}", officialActivityId);
        OfficialActivity officialActivity = officialActivityDao.findById(officialActivityId);
        if (officialActivity == null) {
            LOG.warn("Input parameter officialActivityId:{} is no activity exist", officialActivityId);
            return ResponseDo.buildFailureResponse("输入参数有误");
        }

        UserShareJoin shareJoin = userShareJoinDao.findOne(phone, officialActivityId, Constants.ActivityCatalog.OFFICIAL);
        if (shareJoin == null) {
            LOG.debug("Record user join info");
            shareJoin = new UserShareJoin();
            shareJoin.setActivityId(officialActivityId);
            shareJoin.setPhone(phone);
            shareJoin.setActivityType(Constants.ActivityCatalog.OFFICIAL);
            shareJoin.setCreateTime(DateUtil.getTime());
            userShareJoinDao.save(shareJoin);
        }
        return ResponseDo.buildSuccessResponse();
    }
}