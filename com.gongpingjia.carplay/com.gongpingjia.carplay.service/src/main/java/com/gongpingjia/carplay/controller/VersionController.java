package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
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


    /**
     * 获取版本信息
     *
     * @param product 产品名称，Android/IOS
     * @return 返回版本信息
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public ResponseDo getVersion(@RequestParam("product") String product) {
        return ResponseDo.buildSuccessResponse();
    }
}
