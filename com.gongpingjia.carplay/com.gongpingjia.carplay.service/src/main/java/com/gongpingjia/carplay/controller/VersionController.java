package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.service.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by licheng on 2015/9/29.
 * 版本控制
 */
@RestController
public class VersionController {

    private static Logger LOG = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private VersionService versionService;


    /**
     * 获取版本信息
     *
     * @param product 产品名称，Android/IOS
     * @return 返回版本信息
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public ResponseDo getVersion(@RequestParam(value = "product", defaultValue = "android") String product) {
        LOG.debug("getVersion start, request param product:{}", product);

        try {
            return versionService.getVersion(product);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}
