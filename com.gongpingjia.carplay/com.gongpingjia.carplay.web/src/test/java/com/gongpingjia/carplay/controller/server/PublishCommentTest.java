package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class PublishCommentTest extends BaseTest {

	//没有replyUserId
	@Test
	public void testPublishComment() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/3683878a-833f-4527-bf06-04805f20f604/comment")
							.param("userId", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19")
							.param("comment", "testPublishComment"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//replyUserId 参数用户不存在
	@Test
	public void testPublishComment2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/3683878a-833f-4527-bf06-04805f20f604/comment")
							.param("userId", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19")
							.param("replyUserId", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("comment", "testPublishComment2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	//replyUserId 用户存在
	@Test
	public void testPublishComment3() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
							.post("/activity/3683878a-833f-4527-bf06-04805f20f604/comment")
							.param("userId", "da51944f-ab85-4296-83f9-02603cb6937f")
							.param("token", "5aa0376c-e006-429f-a064-b0821bb92a19")
							.param("replyUserId", "889fb8cf-a4e6-49ec-9c17-be2ea28e0cb0")
							.param("comment", "testPublishComment3"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
}
