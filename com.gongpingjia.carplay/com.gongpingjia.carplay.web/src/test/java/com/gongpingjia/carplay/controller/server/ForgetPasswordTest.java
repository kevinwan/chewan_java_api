package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class ForgetPasswordTest extends BaseTest {

	@Test
	public void testForgetPassword() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/password")
								.param("phone", "18951650021")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("code", "2302"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testForgetPassword2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/password")
								.param("phone", "15365087965")
								.param("password", "changeme123")
								.param("code", "1193"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
