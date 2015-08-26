package com.gongpingjia.carplay.controller.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

/**
 * 2.7 获取车型
 * 
 * @author zhou shuofu
 */
public class GetCarModelTest extends BaseTest {

	@Test
	public void testModel() throws Exception {
		MvcResult result = mockMvc.perform(get("/car/model?brand=audi"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void testModel2() throws Exception {
		MvcResult result = mockMvc.perform(get("/car/model"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		Assert.assertNull(result.getModelAndView());
	}
}
