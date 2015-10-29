package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.dao.activity.PushInfoDao;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Administrator on 2015/9/25.
 */
@RestController
public class TestController {

    private PhoneVerificationDao phoneVerificationDao;

    @RequestMapping(value = "/test/{phone}", method = RequestMethod.GET)
    public ResponseDo getPhoneVerification(@PathVariable("phone") String phone) {
        PhoneVerification phoneVerification = phoneVerificationDao.findOne(Query.query(Criteria.where("phone").is(phone)));
        if (phoneVerification == null) {
            return ResponseDo.buildFailureResponse("没有手机号对应的验证码信息");
        }
        return ResponseDo.buildSuccessResponse(phoneVerification.getCode());
    }

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
