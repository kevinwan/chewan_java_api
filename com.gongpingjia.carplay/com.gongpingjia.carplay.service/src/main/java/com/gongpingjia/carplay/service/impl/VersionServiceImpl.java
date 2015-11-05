package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.dao.common.VersionDao;
import com.gongpingjia.carplay.entity.common.ProductVersion;
import com.gongpingjia.carplay.service.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2015/9/29.
 */
@Service("versionService")
public class VersionServiceImpl implements VersionService {

    private Logger LOG = LoggerFactory.getLogger(VersionServiceImpl.class);

    @Autowired
    private VersionDao versionDao;

    @Override
    public ResponseDo getVersion(String product) throws ApiException {
        LOG.debug("getVersion start, product:{}", product);

        ProductVersion version = versionDao.findOne(Query.query(Criteria.where("product").is(product)));

        return ResponseDo.buildSuccessResponse(version);
    }
}
