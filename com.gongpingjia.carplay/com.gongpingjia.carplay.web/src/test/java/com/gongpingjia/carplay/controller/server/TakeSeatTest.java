package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class TakeSeatTest extends BaseTest{

	//CarId不为空
	@Test
	public void testTakeSeat() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/599b751d-410d-4faa-8bd7-e51a3d85a454/seat/take")
							.param("userId", "c1793999-a36e-4dbc-be1d-931557519897")
							.param("token", "25f34a70-732a-4a57-b9be-508c87f54540")
							.param("carId", "015a1399-dc56-41d1-b69b-ef7e257a673e")
							.param("seatIndex", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//CarId 为空
	@Test
	public void testTakeSeat2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/599b751d-410d-4faa-8bd7-e51a3d85a454/seat/take")
							.param("userId", "5c2595d0-f15f-4258-ac3e-87af05698232")
							.param("token", "5a0b20bf-71f4-43e8-9abc-34da333b7b1d")
							.param("carId", "")
							.param("seatIndex", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
