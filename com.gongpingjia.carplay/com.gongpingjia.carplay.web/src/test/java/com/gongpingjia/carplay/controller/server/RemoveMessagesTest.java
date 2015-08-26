package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class RemoveMessagesTest extends BaseTest {

	@Test
	public void removeMessageTest() throws Exception {
		String userId = "ad5b9c52-2e48-40ed-89b6-26154355262f";
		String token = "764102c5-bc5c-4bf0-89ae-a8371bca1151";
		String[] messages = {"0059c3a5-9a16-40d8-9ed7-264d82ca089e","1887dd1c-c6f9-46d7-b696-a32bd740d48e"};

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/message/remove").param("userId", userId).param("token", token)
						.param("messages", messages))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=utf-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());

	}
}
