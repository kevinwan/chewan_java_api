package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class GetActivityListTest extends BaseTest {

	@Test
	public void testGetActivityList() throws Exception {
		MvcResult result = mockMvc.perform(get("/activity/list")
				.param("userId", "5c19d977-1ed9-42d1-9cbb-8d7e5b4911fd")
				.param("token", "5b8ae80d-c34e-4aca-92c3-3962c425964c")
				.param("key", "latest")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testGetActivityList2() throws Exception {
		MvcResult result = mockMvc.perform(get("/activity/list")
				.param("userId", "5c19d977-1ed9-42d1-9cbb-8d7e5b4911fd")
				.param("token", "5b8ae80d-c34e-4aca-92c3-3962c425964c")
				.param("key", "nearby")
				.param("longitude", "118.88409")
				.param("latitude", "32.096827")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testGetActivityList3() throws Exception {
		MvcResult result = mockMvc.perform(get("/activity/list")
				.param("userId", "5c19d977-1ed9-42d1-9cbb-8d7e5b4911fd")
				.param("token", "5b8ae80d-c34e-4aca-92c3-3962c425964c")
				.param("key", "hot")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
