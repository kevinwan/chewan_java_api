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
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.EmchatToken;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.official.service.OfficialActivityService;
import com.gongpingjia.carplay.service.impl.ChatCommonService;
import com.gongpingjia.carplay.service.util.FetchUtil;
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
import java.util.*;

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
    private ChatCommonService chatCommonService;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("thirdPhotoManager")
    private PhotoService photoService;


    @Override
    public ResponseDo registerActivity(JSONObject json, String userId) throws ApiException {
        LOG.debug("Begin register activity and save data");

        //检查参数是否非法        初始化一些信息；
        OfficialActivity activity = checkAndInitSaveEntity(userId, json);

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

        JSONObject jsonResult = chatThirdPartyService.createChatGroup(chatCommonService.getChatToken(), activity.getTitle(),
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
            //查询 活动开始时间大于 startTimeStr 和 小于 endTimeStr 的 活动；
            String fromTimeStr = json.getString("fromTime");
            String toTimeStr = json.getString("toTime");
            String statusStr = json.getString("status");
            Criteria criteria = new Criteria();
            criteria.and("deleteFlag").is(false);
            if (StringUtils.isNotEmpty(statusStr)) {
                int status = Integer.parseInt(statusStr);
                if (status == 0) {
                    criteria.and("onFlag").is(false);

                } else if (status == 1) {
                    //上架中
                    criteria.and("onFlag").is(true);
                    criteria.and("end").exists(false).orOperator(Criteria.where("end").gte(new Date()));
                } else if (status == 2) {
                    //已经下架
                    criteria.and("onFlag").is(true);
                    criteria.and("end").lte(new Date().getTime());
                }
            }
            // start 大于 fromTime 小于 toTime
            if (StringUtils.isNotEmpty(fromTimeStr) && StringUtils.isNotEmpty(toTimeStr)) {
                Long fromTime = Long.parseLong(fromTimeStr);
                Long toTime = Long.parseLong(toTimeStr);
                toTime = DateUtil.addTime(new Date(toTime), Calendar.HOUR, 24);
                criteria.and("start").gte(fromTime).lt(toTime);
            } else if (StringUtils.isNotEmpty(fromTimeStr) && StringUtils.isEmpty(toTimeStr)) {
                Long fromTime = Long.parseLong(fromTimeStr);
                criteria.and("start").gte(fromTime);
            } else if (StringUtils.isEmpty(fromTimeStr) && StringUtils.isNotEmpty(toTimeStr)) {
                Long toTime = Long.parseLong(toTimeStr);
                toTime = DateUtil.addTime(new Date(toTime), Calendar.HOUR, 24);
                criteria.and("start").lte(toTime);
            }

            if (StringUtils.isNotEmpty(detailAddress)) {
                String detailAddressReg = ".*" + detailAddress + ".*";
                criteria.and("destination.detail").regex(detailAddressReg);
            }

            if (StringUtils.isNotEmpty(title)) {
                String titleReg = ".*" + title + ".*";
                criteria.and("title").regex(titleReg);
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
    public ResponseDo updateActivity(String officialActivityId, JSONObject json, String userId) throws ApiException {
        json.remove("members");
        json.remove("photos");
        json.remove("organizer");
        json.remove("covers");

        checkParam(json);

//        OfficialActivity source = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);
//
        OfficialActivity officialActivity = activityDao.findById(officialActivityId);
        if (null == officialActivity) {
            throw new ApiException("改数据不存在");
        }
//        activityDao.update(officialActivityId, source);
        String params[] = {"title", "instruction", "destination", "cover", "limitType", "price", "start", "onFlag"};
        Update update = FetchUtil.initUpdateFromJson(json, params);
        int limitType = json.getInt("limitType");
        if (limitType == Constants.OfficialActivityLimitType.TOTAL_LIMIT) {
            //限制总人数
            update.set("totalLimit", json.getInt("totalLimit"));
            update.set("maleLimit", -1);
            update.set("femaleLimit", -1);
        } else if (limitType == Constants.OfficialActivityLimitType.GENDER_LIMIT) {
            //分别限制男女人数
            update.set("femaleLimit", json.getInt("femaleLimit"));
            update.set("maleLimit", json.getInt("maleLimit"));
            update.set("totalLimit", -1);
        } else {
            //不限制人数
            update.set("totalLimit", -1);
            update.set("maleLimit", -1);
            update.set("femaleLimit", -1);
        }

        activityDao.update(officialActivityId, update);

        return ResponseDo.buildSuccessResponse();
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

    @Override
    public ResponseDo deleteActivities(List<String> officialActivityIds) throws ApiException {
        if (null == officialActivityIds || officialActivityIds.size() == 0) {
            throw new ApiException("没有选中删除数据");
        }
        List<OfficialActivity> officialActivityList = activityDao.findByIds(officialActivityIds);
        if (officialActivityIds.size() != officialActivityList.size()) {
            throw new ApiException("存在数据库中不存在的数据");
        }
        Update update = Update.update("deleteFlag", true);
        activityDao.updateAll(Query.query(Criteria.where("officialActivityId").in(officialActivityIds)), update);
        return ResponseDo.buildSuccessResponse();
    }


    private OfficialActivity checkAndInitSaveEntity(String userId, JSONObject json) throws ApiException {
        //必填项目
        if (CommonUtil.isEmpty(json, Arrays.asList("title", "instruction", "destination", "cover", "limitType", "price", "start", "onFlag"))) {
            throw new ApiException("参数输入不全 请检查参数");
        }


        OfficialActivity activity = null;
        try {
            activity = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);
        } catch (Exception e) {
            LOG.debug(e.getMessage(), e);

            throw new ApiException("输入参数有误");
        }

        if (null == activity.getDestination()) {
            throw new ApiException("地址不能为空 ");
        }
        if (StringUtils.isEmpty(activity.getDestination().getCity()) || StringUtils.isEmpty(activity.getDestination().getDetail())) {
            throw new ApiException("地址 城市 跟具体地址不能为空");
        }

        if (activity.getLimitType() == Constants.OfficialActivityLimitType.TOTAL_LIMIT) {
            //限制总人数
            if (null == activity.getTotalLimit() || activity.getTotalLimit() <= 0) {
                throw new ApiException("总限制人数必须大于0");
            }
            activity.setMaleLimit(-1);
            activity.setFemaleLimit(-1);
        } else if (activity.getLimitType() == Constants.OfficialActivityLimitType.GENDER_LIMIT) {
            //分别限制男女人数
            if (null == activity.getMaleLimit() || activity.getMaleLimit() < 0) {
                throw new ApiException("男性数量必须大于等于0");
            }
            if (null == activity.getFemaleLimit() || activity.getFemaleLimit() < 0) {
                throw new ApiException("女性数量必须大于等于0");
            }
            //不能同时为0
            if (null != activity.getMaleLimit() && activity.getMaleLimit() == 0 && null != activity.getFemaleLimit() && activity.getFemaleLimit() == 0) {
                throw new ApiException("男女人数不能同时为0");
            }
            activity.setTotalLimit(activity.getMaleLimit() + activity.getFemaleLimit());
        } else {
            //不限制人数
            activity.setTotalLimit(-1);
            activity.setMaleLimit(-1);
            activity.setFemaleLimit(-1);
        }
        activity.setOfficialActivityId(null);
        activity.setUserId(userId);
        activity.setDeleteFlag(false);
        activity.setNowJoinNum(0);
        activity.setMaleNum(0);
        activity.setFemaleNum(0);
        return activity;
    }

    private void checkParam(JSONObject json) throws ApiException {
        //必填项目
        if (CommonUtil.isEmpty(json, Arrays.asList("title", "instruction", "destination", "cover", "limitType", "price", "start", "onFlag"))) {
            throw new ApiException("参数输入不全 请检查参数");
        }

        OfficialActivity activity = (OfficialActivity) JSONObject.toBean(json, OfficialActivity.class);

        if (null == activity.getDestination()) {
            throw new ApiException("地址不能为空 ");
        }
        if (StringUtils.isEmpty(activity.getDestination().getCity()) || StringUtils.isEmpty(activity.getDestination().getDetail())) {
            throw new ApiException("地址 城市 跟具体地址不能为空");
        }

        if (activity.getLimitType() == Constants.OfficialActivityLimitType.TOTAL_LIMIT) {
            //限制总人数
            if (null == activity.getTotalLimit() || activity.getTotalLimit() <= 0) {
                throw new ApiException("总限制人数必须大于0");
            }
        } else if (activity.getLimitType() == Constants.OfficialActivityLimitType.GENDER_LIMIT) {
            //分别限制男女人数
            if (null == activity.getMaleLimit() || activity.getMaleLimit() < 0) {
                throw new ApiException("男性数量必须大于等于0");
            }
            if (null == activity.getFemaleLimit() || activity.getFemaleLimit() < 0) {
                throw new ApiException("女性数量必须大于等于0");
            }
            //不能同时为0
            if (null != activity.getMaleLimit() && activity.getMaleLimit() == 0 && null != activity.getFemaleLimit() && activity.getFemaleLimit() == 0) {
                throw new ApiException("男女人数不能同时为0");
            }
        }
    }

}
