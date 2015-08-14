package com.gongpingjia.carplay.service.impl;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.photo.PhotoService;
import com.gongpingjia.carplay.common.util.CodeGenerator;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.EncoderHandler;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.ToolsUtils;
import com.gongpingjia.carplay.dao.AuthenticationApplicationDao;
import com.gongpingjia.carplay.dao.AuthenticationChangeHistoryDao;
import com.gongpingjia.carplay.dao.CarDao;
import com.gongpingjia.carplay.dao.EmchatAccountDao;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.dao.UserAlbumDao;
import com.gongpingjia.carplay.dao.UserDao;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.AuthenticationChangeHistory;
import com.gongpingjia.carplay.po.Car;
import com.gongpingjia.carplay.po.EmchatAccount;
import com.gongpingjia.carplay.po.PhoneVerification;
import com.gongpingjia.carplay.po.TokenVerification;
import com.gongpingjia.carplay.po.User;
import com.gongpingjia.carplay.po.UserAlbum;
import com.gongpingjia.carplay.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private PhoneVerificationDao phoneVerificationDao;
	
	@Autowired
	private PhotoService photoService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TokenVerificationDao tokenVerificationDao;
	
	@Autowired
	private EmchatAccountDao emchatAccountDao;
	
	@Autowired
	private UserAlbumDao userAlbumDao;
	
	@Autowired
	private CarDao carDao;
	
	@Autowired
	private AuthenticationApplicationDao authenticationApplicationDao;
	
	@Autowired
	private AuthenticationChangeHistoryDao authenticationChangeHistoryDao;
	
	@Override
	public List<User> queryUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUser(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int saveUser(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional 
	public ResponseDo register(User user, String code){
		
		Map<String, Object> data = new HashMap<String, Object>();
		//验证参数 
	    if (!CommonUtil.isPhoneNumber(user.getPhone()) || (code.length() != 4) || !CommonUtil.isUUID(user.getPhoto())) {
	    	LOG.warn("invalid params");
	    	return ResponseDo.buildFailureResponse("输入参数有误");
	    }
	    user.setPhoto(MessageFormat.format(Constants.USER_PHOTO_KEY, user.getPhoto()));
	    
	    //验证验证码
	    ResponseDo failureResponse = checkPhoneVerification(user.getPhone(),code);
	    if (null != failureResponse){
	    	return failureResponse;
	    }
	    
	    //判断七牛上图片是否存在
		try {
			if (!photoService.isExist(user.getPhoto())){
		    	LOG.warn("photo not Exist");
		    	return ResponseDo.buildFailureResponse("注册图片未上传");
			}
		} catch (ApiException e) {
	    	LOG.warn("photo not Exist");
	    	return ResponseDo.buildFailureResponse("注册图片未上传");
		}
	    
	    //判断用户是否注册过
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("phone", user.getPhone());
	    List<User> users = userDao.selectByParam(param);
	    if (null != users && users.size() > 0){
	    	LOG.warn("Phone already ed");
	    	return ResponseDo.buildFailureResponse("该用户已注册");
	    }
	    String uuid = CodeGenerator.generatorId();
	    user.setId(uuid);
	    //注册用户
	    userDao.insert(user);
	    
	    TokenVerification tokenVerification = new TokenVerification();
	    tokenVerification.setUserid(uuid);
	    tokenVerification.setToken(CodeGenerator.generatorId());
	    tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
	    tokenVerificationDao.insert(tokenVerification);
	    
	    EmchatAccount emchatAccount = new EmchatAccount();
	    emchatAccount.setUserid(uuid);
	    emchatAccount.setPassword(user.getPassword());
	    emchatAccount.setRegistertime(DateUtil.getTime());
	    emchatAccount.setUsername(EncoderHandler.encodeByMD5(user.getNickname()));
	    emchatAccountDao.insert(emchatAccount);
	    
	    UserAlbum userAlbum = new UserAlbum();
	    userAlbum.setId(CodeGenerator.generatorId());
	    userAlbum.setUserid(uuid);
	    userAlbum.setCreatetime(DateUtil.getTime());
	    userAlbumDao.insert(userAlbum);
	    
	    data.put("userId", uuid);
	    data.put("token", tokenVerification.getToken());
		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo loginUser(User user) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		//验证参数 
	    if (!CommonUtil.isPhoneNumber(user.getPhone())) {
	    	LOG.warn("invalid params");
	    	return ResponseDo.buildFailureResponse("输入参数有误");
	    }
		
	    //查找用户
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("phone", user.getPhone());
	    List<User> users = userDao.selectByParam(param);
	    if (null != users && users.size() > 0){
	    	User userData = users.get(0);
	    	if (!user.getPassword().equals(userData.getPassword())){
		    	LOG.warn("Fail to find user");
		    	return ResponseDo.buildFailureResponse("密码不正确，请核对后重新登录");
	    	} else {
	    		data.put("userId", userData.getId());
	    		data.put("isAuthenticated", userData.getIsauthenticated());
	    		
	    		//获取用户授权信息
	    		ResponseDo tokenResponseDo = getUserToken(userData.getId());
	    		if (1 == tokenResponseDo.getResult()){
	    			return tokenResponseDo;
	    		}
	    		data.put("token", tokenResponseDo.getData());
	    		
    			//查询用户车辆信息
    			Car car = carDao.selectByUserId(userData.getId());
    			if (null != car){
    			    data.put("brand", car.getBrand());
    			    data.put("brandLogo", 
    			    		car.getBrandlogo() == null ? "" : PropertiesUtil.getProperty("gongpingjia.brand.logo.url", "") + car.getBrandlogo());
    			    data.put("model", car.getModel());
    			    data.put("seatNumber", car.getSeat());
    			}
	    	}
	    } else {
	    	LOG.warn("Fail to find user");
	    	return ResponseDo.buildFailureResponse("用户不存在，请注册后登录");
	    }
	    
		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo forgetPassword(User user, String code) {
		Map<String, Object> data = new HashMap<String, Object>();
		//验证参数 
	    if (!ToolsUtils.isPhoneNumber(user.getPhone()) || (code.length() != 4) ) {
	    	LOG.warn("invalid params");
	    	return ResponseDo.buildFailureResponse("输入参数有误");
	    }
	    
	    //验证验证码
	    ResponseDo failureResponse = checkPhoneVerification(user.getPhone(),code);
	    if (null != failureResponse){
	    	return failureResponse;
	    }
	    
	    //查询用户注册信息
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("phone", user.getPhone());
	    List<User> users = userDao.selectByParam(param);
	    if (null == users || users.size() < 1){
	    	LOG.warn("Fail to find user");
	    	return ResponseDo.buildFailureResponse("用户不存在");
	    }
	    
	    //跟新密码
	    User upUser = users.get(0);
	    upUser.setPassword(user.getPassword());
		if (0 == userDao.updateByPrimaryKey(upUser)){
	    	LOG.warn("Fail to update password");
	    	return ResponseDo.buildFailureResponse("更新密码失败");
		}
		
		//获取用户授权信息
		ResponseDo tokenResponseDo = getUserToken(upUser.getId());
		if (1 == tokenResponseDo.getResult()){
			return tokenResponseDo;
		}
		data.put("userId", upUser.getId());
		data.put("token", tokenResponseDo.getData());
	    
		return ResponseDo.buildSuccessResponse(data);
	}

	@Override
	public ResponseDo applyAuthentication(
			AuthenticationApplication authen,String token,String userId) {
		
		//验证参数 
	    if (!CommonUtil.isUUID(token) || !CommonUtil.isUUID(userId) || (authen.getDrivingexperience() > 50) 
	    		|| (authen.getBrandlogo().indexOf("http://") < 0) || authen.getBrandlogo().lastIndexOf("/") <= 6 ) {
	    	LOG.warn("invalid params");
	    	return ResponseDo.buildFailureResponse("输入参数有误");
	    }
	    
	    //获取认证口令
		TokenVerification tokenVerification = tokenVerificationDao.selectByPrimaryKey(userId);
		if (null == tokenVerification){
	    	LOG.warn("Fail to get token and expire info from token_verification");
	    	return ResponseDo.buildFailureResponse("获取用户授权信息失败");
		}
		if (tokenVerification.getExpire() <= DateUtil.getTime() || !token.equals(tokenVerification.getToken())){
	    	LOG.warn("Token expired or token not correct");
	    	return ResponseDo.buildFailureResponse("口令已过期，请重新登录获取新口令");
		}
		
		User user = userDao.selectByPrimaryKey(userId);
		if (null == user){
	    	LOG.warn("User not exist");
	    	return ResponseDo.buildFailureResponse("用户不存在");
		}
		if (user.getIsauthenticated() != null && 0 != user.getIsauthenticated()){
	    	LOG.warn("User already authenticated");
	    	return ResponseDo.buildFailureResponse("用户已认证，请勿重复认证");
		}
		
	    //判断七牛上图片是否存在
		try {
			if (!photoService.isExist(user.getPhoto())){
		    	LOG.warn("photo not Exist");
		    	return ResponseDo.buildFailureResponse("注册图片未上传");
			}
		} catch (ApiException e) {
	    	LOG.warn("photo not Exist");
	    	return ResponseDo.buildFailureResponse("注册图片未上传");
		}
	    
		//是否已经提起认证处理
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		List<AuthenticationApplication> authenticationApplications = authenticationApplicationDao.selectByParam(param);
		if (null != authenticationApplications && authenticationApplications.size() > 0){
	    	LOG.warn("already applied authentication");
	    	return ResponseDo.buildFailureResponse("之前的认证申请仍在处理中");
		}
		
		authen.setId(CodeGenerator.generatorId());
		authen.setUserid(userId);
		authen.setBrandlogo(authen.getBrandlogo().substring(authen.getBrandlogo().lastIndexOf('/') + 1));
		authen.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authen.setCreatetime(DateUtil.getTime());
		if (0 == authenticationApplicationDao.insert(authen)){
	    	LOG.warn("Fail to insert into authentication_application table");
	    	return ResponseDo.buildFailureResponse("未能成功申请认证");
		}
		AuthenticationChangeHistory authenHistory = new AuthenticationChangeHistory();
		authenHistory.setId(CodeGenerator.generatorId());
		authenHistory.setApplicationid(authen.getId());
		authenHistory.setStatus(Constants.ApplyAuthenticationStatus.STATUS_PENDING_PROCESSED);
		authenHistory.setTimestamp(DateUtil.getTime());
		if (0 == authenticationChangeHistoryDao.insert(authenHistory)){
	    	LOG.warn("Fail to insert into authentication_change_history table");
	    	return ResponseDo.buildFailureResponse("添加认证申请历史记录失败");
		}
		
		return ResponseDo.buildSuccessResponse("");
	}
	
	
	private ResponseDo checkPhoneVerification(String phone,String code){
		
	    //验证验证码
	    PhoneVerification phoneVerification = phoneVerificationDao.selectByPrimaryKey(phone);
	    if (null == phoneVerification){
	    	LOG.warn("Cannot find code of this phone :" + phone);
	    	return ResponseDo.buildFailureResponse("未能获取该手机的验证码");
	    } else if (!code.equals(phoneVerification.getCode())){
	    	LOG.warn("Code not correct,phone : " + phone + ". error code :" + code);
	    	return ResponseDo.buildFailureResponse("验证码错误");
	    } else if (phoneVerification.getExpire() < new Date().getTime()/1000){
	    	LOG.warn("Code expired");
	    	return ResponseDo.buildFailureResponse("该验证码已过期，请重新获取验证码");
	    }
	    return null;
	}
	
	private ResponseDo getUserToken(String userId){
		TokenVerification tokenVerification = tokenVerificationDao.selectByPrimaryKey(userId);
		if (null == tokenVerification){
	    	LOG.warn("Fail to get token and expire info from token_verification");
	    	return ResponseDo.buildFailureResponse("获取用户授权信息失败");
		}
		
		//如果过期 跟新Token
		if (tokenVerification.getExpire() > DateUtil.getTime()){
			return ResponseDo.buildSuccessResponse(tokenVerification.getToken());
		} else {
			String uuid = CodeGenerator.generatorId();
		    tokenVerification.setToken(uuid);
		    tokenVerification.setExpire(DateUtil.addTime(DateUtil.getDate(), Calendar.DATE, 7));
			if (0 == tokenVerificationDao.updateByPrimaryKey(tokenVerification)){
		    	LOG.warn("Fail to update new token and expire info");
		    	return ResponseDo.buildFailureResponse("更新用户授权信息失败");
			}
			return ResponseDo.buildSuccessResponse(uuid);
		}
	}
}
