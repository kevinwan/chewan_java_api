package com.gongpingjia.carplay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2015/9/25.
 */
@RestController
public class TestController {

    @RequestMapping(value = "/test/string", method = RequestMethod.GET)
    public ResponseDo testStr() {
        User user = new User();
        Address address = new Address();
        address.setCity("NJ");
        address.setProvince("JS");
        address.setDistrict("qi xia");
        user.setAddress(address);
        user.setNickname("nick user");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(user);
        jsonObject.put("test", "test");
        return ResponseDo.buildSuccessResponse(jsonObject);
    }
}
