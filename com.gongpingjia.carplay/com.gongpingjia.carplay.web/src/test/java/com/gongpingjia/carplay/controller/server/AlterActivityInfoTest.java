package com.gongpingjia.carplay.controller.server;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;

public class AlterActivityInfoTest extends BaseTest{

	@Test
	public void AlterActivityInfo() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/activity/59455a11-0111-487f-b600-1f92bc05f956/info?userId=28e190e0-a1c2-433b-bd76-39ac1aa91536&token=a8103bb1-f9fd-49b1-a0e8-896fca148fb6")
								.param("type", "旅行")
								.param("introduction", "AA活动期间晴空万里，道路通畅")
								.param("cover", "4d51a321-f953-4623-b7ab-abd4fb858e77")
								.param("cover", "59336875-0128-4121-862a-22d1db86fe03")
								.param("location", "南京邮电大学")
								.param("longitude", "118.869529")
								.param("latitude", "32.02632")
								.param("start", "1436494940937")
								.param("end",   "1436494955800")
								.param("province", "江苏省")
								.param("city", "南京")
								.param("district", "鼓楼区")
								.param("pay", "我请客")
								.param("seat", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		Assert.assertNull(result.getModelAndView());
	}
	
	@Test
	public void AlterActivityInfo2() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders
								.post("/activity/59455a11-0111-487f-b600-1f92bc05f956/info?userId=28e190e0-a1c2-433b-bd76-39ac1aa91536&token=a8103bb1-f9fd-49b1-a0e8-896fca148fb6")
								.param("type", "旅行")
								.param("introduction", "AA活动期间晴空万里，道路通畅")
								.param("cover", "4d51a321-f953-4623-b7ab-abd4fb858e77")
								.param("cover", "59336875-0128-4121-862a-22d1db86fe03")
								.param("location", "南京邮电大学")
								.param("longitude", "118.869529")
								.param("latitude", "32.02632")
								.param("start", "1436494960937")
								.param("end",   "1436494955800")
								.param("province", "江苏省")
								.param("city", "南京")
								.param("district", "鼓楼区")
								.param("pay", "我请客")
								.param("seat", "2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		Assert.assertNull(result.getModelAndView());
	}
}
