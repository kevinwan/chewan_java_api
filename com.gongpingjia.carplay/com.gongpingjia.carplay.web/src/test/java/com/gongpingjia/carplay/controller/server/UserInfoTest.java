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
		
		MvcResult result = mockMvc.perform(get("/user/a3864fa5-35ad-408e-86a9-65b6c7f6472f/info?userId=ab3a32e3-c05e-4a40-98ec-6476ef89f05a&token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
