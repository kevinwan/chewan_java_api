package com.gongpingjia.carplay.official.task;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.activity.AppointmentDao;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.Appointment;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
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
import java.util.*;

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

        remindCommonActivitiesUsers(appointmentDao, chatCommonService, chatThirdPartyService);

        remindOfficialActivitiesUsers(appointmentDao, chatCommonService, chatThirdPartyService);

        LOG.info("Finished remind appointment users");
    }

    private void remindOfficialActivitiesUsers(AppointmentDao appointmentDao, ChatCommonService chatCommonService, ChatThirdPartyService chatThirdPartyService) {
        LOG.debug("Remind official activities users");
        //1.获取官方活动参加列表和邀请同去列表
        Query officialQuery = Query.query(Criteria.where("deleteFlag").is(false)
                .and("status").in(Constants.AppointmentStatus.APPLYING, Constants.AppointmentStatus.ACCEPT)
                .and("activityCategory").in(Constants.ActivityCatalog.OFFICIAL, Constants.ActivityCatalog.TOGETHER));
        List<Appointment> appointmentList = appointmentDao.find(officialQuery);

        Set<String> officialActivityIds = new HashSet<>(appointmentList.size(), 1);
        Set<String> userIds = new HashSet<>(appointmentList.size() * 2, 1);
        for (Appointment item : appointmentList) {
            officialActivityIds.add(item.getActivityId());
            userIds.add(item.getApplyUserId());
            userIds.add(item.getInvitedUserId());
        }

        //2.根据邀约信息，获取官方活动列表
        OfficialActivityDao officialActivityDao = BeanUtil.getBean(OfficialActivityDao.class);
        List<OfficialActivity> officialActivityList = officialActivityDao.find(Query.query(Criteria.where("officialActivityId").in(officialActivityIds)));
        Map<String, OfficialActivity> officialActivityMap = new HashMap<>(appointmentList.size(), 1);
        for (OfficialActivity item : officialActivityList) {
            officialActivityMap.put(item.getOfficialActivityId(), item);
        }

        UserDao userDao = BeanUtil.getBean(UserDao.class);
        List<User> userList = userDao.find(Query.query(Criteria.where("userId").in(userIds)));
        Map<String, User> userMap = new HashMap<>(appointmentList.size(), 1);
        for (User item : userList) {
            userMap.put(item.getUserId(), item);
        }

        //3.根据邀约列表，发送提醒消息
        int count = 0;
        Long current = DateUtil.getTime();
        Long remindTime = DateUtil.getExpiredLimitTime() + Constants.DAY_MILLISECONDS;
        for (Appointment item : appointmentList) {
            User invitedUser = userMap.get(item.getInvitedUserId());
            if (invitedUser == null) {
                continue;
            }
            User applier = userMap.get(item.getApplyUserId());
            if (applier == null) {
                continue;
            }
            OfficialActivity officialActivity = officialActivityMap.get(item.getActivityId());
            if (officialActivity == null) {
                continue;
            }

            if (officialActivity.getEnd() == null) {  //没有结束时间
                if (officialActivity.getStart() <= remindTime) {   //到达了提醒时间
                    sendOfficialActivityRemindMessage(item, officialActivity, invitedUser, applier, chatCommonService, chatThirdPartyService);
                }
            } else {  //活动有结束时间
                if (officialActivity.getEnd() < current + Constants.DAY_MILLISECONDS) {   //活动结束时间只剩下一天的时间
                    sendOfficialActivityRemindMessage(item, officialActivity, invitedUser, applier, chatCommonService, chatThirdPartyService);
                }
            }

            count++;
            sleep(count);
        }

    }

    public void sendOfficialActivityRemindMessage(Appointment appointment, OfficialActivity officialActivity, User invitedUser, User applier,
                                                  ChatCommonService chatCommonService, ChatThirdPartyService chatThirdPartyService) {
        Map<String, Object> ext = new HashMap<>(2, 1);
        ext.put("type", Constants.MessageType.APPOINTMENT_EXPIRED_MSG);

        try {
            if (Constants.ActivityCatalog.OFFICIAL.equals(officialActivity.getActivityCategory())) {
                //官方活动处理
                //发给活动申请人
                String applyUserMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.official.expired",
                        "我参加的{0}活动明天将要失效"), officialActivity.getTitle());
                ext.put("userId", invitedUser.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(applier.getEmchatName()), applyUserMessage, ext);
            }

            if (Constants.ActivityCatalog.TOGETHER.equals(officialActivity.getActivityCategory())) {
                //官方活动邀请同去处理
                //发给活动申请人
                String invitedUserMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.expired",
                        "{0}和你一起参加的{1}活动明天将失效，如想继续联系，可互相关注"), applier.getNickname(), officialActivity.getTitle());
                ext.put("userId", invitedUser.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(applier.getEmchatName()), invitedUserMessage, ext);

                //发给活动接收人(被邀请人员)
                String applierMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.expired",
                        "{0}和你一起参加的{1}活动明天将失效，如想继续联系，可互相关注"), invitedUser.getNickname(), officialActivity.getTitle());
                ext.put("userId", applier.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(invitedUser.getEmchatName()), applierMessage, ext);
            }
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
        }
    }

    private void remindCommonActivitiesUsers(AppointmentDao appointmentDao, ChatCommonService chatCommonService, ChatThirdPartyService chatThirdPartyService) {
        LOG.debug("Begin remind common activity users");
        Long expiredLimitTime = DateUtil.getExpiredLimitTime();
        Long remindTime = expiredLimitTime + Constants.DAY_MILLISECONDS;
        Query query = Query.query(Criteria.where("deleteFlag").is(false)
                .and("activityCategory").is(Constants.ActivityCatalog.COMMON)
                .and("status").in(Constants.AppointmentStatus.APPLYING, Constants.AppointmentStatus.ACCEPT)
                .and("modifyTime").gte(expiredLimitTime).lt(remindTime));

        List<Appointment> appointmentList = appointmentDao.find(query);

        Map<String, User> userMap = new HashMap<>(appointmentList.size(), 1);
        Map<String, Activity> activityMap = new HashMap<>(appointmentList.size(), 1);

        buildUserAndActivityInfo(appointmentList, userMap, activityMap);

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
            sendCommonActivityMessage(chatCommonService, chatThirdPartyService, activity, invitedUser, applier, appointment);

            count++;
            sleep(count);
        }
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

    private void sendCommonActivityMessage(ChatCommonService chatCommonService, ChatThirdPartyService chatThirdPartyService,
                                           Activity activity, User invitedUser, User applier, Appointment appointment) {
        Map<String, Object> ext = new HashMap<>(2, 1);
        ext.put("type", Constants.MessageType.APPOINTMENT_EXPIRED_MSG);

        try {
            if (appointment.getStatus() == Constants.AppointmentStatus.ACCEPT) {
                //发给活动申请人
                String invitedUserMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.expired",
                        "{0}和你的{1}活动明天将失效，如想继续联系，可互相关注"), invitedUser.getNickname(), activity.getType());
                ext.put("userId", invitedUser.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(applier.getEmchatName()), invitedUserMessage, ext);

                //发给活动接收人(被邀请人员)
                String applierMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.expired",
                        "{0}和你的{1}活动明天将失效，如想继续联系，可互相关注"), applier.getNickname(), activity.getType());
                ext.put("userId", applier.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(invitedUser.getEmchatName()), applierMessage, ext);
            } else if (appointment.getStatus() == Constants.AppointmentStatus.APPLYING) {
                //发给活动接收人(被邀请人员),邀请中的状态
                String applierMessage = MessageFormat.format(PropertiesUtil.getProperty("dynamic.format.appointment.applying.expired",
                        "{0}邀请你的{1}活动明天将失效，请及时处理"), invitedUser.getNickname(), activity.getType());
                ext.put("userId", applier.getUserId());
                chatThirdPartyService.sendUserGroupMessage(chatCommonService.getChatToken(), Constants.EmchatAdmin.OFFICIAL,
                        Arrays.asList(invitedUser.getEmchatName()), applierMessage, ext);
            }
        } catch (ApiException e) {
            LOG.warn("Send message failure, applier:{}, inviter:{}", applier.getUserId(), invitedUser.getUserId());
        }
    }

    private void buildUserAndActivityInfo(List<Appointment> appointmentList, Map<String, User> userMap, Map<String, Activity> activityMap) {
        UserDao userDao = BeanUtil.getBean(UserDao.class);
        ActivityDao activityDao = BeanUtil.getBean(ActivityDao.class);

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
}
