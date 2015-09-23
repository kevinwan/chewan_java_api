package com.gongpingjia.carplay.controller;

import java.util.*;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.TypeConverUtil;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.service.UserService;

@RestController
public class UserInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserService userService;

    /**
     * 2.5注册
     *
     * @param json 参数列表
     * @return 注册结果
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo register(@RequestBody JSONObject json) {

        LOG.debug("register is called, request parameter produce:");
        try {
            // 检查必须参数是否为空
            if (CommonUtil.isEmpty(json, Arrays.asList("nickname", "gender", "birthDay"))) {
                throw new ApiException("输入参数错误");
            }
            User user = new User();
            Address address = new Address();
            user.setPhone(json.getString("phone"));
            user.setUserId(json.getString("photo"));
            user.setNickname(json.getString("nickname"));
            user.setGender(json.getString("gender"));
            user.setBirthday(new Date(json.getJSONObject("birthDay").getLong("time")));

            address.setProvince(json.getString("province"));
            address.setCity(json.getString("city"));
            address.setDistrict(json.getString("district"));
            user.setAddress(address);
            user.setPhoto(json.getString("photo"));

            userService.checkRegisterParameters(user, json);

            return userService.register(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}