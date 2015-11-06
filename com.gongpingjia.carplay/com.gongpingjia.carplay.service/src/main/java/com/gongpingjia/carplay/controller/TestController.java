package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.ProductVersion;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import net.sf.json.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

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

        Address address = new Address();
        address.setCity("NJ");
        address.setProvince("JS");
        address.setDistrict("qi xia");

        return ResponseDo.buildSuccessResponse(address);
    }


    @RequestMapping(value = "/test/string", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo testPost(@RequestBody JSONObject jsonObject) {

        return ResponseDo.buildSuccessResponse("123");
    }

    public static void main(String[] args){
        ProductVersion version = new ProductVersion();
        version.setForceUpdate(1);
        version.setProduct("android");
        version.setVersion("2.0");
        version.setUrl("");

        version.setRemarks("车玩版本介绍\n" +
                "推荐活动：官方精选的靠谱的活动，活跃度高，安全有保障\n" +
                "附近活动：附近的Ta发布活动了，唱歌、吃饭、购物，还包接送\n" +
                "匹配活动：想去嗨皮？不用发完活动干等着，分分钟找到附近和你一起想去嗨皮的小伙伴\n" +
                "车主认证：百分百车主官方认证，交友出行100%有保障\n");
        JSONObject jsonObject = JSONObject.fromObject(version);
        System.out.println(jsonObject);
    }
}
