package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class JoinActivityTest extends BaseTest {

	//第一次成功，第二次报重复申请
	@Test
	public void testJoinActivity() throws Exception {
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/activity/7ff0e9b5-0937-4815-97d3-6da0c588face/join")
				.param("userId", "846de312-306c-4916-91c1-a5e69b158014")
				.param("token", "2537271f-3063-45cf-bcff-27cdd35d97e0")
				.param("seat", "0"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//已经是成员
	@Test
	public void testJoinActivity2() throws Exception {
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/join")
				.param("userId", "ad5b9c52-2e48-40ed-89b6-26154355262f")
				.param("token", "764102c5-bc5c-4bf0-89ae-a8371bca1151")
				.param("seat", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//座位数超过申请的车次上线
	@Test
	public void testJoinActivity3() throws Exception {
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/join")
				.param("userId", "5c2595d0-f15f-4258-ac3e-87af05698232")
				.param("token", "5a0b20bf-71f4-43e8-9abc-34da333b7b1d")
				.param("seat", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
