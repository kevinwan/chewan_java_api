package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.entity.activity.Appointment;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;


/**
 * Created by 123 on 2015/10/21.
 */
public class AppointmentCleanTask extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentCleanTask.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("======Execute appointment clean task begin==={}", DateUtil.getDate().toString());
        AppointmentDao appointmentDao = BeanUtil.getBean(AppointmentDao.class);
        Query query = Query.query(Criteria.where("deleteFlag").is(false)
                .and("activityCategory").is(Constants.ActivityCatalog.COMMON)
                .and("createTime").lte(DateUtil.getExpiredLimitTime()));
        List<Appointment> appointmentList = appointmentDao.find(query);
        LOG.info("Clean appointments counts:{}", appointmentList.size());
        for (Appointment appointment : appointmentList) {
            LOG.info("Delete appointment: {}", appointment.getAppointmentId());
        }

        appointmentDao.update(query, Update.update("deleteFlag", true));
        LOG.info("Finished delete appointments");
    }
}
