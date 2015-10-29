package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
        user.setBirthday(new Date().getTime());
        List<User> userList = new ArrayList<>();
        userList.add(user);
        return ResponseDo.buildSuccessResponse(userList);
    }


    @RequestMapping(value = "/test/string", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo testPost(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);
        String data = jsonObject.getString("data");
        return ResponseDo.buildSuccessResponse("");
    }
}
