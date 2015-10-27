package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by licheng on 2015/10/26.
 */
public class PushInfoCleanTask extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(PushInfoCleanTask.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("============Begin clean all pushInfo, execute at:{}", DateUtil.getDate());

        PushInfoDao pushInfoDao = BeanUtil.getBean(PushInfoDao.class);
        pushInfoDao.update(Query.query(Criteria.where("createTime").lte(DateUtil.getExpiredLimitTime())
                .and("deleteFlag").is(false)), Update.update("deleteFlag", true));

        LOG.info("============Finished clean all pushInfo data");
    }
}
