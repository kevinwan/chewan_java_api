package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.*;


/**
 * Created by 123 on 2015/10/21.
 */
public class AppointmentCleanTask extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentCleanTask.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("======Execute appointment clean task begin==={}", DateUtil.getDate().toString());
        Long expiredTime = DateUtil.getExpiredLimitTime();
        AppointmentDao appointmentDao = BeanUtil.getBean(AppointmentDao.class);

        clearCommonActivity(expiredTime, appointmentDao);

        cleanOfficialActivity(expiredTime, appointmentDao);

        LOG.info("Finished delete appointments");
    }

    private void cleanOfficialActivity(Long expiredTime, AppointmentDao appointmentDao) {
        //清理官方活动
        LOG.debug("Clean official activities join appointments and attend together");
        Long current = DateUtil.getTime();
        Query officialQuery = Query.query(Criteria.where("deleteFlag").is(false)
                .and("activityCategory").in(Constants.ActivityCatalog.OFFICIAL, Constants.ActivityCatalog.TOGETHER));
        List<Appointment> officialAppointments = appointmentDao.find(officialQuery);
        Set<String> officialIds = new HashSet<>(officialAppointments.size(), 1);
        for (Appointment item : officialAppointments) {
            officialIds.add(item.getActivityId());
        }

        OfficialActivityDao officialActivityDao = BeanUtil.getBean(OfficialActivityDao.class);
        Map<String, OfficialActivity> officialActivityMap = new HashMap<>(officialAppointments.size());
        List<OfficialActivity> officialActivityList = officialActivityDao.find(Query.query(Criteria.where("officialActivityId").in(officialIds)));
        for (OfficialActivity item : officialActivityList) {
            officialActivityMap.put(item.getOfficialActivityId(), item);
        }

        Set<String> deleteAppointmentIds = new HashSet<>(officialAppointments.size());
        Set<String> invalidAppointmentIds = new HashSet<>(officialAppointments.size());
        for (Appointment item : officialAppointments) {
            OfficialActivity officialActivity = officialActivityMap.get(item.getActivityId());
            if (officialActivity.getEnd() == null) {
                //没有结束时间
                if (officialActivity.getStart() <= expiredTime) {
                    if (item.getStatus() == Constants.AppointmentStatus.REJECT) {
                        deleteAppointmentIds.add(item.getAppointmentId());
                    } else {
                        invalidAppointmentIds.add(item.getAppointmentId());
                    }
                }
            } else {
                //有结束时间,如果结束实现小于当前时间，就删除
                if (officialActivity.getEnd() <= current) {
                    if (item.getStatus() == Constants.AppointmentStatus.REJECT) {
                        deleteAppointmentIds.add(item.getAppointmentId());
                    } else {
                        invalidAppointmentIds.add(item.getAppointmentId());
                    }
                }
            }
        }

        appointmentDao.update(Query.query(Criteria.where("appointmentId").in(deleteAppointmentIds)),
                Update.update("deleteFlag", true));
        appointmentDao.update(Query.query(Criteria.where("appointmentId").in(invalidAppointmentIds)),
                Update.update("status", Constants.AppointmentStatus.INVALID));
    }

    private void clearCommonActivity(Long expiredTime, AppointmentDao appointmentDao) {
        //清理普通活动
        LOG.debug("Clean common activity appointments");
        Criteria criteriaInvalid = Criteria.where("deleteFlag").is(false)
                .and("status").ne(Constants.AppointmentStatus.REJECT)
                .and("activityCategory").is(Constants.ActivityCatalog.COMMON)
                .and("modifyTime").lte(expiredTime);
        appointmentDao.update(Query.query(criteriaInvalid), Update.update("status", Constants.AppointmentStatus.INVALID));

        //只针对非拒绝的状态改为失效的状态
        Criteria criteriaDelete = Criteria.where("deleteFlag").is(false)
                .and("status").is(Constants.AppointmentStatus.REJECT)
                .and("activityCategory").is(Constants.ActivityCatalog.COMMON)
                .and("modifyTime").lte(expiredTime);
        appointmentDao.update(Query.query(criteriaDelete), Update.update("deleteFlag", true));
    }
}
