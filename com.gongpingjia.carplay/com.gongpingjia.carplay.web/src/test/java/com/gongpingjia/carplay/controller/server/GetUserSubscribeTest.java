package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 2.22	我(TA)的关注
 * @author zhou shuofu 
 * */
public class GetUserSubscribeTest extends BaseTest {

	@Test
	public void testUserSubscribe() throws Exception {
		
		MvcResult result = mockMvc
				.perform(
						get("/user/c1793999-a36e-4dbc-be1d-931557519897/subscribe?userId=082c79ac-1683-43ad-ab29-101faf80490c&token=87836150-2529-4c82-b99e-0e0ad7261247&limit=2&ignore=1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
