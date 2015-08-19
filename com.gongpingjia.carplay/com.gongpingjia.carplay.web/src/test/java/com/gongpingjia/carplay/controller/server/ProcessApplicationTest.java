package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ProcessApplicationTest extends BaseTest {

	//已经同意过了的
	@Test
	public void testProcessApplication() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/application/05b30819-09a9-40a6-876d-d106147a6261/process")
								.param("userId", "123fec64-dd98-4a84-adee-7d1e0a950704")
								.param("token", "cb1c20ad-e4b9-4980-907b-08f287c6ff68").param("action", "1"))
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
						MockMvcRequestBuilders.post("/application/d9195028-7610-4c0a-899b-3d0e8936cd64/process")
								.param("userId", "2db67f14-d12b-44ed-97d3-e267c135326c")
								.param("token", "1ec10f8d-ab0f-4fff-9f81-d5c14d7dd435").param("action", "1"))
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