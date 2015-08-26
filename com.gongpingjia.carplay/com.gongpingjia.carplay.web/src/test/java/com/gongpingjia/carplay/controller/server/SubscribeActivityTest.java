package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class SubscribeActivityTest extends BaseTest {
	
	//用户id与token不匹配，返回1
	@Test
	public void testSubscribeActivity() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/subscribe")
							.param("userId", "96f1a488-6d69-48fb-bcd5-ad14090881ab")
							.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	//一个活动只能订阅一次，多次执行该UnitTest会抛异常
//	@Test
//	public void testSubscribeActivity2() throws Exception {
//		MvcResult result = mockMvc
//				.perform(
//						MockMvcRequestBuilders.post("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/subscribe")
//							.param("userId", "96f1a488-6d69-48fb-bcd5-ad14090881ab")
//							.param("token", "bac433d3-5d32-40d0-8fba-a8eb8433cdf1"))
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
//				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
//				.andDo(MockMvcResultHandlers.print()).andReturn();
//		Assert.assertNull(result.getModelAndView());
//	}
}
