package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.23 我(TA)的参与
 * 
 * @author zhou shuofu
 */
public class GetUserJoinTest extends BaseTest {
	@Test
	public void testjoin() throws Exception {
		String userid1 = "889fb8cf-a4e6-49ec-9c17-be2ea28e0cb0";
		String userId2 = "082c79ac-1683-43ad-ab29-101faf80490c";
		String token = "87836150-2529-4c82-b99e-0e0ad7261247";
		MvcResult result = mockMvc
				.perform(get("/user/" + userid1 + "/join").param("userId", userId2).param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
