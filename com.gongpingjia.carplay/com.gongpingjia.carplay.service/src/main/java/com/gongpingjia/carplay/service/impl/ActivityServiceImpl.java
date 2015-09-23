package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.cache.CacheService;
import com.gongpingjia.carplay.cache.util.CacheUtil;
import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.EmchatTokenService;
import com.gongpingjia.carplay.util.ActivityUtil;
import com.gongpingjia.carplay.util.ActivityWeight;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

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

    @Autowired
    private ActivityUtil activityUtil;

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
        parameterChecker.checkUserInfo(userId, token);
        Activity activity = activityDao.findById(userId);
        return ResponseDo.buildSuccessResponse(activity);
    }

    @Override
    public ResponseDo getNearActivityList(Map<String, String> transParams, HttpServletRequest request) throws ApiException {
        //从request读取初始化信息；
        int limit = 10;
        int ignore = 0;
        double maxDistance = 314;
        String limitStr = request.getParameter("limit");
        String ignoreStr = request.getParameter("ignore");
        if (StringUtils.isNotEmpty(limitStr))
            limit = Integer.parseInt(limitStr);
        if (StringUtils.isNotEmpty(ignoreStr))
            ignore = Integer.parseInt(ignoreStr);
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            throw new ApiException("param not match");
        }
        String maxDistanceStr = request.getParameter("maxDistance");
        if (StringUtils.isNotEmpty(maxDistanceStr)) {
            maxDistance = Double.parseDouble(maxDistanceStr);
        }

        //
        Landmark landmark = new Landmark();
        landmark.setLatitude(Double.parseDouble(latitude));
        landmark.setLongitude(Double.parseDouble(longitude));
        Query query = initQuery(request, transParams);
        Criteria criteria = new Criteria();
        //创建在此时间之前的活动；
        long gtTime = DateUtil.addTime(new Date(), Calendar.MINUTE, (0 - (int) ActivityWeight.MAX_PUB_TIME));
        Criteria.where("createTime").gte(gtTime).where("landMark").near(new Point(landmark.getLatitude(), landmark.getLongitude())).maxDistance(maxDistance);
        query.addCriteria(criteria);
        List<Activity> allActivityList = activityDao.find(query);
        //TODO
        //此处是查询条件下的内存分页；现业务下没有更好的方式；需要全部排序 就需要将所有的数据读入到内存中进行计算；
        //优化方式可以 实用缓存方式 ，对于用户重复的请求可以缓存起来，利用version 更改机制 探讨一下；
        List<Activity> rltList = activityUtil.getPageInfo(activityUtil.sortActivityList(allActivityList, new Date(), landmark, maxDistance), ignore, limit);
        return ResponseDo.buildSuccessResponse(rltList);
    }

    @Override
    public Query initQuery(HttpServletRequest request, Map<String, String> transMap) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        for (Map.Entry<String, String> transItem : transMap.entrySet()) {
            String requestVal = request.getParameter(transItem.getKey());
            if (StringUtils.isNotEmpty(requestVal)) {
                criteria.where(transItem.getValue()).is(requestVal);
            }
        }
        query.addCriteria(criteria);
        return query;
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
