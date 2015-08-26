package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class GetAvailableSeatsTest extends BaseTest {

	@Test
	public void testGetAvailableSeats() throws Exception {
		String userId = "846de312-306c-4916-91c1-a5e69b158014";
		String token = "846de312-306c-4916-91c1-a5e69b158014";
		MvcResult result = mockMvc.perform(get("/user/" + userId + "/seats").param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testGetAvailableSeats2() throws Exception {
		// String userId = "846de312-306c-4916-91c1-a5e69b158014";
		String token = "846de312-306c-4916-91c1-a5e69b158014";
		MvcResult result = mockMvc.perform(get("/user/" + 123 + "/seats").param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}

	@Test
	public void testGetAvailableSeats3() throws Exception {
		String userId = "846de312-306c-4916-91c1-a5e69b158014";
		// String token = "846de312-306c-4916-91c1-a5e69b158014";
		MvcResult result = mockMvc.perform(get("/user/" + userId + "/seats").param("token", "1234"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
