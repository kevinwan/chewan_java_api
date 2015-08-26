package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class RegisterUserTest extends BaseTest {

	@Test
	public void testUserRegister() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
								.param("phone", "12345678910")
								.param("code", "1100")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("nickname", "孔明")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1985")
								.param("birthDay", "5")
								.param("province", "江苏省")
								.param("city", "南京市")
								.param("district", "栖霞区")
								.param("photo", "412bac09-b9a0-46b5-a283-7442fa1eb76c"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testUserRegister2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
								.param("phone", "18951650023")
								.param("code", "2304")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("nickname", "烁夫")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1990")
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
	public void testUserRegister3() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
								.param("phone", "18951650026")
								.param("code", "2304")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("nickname", "烁夫")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1990")
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
	public void testUserRegister4() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
								.param("phone", "12345678910")
								.param("code", "8817")
								.param("password", "e10adc3949ba59abbe56e057f20f883e")
								.param("nickname", "孔明")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1985")
								.param("birthDay", "5")
								.param("province", "江苏省")
								.param("city", "南京市")
								.param("district", "栖霞区")
								.param("photo", "a94fec74-c3a6-4c78-988f-a24450284342"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testUserRegister5() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/register")
							.param("snsUid", "11ABD6303F2C5A51DADnp3W4NMC4")		
							.param("snsUserName", "剑神")
							.param("snsChannel", "wechat")
							.param("nickname", "剑神")
							.param("gender", "女")
							.param("birthMonth", "11")
							.param("birthYear", "1985")
							.param("birthDay", "5")
							.param("province", "江苏省")
							.param("city", "南京市")
							.param("district", "玄武区")
							.param("photo", "6d0b832d-401a-458a-bbdf-08a213699844"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
