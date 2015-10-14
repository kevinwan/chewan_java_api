package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.EmchatTokenService;
import com.gongpingjia.carplay.service.OfficialService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2015/9/29.
 */
@Service
public class OfficialServiceImpl implements OfficialService {

    private static Logger LOG = Logger.getLogger(OfficialServiceImpl.class);

    @Autowired
    private OfficialActivityDao officialActivityDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private EmchatTokenService emchatTokenService;

    @Autowired
    private AppointmentDao appointmentDao;

    @Override
    public ResponseDo getActivityInfo(String activityId) {
        LOG.debug("getActivityInfo");

        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
        if (null == officialActivity) {

        }
        return ResponseDo.buildSuccessResponse(officialActivity);
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
        if (StringUtils.isNotEmpty(address.getDistrict())) {
            criteria.and("destination.district").is(address.getDistrict());
        }
        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.limit(limit).skip(ignore);
        List<OfficialActivity> activityList = officialActivityDao.find(query);
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

        LOG.debug("applyJoinActivity starts");

        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
        if (null == officialActivity) {
            LOG.warn("no activity match activityId");
            throw new ApiException("没有对应的官方活动");
        }

        List<String> members = officialActivity.getMembers();
        for (String member : members) {
            if (member.equals(userId)) {
                LOG.warn("Already be a member");
                throw new ApiException("已是成员，不能重复申请加入活动");
            }
        }



        User applyUser = userDao.findById(userId);
        if (null == applyUser) {
            LOG.warn("user not exist");
            throw new ApiException("用户不存在");
        }

        //判断是否超过人数设定值
        //TODO

        //加入到环信群组中
        try {
            chatThirdPartyService.addUserToChatGroup(emchatTokenService.getToken(), officialActivity.getEmchatGroupId(), applyUser.getEmchatName());
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            //添加环信用户失败；
            throw e;
        }


        //加入到 members中;
        Update update = new Update();
        update.addToSet("members", userId);
        if (StringUtils.equals(applyUser.getGender(), Constants.USER_GENDER.MALE)) {
            update.inc("maleNum", 1);
        } else {
            update.inc("femaleNum", 1);
        }
        update.inc("nowJoinNum", 1);
        officialActivityDao.update(activityId, update);

        return ResponseDo.buildSuccessResponse();
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
}
