package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class RemoveCommentsTest extends BaseTest {

	@Test
	public void testRemoveComments() throws Exception {
		String userId = "889fb8cf-a4e6-49ec-9c17-be2ea28e0cb0";
		String token = "11ef563d-cbcb-43bb-aa84-db05bb4e14be";
		String[] comments = { "98a575ed-2770-4208-b405-57a87dfd69ef" };

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/comment/remove").param("userId", userId).param("token", token)
						.param("comments", comments))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=utf-8"))
				// .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());

	}
}
