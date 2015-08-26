package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class GetActivityCommentsTest extends BaseTest{
	
	@Test
	public void testGetActivityInfoTest() throws Exception {
		MvcResult result = mockMvc
				.perform(
						get("/activity/1a479aea-7d9f-4bf7-bbc6-ac9b362a6b7b/comment")
						.param("userId", "96f1a488-6d69-48fb-bcd5-ad14090881ab")
						.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].replyUserId").exists())
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}

}
