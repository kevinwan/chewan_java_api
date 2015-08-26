package com.gongpingjia.carplay.controller.business;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.controller.server.Constants;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.impl.PhoneVerificationDaoImpl;
import com.gongpingjia.carplay.po.PhoneVerification;

public class BusinessTest extends BaseTest {

	@Autowired
	private PhoneVerificationDao phoneVerifyDao;

	@Test
	public void registerTest() throws Exception {
		// 2.1 获取验证码
		MvcResult result_1 = mockMvc.perform(get("/phone/" + Constants.PHONE_NUMBER + "/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.2 验证码校验
		PhoneVerification phoneVerify = phoneVerifyDao.selectByPrimaryKey(Constants.PHONE_NUMBER);

		MvcResult result_2 = mockMvc
				.perform(MockMvcRequestBuilders.post("/phone/" + Constants.PHONE_NUMBER + "/verification").param("code",
						phoneVerify.getCode()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.5注册
		MvcResult result_5 = mockMvc
				.perform(MockMvcRequestBuilders.post("/user/register")
						.param("phone", Constants.PHONE_NUMBER)
						.param("code", phoneVerify.getCode())
						.param("password", "e10adc3949ba59abbe56e057f20f883e")
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

	}
}
