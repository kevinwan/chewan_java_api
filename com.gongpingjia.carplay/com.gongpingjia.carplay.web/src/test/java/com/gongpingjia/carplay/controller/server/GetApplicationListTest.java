package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.26 获取申请列表
 * 
 * @author zhou shuofu
 */
public class GetApplicationListTest extends BaseTest {

	@Test
	public void testGetApplicationList() throws Exception {
		String userId = "5c19d977-1ed9-42d1-9cbb-8d7e5b4911fd";
		String token = "8bc36576-f72f-4ad0-a982-4b333acc751d";
		MvcResult result = mockMvc.perform(get("/user/" + userId + "/application/list").param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
