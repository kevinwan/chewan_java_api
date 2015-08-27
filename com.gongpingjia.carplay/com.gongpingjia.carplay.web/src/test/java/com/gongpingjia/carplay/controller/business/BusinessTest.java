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
		String phone="12345678912";
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
		mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/login")
								.param("phone", phone)
								.param("password", password))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
				.andDo(MockMvcResultHandlers.print()).andReturn();
		
		//2.9忘记密码
		MvcResult result_9 = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/password")
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
		mockMvc
				.perform(
						MockMvcRequestBuilders.post("/user/"+userId+"/authentication?token="+token)
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
		String token="fd9d8810-b7c2-4e9d-821c-fde4af667de3";
		
		//2.15创建活动
		MvcResult result_15 = mockMvc
				.perform(
						MockMvcRequestBuilders
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
		mockMvc.perform(
				MockMvcRequestBuilders
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
		mockMvc.perform(
				get("/activity/"+activityId+"/comment")
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
		String token="fd9d8810-b7c2-4e9d-821c-fde4af667de3";
		String activityId="abe3d01e-d0ed-4f18-bcc6-d93becddbc60";
		String password="e10adc3949ba59abbe56e057f20f883e";
		
		
		// 2.20 个人详情
		mockMvc.perform(get("/user/"+userId+"/info?userId="+userId+"&token="+token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
		.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(0))
		.andDo(MockMvcResultHandlers.print()).andReturn();
		
		// 2.21 我(TA)的发布
		mockMvc
		.perform(get("/user/" + userId + "/post").param("userId", userId).param("token", token))
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
		 Map<String,String> user2=getNewUser("12345678913", password, "90be2067-8993-49b8-b9cf-b90353ebba66");
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
					.andDo(MockMvcResultHandlers.print())
					.andReturn();
		 
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

