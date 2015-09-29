package com.gongpingjia.carplay.official.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.entity.user.AuthApplication;
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

    @Override
    public ResponseDo saveAuthApplication(String applicationId, String status) {
        LOG.debug("saveAuthApplication start");

        authApplicationDao.update(applicationId, Update.update("status", status));
        return ResponseDo.buildSuccessResponse();
    }

    @Override
    public ResponseDo getAuthApplicationList(String userId,String status,Long start,Long end,int ignore,int limit) {
        LOG.debug("getAuthApplicationList start");

        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(status)) {
            criteria.where("status").is(status);
        }
        if (null != start) {
            criteria.gte(start);
        }
        if (null != end) {
            criteria.lte(end);
        }

        Query query = Query.query(criteria);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "applyTime")));
        query.limit(limit);
        if (ignore != 0) {
            query.skip(ignore);
        }
        List<AuthApplication> authApplicationList = authApplicationDao.find(query);
        return ResponseDo.buildSuccessResponse(authApplicationList);
    }
}
