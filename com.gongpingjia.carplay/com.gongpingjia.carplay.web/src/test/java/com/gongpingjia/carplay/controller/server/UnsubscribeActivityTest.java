package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class UnsubscribeActivityTest extends BaseTest {
	@Test
	public void testUnsubscribeActivity() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/288978de-5d8f-42a6-a9c6-2372f001a80c/unsubscribe")
							.param("userId", "7da6fac1-c360-449b-8b19-5231d8fa9b0a")
							.param("token", "0a5c4a06-3296-48e7-87d2-ffe4338252ec"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//活动创建者
	@Test
	public void testUnsubscribeActivity2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/288978de-5d8f-42a6-a9c6-2372f001a80c/unsubscribe")
							.param("userId", "2db67f14-d12b-44ed-97d3-e267c135326c")
							.param("token", "1ec10f8d-ab0f-4fff-9f81-d5c14d7dd435"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
