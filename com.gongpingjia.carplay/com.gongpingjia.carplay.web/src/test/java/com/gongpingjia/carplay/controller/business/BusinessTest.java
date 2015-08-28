package com.gongpingjia.carplay.controller.business;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.gongpingjia.carplay.controller.BaseTest;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.po.PhoneVerification;

import net.sf.json.JSONObject;

public class BusinessTest extends BaseTest {

	@Autowired
	private PhoneVerificationDao phoneVerifyDao;
	
	String phone;
	String password;
	String userId;
	String token;
	String activityId;
	
	@Test
	public void registerTest() throws Exception {
		String phone="15365087965";
		String password="e10adc3949ba59abbe56e057f20f883e";
		String photo ="90179e74-e5ce-4925-89cb-d9de2d0ccfc7";
		
		// 2.1 获取验证码
		mockMvc.perform(get("/phone/" + phone + "/verification"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.2 验证码校验
		PhoneVerification phoneVerify = phoneVerifyDao.selectByPrimaryKey(phone);

		mockMvc
				.perform(MockMvcRequestBuilders.post("/phone/" + phone + "/verification").param("code",
						phoneVerify.getCode()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();

		// 2.5注册
		mockMvc
				.perform(MockMvcRequestBuilders.post("/user/register")
						.param("phone", phone)
						.param("code", phoneVerify.getCode())
						.param("password", password)
						.param("nickname", "孔明")
						.param("gender", "男")
						.param("birthMonth", "11")
						.param("birthYear", "1985")
						.param("birthDay", "5")
						.param("province", "江苏省")
						.param("city", "南京市")
						.param("district", "栖霞区")
						.param("photo", photo))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		//2.8登录
		mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
					.param("phone", phone)
					.param("password", password))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		//2.9忘记密码
		MvcResult result_9 = mockMvc
				.perform(MockMvcRequestBuilders.post("/user/password")
						.param("phone", phone)
						.param("password", password)
						.param("code", phoneVerify.getCode()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		
		//获取userId和token
		JSONObject userInfo=JSONObject.fromObject(result_9.getResponse().getContentAsString());
		userId=userInfo.getJSONObject("data").getString("userId");
		token=userInfo.getJSONObject("data").getString("token");
		System.out.println(userId+"     "+token);
		
		
		// 2.11 车主认证申请
		mockMvc.perform(MockMvcRequestBuilders.post("/user/"+userId+"/authentication?token="+token)
					.param("drivingExperience", "3")
					.param("carBrand", "大众")
					.param("carBrandLogo", "http://gongpingjia.qiniudn.com/img/logo/7206452af3747880ddd07398a95b9bdbebbc963e.jpg")
					.param("carModel", "大众cc")
					.param("slug", "dazhong-cc"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
	
			
	}
	
	@Test
	public void activityTest() throws Exception{
		
		String userId="90179e74-e5ce-4925-89cb-d9de2d0ccfc7";
		String token="7a8ec9b9-dbd9-4844-b53d-4192432a27df";
		
		//2.15创建活动
		MvcResult result_15 = mockMvc
				.perform(MockMvcRequestBuilders
						.post("/activity/register?userId="+userId+"&token="+token)
						.param("type", "旅行")
						.param("introduction", "DD活动期间晴空万里，道路通畅")
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
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		activityId=JSONObject.fromObject(result_15.getResponse().getContentAsString()).getJSONObject("data").getString("activityId");
		
		// 2.16 获取热门/附近/最新活动列表
		
		//最新
		mockMvc.perform(get("/activity/list")
				.param("userId", userId)
				.param("token", token)
				.param("key", "latest")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		//附近
		mockMvc.perform(get("/activity/list")
				.param("userId", userId)
				.param("token", token)
				.param("key", "nearby")
				.param("longitude", "118.88409")
				.param("latitude", "32.096827")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		//热门
		mockMvc.perform(get("/activity/list")
				.param("userId", userId)
				.param("token", token)
				.param("key", "hot")
				.param("limit", "5"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		// 2.17 获取活动详情
		mockMvc.perform(
				get("/activity/"+activityId+"/info")
				.param("userId", userId)
				.param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.isOrganizer").value(1))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.19评论活动
		mockMvc.perform(MockMvcRequestBuilders
				.post("/activity/"+activityId+"/comment")
				.param("userId", userId)
				.param("token", token)
				.param("comment", "testPublishComment3"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
		.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.18 获取活动评论
		mockMvc.perform(get("/activity/"+activityId+"/comment")
				.param("userId", userId)
				.param("token", token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
		.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].replyUserId").exists())
		.andDo(MockMvcResultHandlers.print()).andReturn();
		
		
	}
	
	@Test
	public void userActionTest()throws Exception{
		String userId="90179e74-e5ce-4925-89cb-d9de2d0ccfc7";
		String token="7a8ec9b9-dbd9-4844-b53d-4192432a27df";
		String activityId="d0007003-a8c6-45ed-be1b-9f1878a305c0";
		String password="e10adc3949ba59abbe56e057f20f883e";
		
		
		// 2.20 个人详情
		mockMvc.perform(get("/user/"+userId+"/info?userId="+userId+"&token="+token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
		.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.21 我(TA)的发布
		mockMvc.perform(get("/user/" + userId + "/post").param("userId", userId).param("token", token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
		.andReturn();
		
		// 2.22 我(TA)的关注
		mockMvc
		.perform(get("/user/" + userId + "/subscribe").param("userId", userId).param("token", token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
		.andReturn();
		
		// 2.23 我(TA)的参与
		 mockMvc
			.perform(get("/user/" + userId + "/join").param("userId", userId).param("token", token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print())
			.andReturn();
		 
		// 2.24 关注活动
		 Map<String,String> user2=getNewUser("18086503005", password, "90be2067-8993-49b8-b9cf-b90353ebba66");
		 mockMvc.perform(
					MockMvcRequestBuilders.post("/activity/"+activityId+"/subscribe")
						.param("userId", user2.get("userId"))
						.param("token", user2.get("token")))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.25 申请加入活动
		 mockMvc.perform(
					MockMvcRequestBuilders.post("/activity/"+activityId+"/join")
					.param("userId", user2.get("userId"))
					.param("token", user2.get("token"))
					.param("seat", "0"))
		 			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
					.andDo(MockMvcResultHandlers.print())
					.andReturn();
		 
		// 2.45 取消关注活动
		 mockMvc.perform(
					MockMvcRequestBuilders
						.post("/activity/"+activityId+"/unsubscribe")
						.param("userId", user2.get("userId"))
						.param("token", user2.get("token")))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		 
		// 2.26 获取申请列表
		 MvcResult result = mockMvc.perform(get("/user/" + userId + "/application/list").param("token", token))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
					.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
					.andDo(MockMvcResultHandlers.print())
					.andReturn();
		 
		 String applicationId=JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONArray("data").getJSONObject(0).getString("applicationId");
		 System.out.println(applicationId);
		 
		// 2.27 同意/拒绝 活动申请
		 mockMvc.perform(
					MockMvcRequestBuilders.post("/application/"+applicationId+"/process")
						.param("userId", userId)
						.param("token", token).param("action", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		 // 2.28 获取车座/成员信息
		 mockMvc
			.perform(get("/activity/"+activityId+"/members")
			.param("userId", userId)
			.param("token", token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
			.andReturn();
		 
		// 2.29 立即抢座
		 mockMvc
			.perform(
					MockMvcRequestBuilders
						.post("/activity/"+activityId+"/seat/take")
						.param("userId", user2.get("userId"))
						.param("token", user2.get("token"))
						.param("carId", "")
						.param("seatIndex", "1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();

		// 2.30 拉下座位
		 mockMvc.perform(
					MockMvcRequestBuilders
						.post("/activity/"+activityId+"/seat/return")
						.param("member", user2.get("userId"))
						.param("userId", userId)
						.param("token", token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		
		 // 2.31 移除成员
		 mockMvc.perform(
					MockMvcRequestBuilders
						.post("/activity/"+activityId+"/member/remove")
						.param("userId", userId)
						.param("token", token)
						.param("member", user2.get("userId")))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		
		 
		// 2.25 申请加入活动
		 mockMvc.perform(
					MockMvcRequestBuilders.post("/activity/"+activityId+"/join")
					.param("userId", user2.get("userId"))
					.param("token", user2.get("token"))
					.param("seat", "0"))
		 			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
					.andDo(MockMvcResultHandlers.print())
					.andReturn();
				 
		// 2.27 同意/拒绝 活动申请
		 mockMvc.perform(
					MockMvcRequestBuilders.post("/application/"+applicationId+"/process")
						.param("userId", userId)
						.param("token", token).param("action", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		 
		// 2.32 退出活动
				 mockMvc.perform(MockMvcRequestBuilders
								.post("/activity/"+activityId+"/quit")
								.param("userId", user2.get("userId"))
								.param("token", user2.get("token")))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
					.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
					.andDo(MockMvcResultHandlers.print()).andReturn();
		// 2.33 编辑活动
		 mockMvc
			.perform(
					MockMvcRequestBuilders
							.post("/activity/"+activityId+"/info?userId="+userId+"&token="+token)
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
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		 
		 // 2.34 我关注的人
		 mockMvc.perform(get("/user/"+userId+"/info?userId="+user2.get("userId")+"&token="+user2.get("token")))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();

		// 2.35 关注其他用户
		 mockMvc
			.perform(MockMvcRequestBuilders.post("/user/"+user2.get("userId")+"/listen?token="+user2.get("token"))
							.param("targetUserId", userId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();

		// 2.36 取消关注其他用户
		 mockMvc
			.perform(
					MockMvcRequestBuilders.post("/user/"+user2.get("userId")+"/unlisten?token="+user2.get("token"))
							.param("targetUserId", userId))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
 
		// 2.38 变更我的信息
		 mockMvc.perform(MockMvcRequestBuilders.post("/user/"+userId+"/info?token="+token)
							.param("nickname", "小苹果")
							.param("gender", "男")
							.param("drivingExperience", "3")
							.param("province", "江苏省")
							.param("city", "南京市")
							.param("district", "栖霞区"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
	
		// 2.41 获取最新消息数
		 mockMvc.perform(get("/user/" + userId + "/message/count").param("token", token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print())
			.andReturn();

		// 2.42 获取消息列表
		 MvcResult result_45= mockMvc
			.perform(get("/user/" + userId + "/message/list").param("token", token).param("type", "application"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print())
			.andReturn();
		
		// 2.46 获取最新版本信息
		 mockMvc.perform(get("/version?product=android"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
			.andReturn();

		String messagesId1=JSONObject.fromObject(result_45.getResponse().getContentAsString()).getJSONArray("data").getJSONObject(0).getString("messageId");
		String[] messages={messagesId1};
		// 2.47批量删除消息
		 mockMvc
			.perform(MockMvcRequestBuilders.post("/message/remove").param("userId", userId).param("token", token)
					.param("messages", messages))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=utf-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		 
		// 2.19评论活动
		mockMvc.perform(
					MockMvcRequestBuilders
						.post("/activity/"+activityId+"/comment")
						.param("userId", user2.get("userId"))
						.param("token", user2.get("token"))
						.param("comment", "testPublishComment3"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
			.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
			.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.18 获取活动评论
		MvcResult result_18=mockMvc.perform(
						get("/activity/"+activityId+"/comment")
						.param("userId", userId)
						.param("token", token))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].replyUserId").exists())
				.andDo(MockMvcResultHandlers.print()).andReturn();
				
		
		String commentId1=JSONObject.fromObject(result_18.getResponse().getContentAsString()).getJSONArray("data").getJSONObject(0).getString("commentId");
		String[] comments={commentId1};	
		
		// 2.48 批量删除评论
		mockMvc
		.perform(MockMvcRequestBuilders.post("/comment/remove").param("userId", userId).param("token", token)
				.param("comments", comments))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=utf-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
		.andDo(MockMvcResultHandlers.print()).andReturn();
	}
	
	
	
	
	
	
	/**
	 * 获取一个新用户，map里是userId,token
	 * 
	 * @param phone 手机号
	 * @param password 密码
	 * @param photo  头像，主键
	 * 
	 * */
	public Map<String ,String> getNewUser(String phone,String password,String photo) throws Exception{
			// 2.1 获取验证码
				mockMvc.perform(get("/phone/" + phone + "/verification").param("type","0"))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
						.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
						.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
						.andReturn();

				// 2.2 验证码校验
				PhoneVerification phoneVerify = phoneVerifyDao.selectByPrimaryKey(phone);

				mockMvc.perform(MockMvcRequestBuilders.post("/phone/" + phone + "/verification").param("code",
								phoneVerify.getCode()))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
						.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
						.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0)).andDo(MockMvcResultHandlers.print())
						.andReturn();

				// 2.5注册
				MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
								.param("phone", phone)
								.param("code", phoneVerify.getCode())
								.param("password", password)
								.param("nickname", "孔明")
								.param("gender", "男")
								.param("birthMonth", "11")
								.param("birthYear", "1985")
								.param("birthDay", "5")
								.param("province", "江苏省")
								.param("city", "南京市")
								.param("district", "栖霞区")
								.param("photo", photo))
						.andExpect(MockMvcResultMatchers.status().isOk())
						.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
						.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
						.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				
				JSONObject userInfo=JSONObject.fromObject(result.getResponse().getContentAsString());
				String userId=userInfo.getJSONObject("data").getString("userId");
				String token=userInfo.getJSONObject("data").getString("token");
				Map<String,String> userMap=new HashMap<String, String>();
				userMap.put("userId", userId);
				userMap.put("token", token);
				return userMap;
	}
	
	
	
	}

