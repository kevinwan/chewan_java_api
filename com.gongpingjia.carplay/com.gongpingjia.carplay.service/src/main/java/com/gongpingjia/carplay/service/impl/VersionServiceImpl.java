package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.common.VersionDao;
import com.gongpingjia.carplay.entity.common.Version;
import com.gongpingjia.carplay.service.VersionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2015/9/29.
 */
@Service("versionService")
public class VersionServiceImpl implements VersionService {

    private Logger logger = Logger.getLogger(VersionServiceImpl.class);

    @Autowired
    private VersionDao versionDao;

    @Override
    public ResponseDo getVersion(String product) throws ApiException {
        logger.debug("getVersion start");

        if (StringUtils.isEmpty(product)) {
            //默认是 android
            product = Constants.Product.DEFAULT_NAME;
        }
        Version version = versionDao.findOne(Query.query(Criteria.where("product").is(product)));
        return ResponseDo.buildSuccessResponse(version);
    }
}
