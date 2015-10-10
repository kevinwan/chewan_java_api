package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.AuthApplication;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.official.service.OfficialApproveService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by licheng on 2015/9/28.
 */
@Service
public class OfficialApproveServiceImpl implements OfficialApproveService {

    private static Logger LOG = Logger.getLogger(OfficialApproveServiceImpl.class);

    @Autowired
    private AuthApplicationDao authApplicationDao;

    @Autowired
    private UserDao userDao;

    @Override
    public ResponseDo saveAuthApplication(String applicationId, String status) {
        LOG.debug("saveAuthApplication start");

        authApplicationDao.update(applicationId, Update.update("status", status));
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getAuthApplicationList(String userId, String type, String status, Long start, Long end, int ignore, int limit) {
        LOG.debug("getAuthApplicationList start");

        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(type)) {
            criteria.and("type").is(type);
        }
        if (StringUtils.isNotEmpty(status)) {
            criteria.and("status").is(status);
        }
        if (start != null) {
            criteria.and("start").gte(start);
        }
        if (end != null) {
            criteria.and("end").lte(end);
        }

        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "applyTime")));
        query.limit(limit);
        if (ignore != 0) {
            query.skip(ignore);
        }
        List<AuthApplication> authApplicationList = authApplicationDao.find(query);

        LOG.debug("Query apply user information");
        for (AuthApplication application : authApplicationList) {
            User applyUser = userDao.findById(application.getApplyUserId());
            applyUser.refreshPhotoInfo(CommonUtil.getLocalPhotoServer(), CommonUtil.getThirdPhotoServer());
            if (applyUser.getCar() != null) {
                applyUser.getCar().refreshPhotoInfo(CommonUtil.getGPJBrandLogoPrefix());
            }

            application.setApplyUser(applyUser);
        }

        return ResponseDo.buildSuccessResponse(authApplicationList);
    }
}
