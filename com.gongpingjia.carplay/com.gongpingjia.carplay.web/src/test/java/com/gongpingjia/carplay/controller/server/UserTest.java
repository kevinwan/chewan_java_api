package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
								.param("photo", "846de312-306c-4916-91c1-a5e69b158014"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testloginUser() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/login")
								.param("phone", "18951650021")
								.param("password", "e10adc3949ba59abbe56e057f20f883e"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testForgetPassword() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/password")
								.param("phone", "18951650021")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("code", "2302"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testApplyAuthentication() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/ab3a32e3-c05e-4a40-98ec-6476ef89f05a/authentication?token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53")
								.param("drivingExperience", "3")
								.param("carBrand", "大众")
								.param("carBrandLogo", "http://gongpingjia.qiniudn.com/img/logo/7206452af3747880ddd07398a95b9bdbebbc963e.jpg")
								.param("carModel", "大众cc")
								.param("slug", "dazhong-cc"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testUserInfo() throws Exception {
		
		MvcResult result = mockMvc.perform(get("/user/a3864fa5-35ad-408e-86a9-65b6c7f6472f/info?userId=ab3a32e3-c05e-4a40-98ec-6476ef89f05a&token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testUserListen() throws Exception {
		
		MvcResult result = mockMvc.perform(get("/user/ab3a32e3-c05e-4a40-98ec-6476ef89f05a/listen?token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testPayAttention() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/ab3a32e3-c05e-4a40-98ec-6476ef89f05a/listen?token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53")
								.param("targetUserId", "a3864fa5-35ad-408e-86a9-65b6c7f6472f"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testUnPayAttention() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/ab3a32e3-c05e-4a40-98ec-6476ef89f05a/unlisten?token=d82fbe5a-3f58-4c84-81a4-3d27224e8c53")
								.param("targetUserId", "a3864fa5-35ad-408e-86a9-65b6c7f6472f"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
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
