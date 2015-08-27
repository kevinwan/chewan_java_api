package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class UserInfoTest extends BaseTest {
	
	@Test
	public void testUserInfo() throws Exception {
		
		MvcResult result = mockMvc.perform(get("/user/12abfe47-e0fd-4af6-a041-0cb67cbbabdd/info?userId=12abfe47-e0fd-4af6-a041-0cb67cbbabdd&token=9c7c6543-5e56-4ddf-be2e-cfd8da44bca2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
