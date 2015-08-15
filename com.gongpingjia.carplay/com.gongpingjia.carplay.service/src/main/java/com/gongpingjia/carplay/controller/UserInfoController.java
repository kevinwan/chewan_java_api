package com.gongpingjia.carplay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.service.UserService;

@RestController
public class UserInfoController {

	private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDo register(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "code") String code,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "nickname") String nickname,
			@RequestParam(value = "gender") String gender,
			@RequestParam(value = "birthYear") Integer birthYear,
			@RequestParam(value = "birthMonth") Integer birthMonth,
			@RequestParam(value = "birthDay") Integer birthDay,
			@RequestParam(value = "province") String province,
			@RequestParam(value = "city") String city,
			@RequestParam(value = "district") String district,
			@RequestParam(value = "photo") String photo) {

		LOG.debug("register is called, request parameter produce:");

		User user = new User();
		user.setPhone(phone);
		user.setPassword(password);
		user.setNickname(nickname);
		user.setGender(gender);
		user.setBirthyear(birthYear);
		user.setBirthmonth(birthMonth);
		user.setBirthday(birthDay);
		user.setProvince(province);
		user.setCity(city);
		user.setDistrict(district);
		user.setPhoto(photo);

		return userService.register(user, code);
	}
	
	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDo loginUser(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "password") String password) {

		LOG.debug("login is called, request parameter produce:");

		User user = new User();
		user.setPhone(phone);
		user.setPassword(password);

		return userService.loginUser(user);
	}
	
	@RequestMapping(value = "/user/password", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDo forgetPassword(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "code") String code,
			@RequestParam(value = "password") String password) {

		LOG.debug("forgetPassword is called, request parameter produce:");

		User user = new User();
		user.setPhone(phone);
		user.setPassword(password);

		return userService.forgetPassword(user, code);
	}
	
	@RequestMapping(value = "/user/{userId}/authentication", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDo applyAuthentication(@RequestParam(value = "token") String token,
			@RequestParam(value = "drivingExperience") Integer drivingExperience,
			@RequestParam(value = "carBrand") String carBrand,
			@RequestParam(value = "carBrandLogo") String carBrandLogo,
			@RequestParam(value = "carModel") String carModel,
			@RequestParam(value = "slug") String slug,
			@PathVariable String userId) {

		LOG.debug("applyAuthentication is called, request parameter produce:");

		AuthenticationApplication authenticationApplication = new AuthenticationApplication();
		authenticationApplication.setDrivingexperience(drivingExperience);
		authenticationApplication.setBrand(carBrand);
		authenticationApplication.setBrandlogo(carBrandLogo);
		authenticationApplication.setModel(carModel);
		authenticationApplication.setSlug(slug);
		
		return userService.applyAuthentication(authenticationApplication,token,userId);
	}
	
}
