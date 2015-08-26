package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.21 我(TA)的发布
 * 
 * @author zhou shuofu
 */
public class GetUserPostTest extends BaseTest {

	@Test
	public void testUserPost() throws Exception {
		String userid1 = "7da6fac1-c360-449b-8b19-5231d8fa9b0a";
		String userId2 = "7da6fac1-c360-449b-8b19-5231d8fa9b0a";
		String token = "0b5815d8-865e-4c30-8632-647b488e90bb";

		MvcResult result = mockMvc
				.perform(get("/user/" + userid1 + "/post").param("userId", userId2).param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
