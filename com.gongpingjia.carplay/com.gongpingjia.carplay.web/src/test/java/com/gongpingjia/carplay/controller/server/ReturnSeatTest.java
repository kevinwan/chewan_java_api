package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class ReturnSeatTest  extends BaseTest{

	@Test
	public void testTakeSeat() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/599b751d-410d-4faa-8bd7-e51a3d85a454/seat/return")
							.param("member", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("userId", "c1793999-a36e-4dbc-be1d-931557519897")
							.param("token", "25f34a70-732a-4a57-b9be-508c87f54540"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testTakeSeat2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/599b751d-410d-4faa-8bd7-e51a3d85a454/seat/return")
							.param("member", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("userId", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
