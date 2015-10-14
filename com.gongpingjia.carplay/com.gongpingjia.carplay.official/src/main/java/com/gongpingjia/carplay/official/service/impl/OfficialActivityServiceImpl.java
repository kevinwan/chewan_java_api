package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import com.gongpingjia.carplay.service.EmchatTokenService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2015/9/28.
 */
@Service
public class OfficialActivityServiceImpl implements OfficialActivityService {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialActivityServiceImpl.class);

    @Autowired
    private OfficialActivityDao activityDao;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private EmchatTokenService emchatTokenService;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService photoService;


    @Override
    public ResponseDo registerActivity(OfficialActivity activity, JSONObject json) throws ApiException {
        LOG.debug("Begin register activity and save data");

//        JSONArray coverArray = json.getJSONArray("covers");

        Long current = DateUtil.getTime();
        activity.setCreateTime(current);

//        List<Photo> covers = activity.getCovers();
//        //根据传入的图片资源数组，检查图片资源是否存在
//        for (int i = 0; i < coverArray.size(); i++) {
//            String coverId = coverArray.getString(i);
//            String key = MessageFormat.format(Constants.PhotoKey.COVER_KEY, coverId);
//            if (!photoService.isExist(key)) {
//                LOG.warn("Cover photo is not exist, photoKey:{}", key);
//                throw new ApiException("输入参数有误");
//            }
//
//            Photo photo = new Photo();
//            photo.setId(CodeGenerator.generatorId());
//            photo.setKey(key);
//            photo.setUploadTime(current);
//            covers.add(photo);
//        }

        activityDao.save(activity);

        User user = userDao.findById(activity.getUserId());

        JSONObject jsonResult = chatThirdPartyService.createChatGroup(emchatTokenService.getToken(), activity.getTitle(),
                activity.getOfficialActivityId(), user.getEmchatName(), null);
        if (json.isEmpty()) {
            LOG.warn("Failed to create chat group");
            activityDao.deleteById(activity.getOfficialActivityId());
            throw new ApiException("创建聊天群组失败");
        }

        activityDao.update(Query.query(Criteria.where("officialActivityId").is(activity.getOfficialActivityId())),
                Update.update("emchatGroupId", jsonResult.getJSONObject("data").getString("groupid")));

        return ResponseDo.buildSuccessResponse();
    }


    /**
     * draw length start                         draw   end      length 总长度  start start
     * 获取官方活动列表
     *
     * @param userId
     * @param json
     * @return
     * @throws ApiException
     */
    @Override
    public ResponseDo getActivityList(String userId, JSONObject json) throws ApiException {
        try {
            String title = json.getString("title");
            String detailAddress = json.getString("detailAddress");
            String startTimeStr = json.getString("startTime");
            String onFlagStr = json.getString("onFlag");
            Criteria criteria = new Criteria();
            if (StringUtils.isNotEmpty(startTimeStr)) {
                Long startTime = Long.parseLong(startTimeStr);
                criteria.and("start").lt(startTime);
            }
            if (StringUtils.isNotEmpty(onFlagStr)) {
                Boolean onFlag = json.getBoolean("onFlag");
                criteria.and("onFlag").is(onFlag);
            }
            if (StringUtils.isNotEmpty(detailAddress)) {
                String detailAddressReg = "/" + detailAddress + "/";
                criteria.and(detailAddress).regex(detailAddressReg);
            }
            if (StringUtils.isNotEmpty(title)) {
                String titleReg = "/." + title + "/.";
                criteria.and("destination.detail").regex(titleReg);
            }
            List<OfficialActivity> officialActivities = activityDao.find(Query.query(criteria));
            return ResponseDo.buildSuccessResponse(officialActivities);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public ResponseDo changeActivityOnFlag(String officialActivityId) throws ApiException {
        try {
            Criteria criteria = Criteria.where("officialActivityId").is(officialActivityId).and("deleteFlag").is(false);
            OfficialActivity officialActivity = activityDao.findOne(Query.query(criteria));
            if (null == officialActivity) {
                throw new ApiException("改官方活动不存在");
            }
            //该活动已经上架
            if (officialActivity.getOnFlag()) {
                throw new ApiException("该活动已经上架 不能修改");
            }
            Update update = Update.update("onFlag", true);
            activityDao.update(Query.query(criteria), update);
            return ResponseDo.buildSuccessResponse();
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public ResponseDo updateActivity(String officialActivityId, JSONObject json) throws ApiException {

        OfficialActivity toUpdate = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);
        OfficialActivity officialActivity = activityDao.findById(officialActivityId);
        if (null == officialActivity) {
            throw new ApiException("改数据不存在");
        }
        activityDao.update(officialActivityId, toUpdate);
        return ResponseDo.buildSuccessResponse();
    }

    private OfficialActivity initOfficialActivity(JSONObject jsonObject) throws ApiException {
        OfficialActivity officialActivity = new OfficialActivity();
        String title = jsonObject.getString("title");
        String instruction = jsonObject.getString("instruction");
//        String description

        return officialActivity;
    }

    @Override
    public ResponseDo getActivity(String officialActivityId) throws ApiException {
        OfficialActivity officialActivity = activityDao.findById(officialActivityId);
        if (null == officialActivity) {
            throw new ApiException("can not find this data");
        }
        if (null != officialActivity.getCover() && StringUtils.isNotEmpty(officialActivity.getCover().getKey())) {
            officialActivity.getCover().setUrl(CommonUtil.getThirdPhotoServer() + officialActivity.getCover().getKey());
        }
        return ResponseDo.buildSuccessResponse(officialActivity);
    }
}
