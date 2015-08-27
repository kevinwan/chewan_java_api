package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class ProcessApplicationTest extends BaseTest {

	//已经同意过了的
	@Test
	public void testProcessApplication() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/application/6561ad65-5d98-4bc0-8faf-b88844579f3c/process")
								.param("userId", "412bac09-b9a0-46b5-a283-7442fa1eb76c")
								.param("token", "206c0f41-4f65-4bf4-ba38-5a11ca03ad36").param("action", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//第一次批准同意，后续会报找不到相应的申请信息
	@Test
	public void testProcessApplication2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/application/0d432863-2e6f-4f2d-b19f-c2a736aab3b5/process")
								.param("userId", "2db67f14-d12b-44ed-97d3-e267c135326c")
								.param("token", "1ec10f8d-ab0f-4fff-9f81-d5c14d7dd435").param("action", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testProcessApplication3() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/application/1999982e-d3dc-40b7-ac52-c584628f445a/process")
								.param("userId", "90179e74-e5ce-4925-89cb-d9de2d0ccfc7")
								.param("token", "fd9d8810-b7c2-4e9d-821c-fde4af667de3").param("action", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testProcessApplication4() throws Exception{
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/application/cc41e0dd-5597-450c-af4f-0f058b1b1729/process")
				.param("userId", "2db67f14-d12b-44ed-97d3-e267c135326c")
				.param("token", "1ec10f8d-ab0f-4fff-9f81-d5c14d7dd435").param("action", "0"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
