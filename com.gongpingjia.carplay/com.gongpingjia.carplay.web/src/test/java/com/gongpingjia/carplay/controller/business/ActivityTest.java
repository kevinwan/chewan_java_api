package com.gongpingjia.carplay.controller.business;

import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.user.PhoneVerificationDao;
import com.gongpingjia.carplay.entity.activity.Activity;
import com.gongpingjia.carplay.entity.common.Address;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.PhoneVerification;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 */
public class ActivityTest extends BaseTest {

    @Autowired
    private PhoneVerificationDao phoneVerificationDao;

    @Autowired
    private ActivityDao activityDao;

    private Activity initData() {
        //members 当前不能有 activityId不需要指定
        Activity activity = new Activity();
        activity.setBusinessId("business id");
        Address destination = new Address();
        destination.setProvince("江苏");
        destination.setCity("NJ");
        destination.setDistrict("仙林");
        destination.setStreet("文苑路");
        activity.setDestination(destination);
        Landmark landmark = new Landmark();
        landmark.setLatitude(110.2);
        landmark.setLatitude(124.2);
        activity.setDestPoint(landmark);
        activity.setType("point");
        activity.setStart(new Date().getTime());
        activity.setEnd(new Date().getTime());
        activity.setEstablish(destination);
        activity.setTransfer(false);
        activity.setPay("pay");
        return activity;
    }

    @Test
    public void testActivityRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(JSONObject.fromObject(initData()).toString()).param("userId", "123").param("token", "token"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }


    @Test
    public void testDateTime() {
        PhoneVerification phoneVerification = new PhoneVerification();
        phoneVerification.setPhone("999999999");
        phoneVerification.setModifyTime(new Date().getTime());
        phoneVerification.setCode("code");
        phoneVerification.setExpire(DateUtil.addTime(new Date(), Calendar.MINUTE, 7200));
        phoneVerificationDao.save(phoneVerification);
        PhoneVerification phNew = phoneVerificationDao.findById(phoneVerification.getId());
        System.out.println(phNew.getModifyTime());
    }

    @Test
    public void  testSaveActivity(){
        Activity activity = initData();
        activity.setUserId("1234");
        activity.setTransfer(false);
        List<String> members = new ArrayList<String>();
        members.add("pass");
        activity.setMembers(members);
        activityDao.save(activity);
    }

}
