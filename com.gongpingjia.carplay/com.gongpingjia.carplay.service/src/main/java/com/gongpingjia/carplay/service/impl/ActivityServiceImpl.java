package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.cache.CacheService;
import com.gongpingjia.carplay.cache.util.CacheUtil;
import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.EmchatTokenService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyongyu on 2015/9/22.
 */
@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private EmchatTokenService emchatTokenService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ParameterChecker parameterChecker;



    @Autowired
    private ActivityDao activityDao;

    @Override
    public ResponseDo activityRegister(String userId, String token, Activity activity) throws ApiException {
        parameterChecker.checkUserInfo(userId, token);
        List<String> memberIds = new ArrayList<String>(1);
        memberIds.add(userId);
        activity.setMembers(memberIds);
        activityDao.save(activity);
        createEmchatGroup(activity);
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getActivityInfo(String userId, String token, String activityId) throws ApiException {
        parameterChecker.checkUserInfo(userId,token);
        Activity activity = activityDao.findById(userId);
        return ResponseDo.buildSuccessResponse(activity);
    }

    /**
     * 根据活动信息创建聊天群
     *
     * @param activity 活动信息
     * @throws ApiException 创建群聊失败时
     */
    private void createEmchatGroup(Activity activity) throws ApiException {
        LOG.debug("Begin create chat group");
        User owner = userDao.findById(activity.getUserId());
        JSONObject json = chatThirdPartyService.createChatGroup(emchatTokenService.getToken(), activity.getType(), activity.getActivityId(), owner.getNickname(), activity.getMembers());
        if (json.isEmpty()) {
            LOG.warn("Failed to create chat group");
            throw new ApiException("创建聊天群组失败");
        }
    }
}
