package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class SnsLoginTest extends BaseTest{
	@Test
	public void testSnsLogin() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/sns/login")
								.param("uid", "11ABD6303F2C5A51DADnp3W4NMC4")
								.param("username", "剑神")
								.param("url", "http://gb.cri.cn/mmsource/images/2015/08/25/85/311680634753988777.jpg")
								.param("channel", "wechat")
								.param("sign", "aa41f3578f9ce0a9fbce06a329eadd57"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testSnsLogin2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/sns/login")
								.param("uid", "11ABD6303F2C5A51DADnp3W4NMC4")
								.param("username", "剑神")
								.param("url", "http://gb.cri.cn/mmsource/images/2015/08/25/85/311680634753988777.jpg")
								.param("channel", "qq")
								.param("sign", "cb4beb36ad2bcedca1257d1f242df265"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(
						MockMvcResultMatchers.content().contentType(
								"application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
	
}
