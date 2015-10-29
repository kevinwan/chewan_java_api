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
//        User user = new User();
//        Address address = new Address();
//        address.setCity("NJ");
//        address.setProvince("JS");
//        address.setDistrict("qi xia");
//        user.setAddress(address);
//        user.setNickname("nick user");
//        List<User> userList = new ArrayList<>();
//        userList.add(user);
//        return ResponseDo.buildSuccessResponse(userList);
        System.out.println(jsonObject);
        PushInfoDao dao = BeanUtil.getBean(PushInfoDao.class);
        Set<String> result = dao.groupByReceivedUsers(Arrays.asList("123", "345", "456"), 5);
        return ResponseDo.buildSuccessResponse(result);
    }

    public static void main(String[] args) {
        /*
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
        */

        OfficialActivity oa = new OfficialActivity();
        oa.setUserId("561ba2d60cf2429fb48e86bd");
        oa.setCreateTime(new Date().getTime());
        oa.setEnd(new Date().getTime() + 10000000L);
        oa.setDescription("测试官方活动Description");

        Photo photo = new Photo();
        photo.setId("315aa636-bc9c-4548-9f83-69d720738d90");
        photo.setKey("asset/activity/cover/315aa636-bc9c-4548-9f83-69d720738d90/cover.jpg");
        List<Photo> photos = new ArrayList<>(1);
        photos.add(photo);
//        oa.setCovers(photos);

        Address destination = new Address();
        destination.setProvince("江苏省");
        destination.setCity("南京市");
        destination.setDistrict("玄武区");
        destination.setStreet("玄武大道");
        oa.setDestination(destination);

//        oa.setEstablish(destination);
//        oa.setEstabPoint(landmark);

        oa.setFemaleLimit(20);
        oa.setMaleLimit(20);
        oa.setInstruction("测试官方活动Instruction");
//        oa.setPrice(200L);
        oa.setTitle("测试官方活动Title");
        oa.setPriceDesc("测试PriceDescription");

        oa.setStart(new Date().getTime());

        JSONObject jsonObject = JSONObject.fromObject(oa);
        System.out.println(jsonObject);
    }
}
