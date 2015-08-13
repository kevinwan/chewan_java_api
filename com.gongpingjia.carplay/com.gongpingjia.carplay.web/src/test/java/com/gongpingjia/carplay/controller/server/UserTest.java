package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UserTest extends BaseTest {

	
	@Test
	public void testUserRegister() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
								.param("phone", "18951650021")
								.param("code", "2302")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("nickname", "孔明")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1985")
								.param("birthDay", "5")
								.param("province", "江苏省")
								.param("city", "南京市")
								.param("district", "栖霞区")
								.param("photo", "4d672627-860c-4118-bcbd-2978aca469ad"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
