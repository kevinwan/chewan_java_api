package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.service.OfficialService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private AppointmentDao appointmentDao;

    @Override
    public ResponseDo getActivityInfo(String activityId) {
        LOG.debug("getActivityInfo");

        OfficialActivity officialActivity = officialActivityDao.findById(activityId);
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

        Criteria criteria = Criteria.where("destination.province").is(address.getProvince()).where("destination.city").is(address.getCity())
                .where("destination.district").is(address.getDistrict());
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
        Appointment appointment = appointmentDao.findOne(Query.query(Criteria.where("activityId").is(activityId).and("applyUserId").is(userId).and("status").is(Constants.AppointmentStatus.APPLYING)));
        if (appointment != null) {
            LOG.warn("already applying for this activity");
            throw new ApiException("该活动已处于申请中，请勿重复申请");
        }

        appointment = new Appointment();
        appointment.setActivityId(officialActivity.getOfficialActivityId());
        appointment.setApplyUserId(userId);
        appointment.setInvitedUserId(officialActivity.getUserId());
        appointment.setCreateTime(DateUtil.getTime());
        appointment.setStatus(Constants.AppointmentStatus.APPLYING);
        appointment.setCreateTime(DateUtil.getTime());
        appointment.setModifyTime(DateUtil.getTime());

        appointment.setActivityCategory(Constants.ActivityCatalog.OFFICIAL);

        appointmentDao.save(appointment);

        return ResponseDo.buildSuccessResponse();
    }
}
