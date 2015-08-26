package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.41 获取最新消息数
 * 
 * @author zhou shuofu
 */
public class GetMessageCountTest extends BaseTest {

	@Test
	public void testMessageCount() throws Exception {
		String userId = "ad5b9c52-2e48-40ed-89b6-26154355262f";
		String token = "4efcd9c6-7a2c-4ff0-a8e4-feaecd197406";
		MvcResult result = mockMvc.perform(get("/user/" + userId + "/message/count").param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
