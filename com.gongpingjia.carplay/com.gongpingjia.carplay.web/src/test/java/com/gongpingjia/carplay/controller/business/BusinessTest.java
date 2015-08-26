package com.gongpingjia.carplay.controller.business;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.controller.server.Constants;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.po.PhoneVerification;

import net.sf.json.JSONObject;

public class BusinessTest extends BaseTest {

	@Autowired
	private PhoneVerificationDao phoneVerifyDao;
	

	@Test
	public void registerTest() throws Exception {
		String phone=Constants.PHONE_NUMBER;
		String password="e10adc3949ba59abbe56e057f20f883e";
		
		
		// 2.1 获取验证码
		mockMvc.perform(get("/phone/" + phone + "/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.2 验证码校验
		PhoneVerification phoneVerify = phoneVerifyDao.selectByPrimaryKey(phone);

		mockMvc
				.perform(MockMvcRequestBuilders.post("/phone/" + phone + "/verification").param("code",
						phoneVerify.getCode()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.5注册
		mockMvc
				.perform(MockMvcRequestBuilders.post("/user/register")
						.param("phone", phone)
						.param("code", phoneVerify.getCode())
						.param("password", password)
						.param("nickname", "孔明")
						.param("gender", "男")
						.param("birthMonth", "11")
						.param("birthYear", "1985")
						.param("birthDay", "5")
						.param("province", "江苏省")
						.param("city", "南京市")
						.param("district", "栖霞区")
						.param("photo", "412bac09-b9a0-46b5-a283-7442fa1eb76c"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		//2.8登录
		mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/login")
								.param("phone", phone)
								.param("password", password))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		//2.9忘记密码
		MvcResult result_9 = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/password")
								.param("phone", phone)
								.param("password", password)
								.param("code", phoneVerify.getCode()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		
		//获取userId和token
		JSONObject userInfo=JSONObject.fromObject(result_9.getResponse().getContentAsString());
		String userId=userInfo.getJSONObject("data").getString("userId");
		String token=userInfo.getJSONObject("data").getString("token");
		System.out.println(userId+"     "+token);
		
		
		// 2.11 车主认证申请
		mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/"+userId+"/authentication?token="+token)
								.param("drivingExperience", "3")
								.param("carBrand", "大众")
								.param("carBrandLogo", "http://gongpingjia.qiniudn.com/img/logo/7206452af3747880ddd07398a95b9bdbebbc963e.jpg")
								.param("carModel", "大众cc")
								.param("slug", "dazhong-cc"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
	
			
		}
	}

