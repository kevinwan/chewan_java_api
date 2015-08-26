package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class RemoveMemberTest extends BaseTest {
	@Test
	public void testRemoveMember() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/599b751d-410d-4faa-8bd7-e51a3d85a454/member/remove")
							.param("userId", "c1793999-a36e-4dbc-be1d-931557519897")
							.param("token", "25f34a70-732a-4a57-b9be-508c87f54540")
							.param("member", "5c2595d0-f15f-4258-ac3e-87af05698232"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
}
