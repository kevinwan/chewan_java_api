package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.po.PhoneVerification;

/**
 * 2.2 验证码校验
 * 
 * @author licheng
 *
 */
public class CheckPhoneVerificationTest extends BaseTest {

	@Autowired
	private PhoneVerificationDao phoneVerifyDao;


	@Test
	public void testCheckPhoneVerification() throws Exception {

		PhoneVerification phoneVerify = phoneVerifyDao.selectByPrimaryKey(Constants.PHONE_NUMBER);
		// 表示用户存在，且验证码存在
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/phone/" + Constants.PHONE_NUMBER + "/verification").param("code",
								phoneVerify.getCode())).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testCheckPhoneVerification2() throws Exception {

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/phone/1234/verification").param("code", "1234"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testCheckPhoneVerification3() throws Exception {

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/phone/" + Constants.PHONE_NUMBER + "/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
