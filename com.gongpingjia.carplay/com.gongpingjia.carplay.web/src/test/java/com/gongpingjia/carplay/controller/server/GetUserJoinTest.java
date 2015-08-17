package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
* 2.23	 我(TA)的参与
*  @author zhou shuofu 
* */
public class GetUserJoinTest extends BaseTest {
	
	@Test
	public void testjoin() throws Exception {
		
		MvcResult result = mockMvc
				.perform(
						get("/user/ad5b9c52-2e48-40ed-89b6-26154355262f/join?userId=5c19d977-1ed9-42d1-9cbb-8d7e5b4911fd&token=5b8ae80d-c34e-4aca-92c3-3962c425964c"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
