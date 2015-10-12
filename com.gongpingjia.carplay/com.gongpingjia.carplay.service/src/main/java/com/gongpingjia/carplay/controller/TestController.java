package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public ResponseDo testPost() {
        User user = new User();
        Address address = new Address();
        address.setCity("NJ");
        address.setProvince("JS");
        address.setDistrict("qi xia");
        user.setAddress(address);
        user.setNickname("nick user");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        return ResponseDo.buildSuccessResponse(userList);
    }

    public static void main(String[] args) {
        Activity activity = new Activity();
        activity.setType("看电影");
        activity.setPay("AA");

        Landmark landmark = new Landmark();
        landmark.setLatitude(32.23);
        landmark.setLongitude(180.12);
        activity.setEstabPoint(landmark);
        activity.setDestPoint(landmark);

        Address address = new Address();
        address.setProvince("江苏省");
        address.setCity("南京市");
        address.setDistrict("玄武区");
        address.setStreet("新街口");
        activity.setEstablish(address);
        activity.setDestination(address);

        activity.setStart(new Date().getTime());
        activity.setEnd(new Date().getTime());
        activity.setTransfer(true);

        JSONObject jsonObject = JSONObject.fromObject(activity);
        System.out.println(jsonObject);
    }
}
