package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class GetMemberAndSeatInfoTest extends BaseTest{

	@Test
	public void testGetMemberAndSeatInfo() throws Exception{
		MvcResult result = mockMvc
				.perform(get("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/members")
				.param("userId", "082c79ac-1683-43ad-ab29-101faf80490c")
				.param("token", "87836150-2529-4c82-b99e-0e0ad7261247"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
