package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.impl.ChatCommonService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/10/21.
 */
public class AppointmentExpiredRemindTask extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(AppointmentExpiredRemindTask.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("======Execute appointment expired remind begin at:{}", DateUtil.getDate());

        AppointmentDao appointmentDao = BeanUtil.getBean(AppointmentDao.class);
        ChatCommonService chatCommonService = BeanUtil.getBean(ChatCommonService.class);
        ChatThirdPartyService chatThirdPartyService = BeanUtil.getBean(ChatThirdPartyService.class);
        UserDao userDao = BeanUtil.getBean(UserDao.class);
        ActivityDao activityDao = BeanUtil.getBean(ActivityDao.class);

        List<Appointment> appointmentList = queryExpiredAppointments(appointmentDao);

        Map<String, User> userMap = new HashMap<>(appointmentList.size(), 1);
        Map<String, Activity> activityMap = new HashMap<>(appointmentList.size(), 1);

        buildUserAndActivityInfo(userDao, activityDao, appointmentList, userMap, activityMap);

        int count = 0;
        LOG.info("begin send appointment expired message");
        for (Appointment appointment : appointmentList) {
            LOG.info("Appointment:{} is neary expired", appointment.getAppointmentId());
            Activity activity = activityMap.get(appointment.getActivityId());
            if (activity == null) {
                continue;
            }

            User invitedUser = userMap.get(appointment.getInvitedUserId());
            if (invitedUser == null) {
                continue;
            }
            User applier = userMap.get(appointment.getApplyUserId());
            if (applier == null) {
                continue;
            }
            sendMessage(chatCommonService, chatThirdPartyService, activity, invitedUser, applier);

            count++;
            sleep(count);
        }
        LOG.info("Finished remind appointment users");
    }

    //环信发送消息约束
    private void sleep(int count) {
        if (count % 20 == 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.warn(e.getMessage());
            }
        }
    }

    private void sendMessage(ChatCommonService chatCommonService, ChatThirdPartyService chatThirdPartyService,
                             Activity activity, User invitedUser, User applier) {
        List<String> emchatUsers = new ArrayList<>(2);
        emchatUsers.add(invitedUser.getEmchatName());
        emchatUsers.add(applier.getEmchatName());

        Map<String, Object> ext = new HashMap<>(2, 1);
        ext.put("type", Constants.MessageType.APPOINTMENT_EXPIRED_MSG);

        try {
            chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL, emchatUsers,
                    MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.expired", "{0}邀约明天就要过期"), activity.getType()),
                    ext);
        } catch (ApiException e) {
            LOG.warn("Send Message failure, applier:{}, inviter:{}", applier.getUserId(), invitedUser.getUserId());
        }
    }

    private void buildUserAndActivityInfo(UserDao userDao, ActivityDao activityDao, List<Appointment> appointmentList, Map<String, User> userMap, Map<String, Activity> activityMap) {
        for (Appointment appointment : appointmentList) {
            userMap.put(appointment.getApplyUserId(), null);
            userMap.put(appointment.getInvitedUserId(), null);
            activityMap.put(appointment.getActivityId(), null);
        }

        List<User> userList = userDao.findByIds(userMap.keySet());
        for (User user : userList) {
            userMap.put(user.getUserId(), user);
        }

        List<Activity> activityList = activityDao.findByIds(activityMap.keySet());
        for (Activity activity : activityList) {
            activityMap.put(activity.getActivityId(), activity);
        }
    }

    private List<Appointment> queryExpiredAppointments(AppointmentDao appointmentDao) {
        Long expiredLimitTime = DateUtil.getExpiredLimitTime();
        Long remindTime = expiredLimitTime + (1000 * 60 * 60 + 24);
        Query query = Query.query(Criteria.where("deleteFlag").is(false)
                .and("activityCategory").is(Constants.ActivityCatalog.COMMON)
                .and("createTime").gte(expiredLimitTime).lt(remindTime));

        return appointmentDao.find(query);
    }
}
