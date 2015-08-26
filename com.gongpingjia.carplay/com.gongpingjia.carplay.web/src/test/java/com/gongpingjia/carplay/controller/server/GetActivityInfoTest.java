package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class GetActivityInfoTest extends BaseTest {

	// 组织者
	@Test
	public void testGetActivityInfo() throws Exception {
		MvcResult result = mockMvc
				.perform(
						get("/activity/3683878a-833f-4527-bf06-04805f20f604/info")
						.param("userId", "da51944f-ab85-4296-83f9-02603cb6937f")
						.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.isOrganizer").value(1))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	// 非组织者
	@Test
	public void testGetActivityInfo2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						get("/activity/3683878a-833f-4527-bf06-04805f20f604/info")
						.param("userId", "889fb8cf-a4e6-49ec-9c17-be2ea28e0cb0")
						.param("token", "11ef563d-cbcb-43bb-aa84-db05bb4e14be"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.isOrganizer").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
