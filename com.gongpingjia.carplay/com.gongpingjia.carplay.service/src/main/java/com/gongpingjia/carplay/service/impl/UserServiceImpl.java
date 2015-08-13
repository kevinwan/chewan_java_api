package com.gongpingjia.carplay.service.impl;

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
import com.gongpingjia.carplay.common.util.ToolsUtils;
import com.gongpingjia.carplay.dao.EmchatAccountDao;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.dao.UserAlbumDao;
import com.gongpingjia.carplay.dao.UserDao;
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
		
		//验证参数 
	    if (!ToolsUtils.isPhoneNumber(user.getPhone()) || (code.length() != 4) || !CodeGenerator.isUUID(user.getPhoto())) {
	    	LOG.warn("invalid params");
	    	return ResponseDo.buildFailureResponse("输入参数有误");
	    }
	    user.setPhoto(Constants.Photo.PHOTO_HEAD + user.getPhoto() + Constants.Photo.PHOTO_END);
	    
	    //验证验证码
	    PhoneVerification phoneVerification = phoneVerificationDao.selectByPrimaryKey(user.getPhone());
	    if (null == phoneVerification){
	    	LOG.warn("Cannot find code of this phone :" + user.getPhone());
	    	return ResponseDo.buildFailureResponse("未能获取该手机的验证码");
	    } else if (!code.equals(phoneVerification.getCode())){
	    	LOG.warn("Code not correct,phone : " + user.getPhone() + ". error code :" + code);
	    	return ResponseDo.buildFailureResponse("验证码错误");
	    } else if (phoneVerification.getExpire() < new Date().getTime()/1000){
	    	LOG.warn("Code expired");
	    	return ResponseDo.buildFailureResponse("该验证码已过期，请重新获取验证码");
	    }
	    
	    //判断七牛上图片是否存在
	    Boolean isPhotoExist;
		try {
			isPhotoExist = photoService.isExist(user.getPhoto());
		} catch (ApiException e) {
			//抛出异常则认为图片不存在
			isPhotoExist = false;
		}
	    if (isPhotoExist){
	    	LOG.warn("photo Exist");
	    	return ResponseDo.buildFailureResponse("注册图片已存在");
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
	    emchatAccount.setUsername(user.getNickname());
	    emchatAccountDao.insert(emchatAccount);
	    
	    UserAlbum userAlbum = new UserAlbum();
	    userAlbum.setId(CodeGenerator.generatorId());
	    userAlbum.setUserid(uuid);
	    userAlbum.setCreatetime(DateUtil.getTime());
	    userAlbumDao.insert(userAlbum);
	    
	    Map<String, String> data = new HashMap<String, String>();
	    data.put("userId", uuid);
	    data.put("token", tokenVerification.getToken());
		return ResponseDo.buildSuccessResponse(data);
	}
	

}
