package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.entity.activity.Activity;
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
 * Created by licheng on 2015/10/21.
 */
public class ActivityCleanTask extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityCleanTask.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("======Execute activity clean task begin==={}", DateUtil.getDate().toString());
        ActivityDao activityDao = BeanUtil.getBean(ActivityDao.class);
        Query query = Query.query(Criteria.where("deleteFlag").is(false)
                .and("createTime").lte(DateUtil.getExpiredLimitTime()));
        List<Activity> activityList = activityDao.find(query);
        LOG.info("Clean activities counts:{}", activityList.size());
        for (Activity activity : activityList) {
            LOG.info("Delete activity: {}", activity.getActivityId());
        }

        activityDao.update(query, Update.update("deleteFlag", true));
        LOG.info("Finished delete activity");
    }
}
