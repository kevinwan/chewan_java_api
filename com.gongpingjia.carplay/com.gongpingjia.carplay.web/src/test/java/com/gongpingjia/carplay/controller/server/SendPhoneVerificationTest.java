package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.1 获取注册验证码
 * 
 * @author licheng
 *
 */
public class SendPhoneVerificationTest extends BaseTest {

	@Test
	public void testSendPhoneVerification() throws Exception {
		MvcResult result = mockMvc.perform(get("/phone/" + Constants.PHONE_NUMBER + "/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testSendPhoneVerification1() throws Exception {
		MvcResult result = mockMvc.perform(get("/phone/123456/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testSendPhoneVerification2() throws Exception {
		MvcResult result = mockMvc.perform(get("/phone/" + Constants.PHONE_NUMBER + "/verification?type=1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
