package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class AlterUserInfoTest extends BaseTest {

	@Test
	public void testAlterUserInfo() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/ab3a32e3-c05e-4a40-98ec-6476ef89f05a/info?token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53")
								.param("nickname", "小苹果")
								.param("gender", "男")
								.param("drivingExperience", "3")
								.param("province", "江苏省")
								.param("city", "南京市")
								.param("district", "栖霞区"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
