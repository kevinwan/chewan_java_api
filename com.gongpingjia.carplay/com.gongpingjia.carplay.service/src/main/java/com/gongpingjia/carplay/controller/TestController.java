package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.BeanUtil;
import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.ProductVersion;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.UserService;
import com.gongpingjia.carplay.service.impl.ChatCommonService;
import com.gongpingjia.carplay.service.impl.UserServiceImpl;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
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

    public static void main(String[] args) {
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


//    @RequestMapping(value = "/test/emchatgroup", method = RequestMethod.GET)
//    public ResponseDo createActivityEmchatGroup() throws ApiException {
//        OfficialActivityDao officialActivityDao = BeanUtil.getBean(OfficialActivityDao.class);
//        UserDao userDao = BeanUtil.getBean(UserDao.class);
//        ChatCommonService chatCommonService = BeanUtil.getBean(ChatCommonService.class);
//        ChatThirdPartyService chatThirdPartyService = BeanUtil.getBean(ChatThirdPartyService.class);
//
//        List<OfficialActivity> activitys = officialActivityDao.find(Query.query(Criteria.where("userId").is("56260e760cf21390f134cf2b")));
//        for (OfficialActivity activity : activitys) {
//            User user = userDao.findById(activity.getUserId());
//
//            try {
//                JSONObject jsonResult = chatThirdPartyService.createChatGroup(chatCommonService.getChatToken(), activity.getTitle(),
//                        "", user.getEmchatName(), null);
//                LOG.info(jsonResult.toString());
//                if (jsonResult.isEmpty()) {
//                    LOG.warn("Failed to create chat group");
//                    throw new ApiException("创建聊天群组失败");
//                }
//
//                officialActivityDao.update(Query.query(Criteria.where("officialActivityId").is(activity.getOfficialActivityId())),
//                        Update.update("emchatGroupId", jsonResult.getJSONObject("data").getString("groupid")));
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return ResponseDo.buildSuccessResponse();
//    }

//    @RequestMapping(value = "/test/batchpassword", method = RequestMethod.GET)
//    public ResponseDo batchChangeUserPassword() {
//        long start = 10012340001l;
//        long end = 10012340020l;
//
//        UserService userService = BeanUtil.getBean(UserService.class);
//        UserDao userDao = BeanUtil.getBean(UserDao.class);
//
//        String newPassword = "bfd59291e825b5f2bbf1eb76569f8fe7";
//
//        for (long phone = start; phone <= end; phone++) {
//            User user = userDao.findOne(Query.query(Criteria.where("phone").is(String.valueOf(phone))));
//            try {
//                ResponseDo responseDo = userService.changePassword(user.getUserId(), user.getPassword(), newPassword);
//                LOG.debug(responseDo.toString());
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//        }
//        return ResponseDo.buildSuccessResponse();
//    }
}
