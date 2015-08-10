package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gongpingjia.carplay.common.util.PropertiesUtil;

public class VersionTest extends BaseTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testVersion() throws Exception {
		
		System.out.println(PropertiesUtil.getThirdConfig());
		
		MvcResult result = mockMvc.perform(get("/version?product=android"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value("0"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testVersionDefault() throws Exception {
		MvcResult result = mockMvc.perform(get("/version"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value("0"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value("0"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		Assert.assertNull(result.getModelAndView());
	}
}
