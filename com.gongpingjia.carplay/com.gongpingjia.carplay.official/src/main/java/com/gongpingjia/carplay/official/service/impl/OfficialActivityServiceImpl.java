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
            Integer draw = json.getInt("draw");
            Integer length = json.getInt("length");
            Integer start = json.getInt("start");
            String city = json.getString("city");
            Criteria criteria = new Criteria();

            if (StringUtils.isNotEmpty(city)) {
                criteria.and("destination.city").is(city);
            }
            String title = json.getString("title");
            if (StringUtils.isNotEmpty(title)) {
                criteria.and("title").is(title);
            }
            List<OfficialActivity> officialActivities = activityDao.find(Query.query(criteria));

            //TODO
//            Map<String, Object> resultMap = new HashMap<>();
//            resultMap.put()
            return ResponseDo.buildSuccessResponse(officialActivities);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
