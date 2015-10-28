package com.gongpingjia.carplay.statistic.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by licheng on 2015/10/28.
 * 活动相关的埋点记录
 */
@RestController
public class RecordController {

    private static final Logger LOG = LoggerFactory.getLogger(RecordController.class);

    /**
     * 埋点记录数据上传接口
     *
     * @return
     */
    @RequestMapping(value = "/record/upload", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo recordUpload(@RequestParam HttpServletRequest request) {
        LOG.info("Upload record begin");


        return ResponseDo.buildSuccessResponse();
    }
}
