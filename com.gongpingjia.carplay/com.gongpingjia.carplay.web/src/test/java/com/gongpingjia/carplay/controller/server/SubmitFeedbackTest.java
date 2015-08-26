package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.44 提交反馈
 * 
 * @author zhou shuofu
 */
public class SubmitFeedbackTest extends BaseTest {
	@Test
	public void testjoin() throws Exception {
		String userId = "082c79ac-1683-43ad-ab29-101faf80490c";
		String token = "87836150-2529-4c82-b99e-0e0ad7261247";
		String content = "are you ok?";
		String[] photos = { "4e62919d-db78-4811-bdf5-76a696617e23", "two", "three" };
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/feedback/submit").param("userId", userId).param("token", token)
						.param("content", content).param("photos", photos))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}

}
