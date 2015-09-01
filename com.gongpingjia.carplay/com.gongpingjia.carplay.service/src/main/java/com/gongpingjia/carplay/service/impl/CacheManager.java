package com.gongpingjia.carplay.service.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.cache.CacheService;
import com.gongpingjia.carplay.cache.util.CacheUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import com.gongpingjia.carplay.dao.EmchatTokenDao;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.po.EmchatToken;
import com.gongpingjia.carplay.po.TokenVerification;

/**
 * 对业务缓存的统一处理
 * 
 * @author licheng
 *
 */
@Service
public class CacheManager {

	@Autowired
	private TokenVerificationDao tokenDao;

	@Autowired
	private PhoneVerificationDao phoneDao;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private EmchatTokenDao emchatTokenDao;

	/**
	 * 根据UserID获取用户对应的Token信息, 先查缓存，再查数据库
	 * 
	 * @param userId
	 *            用户ID
	 * @return Token对象
	 */
	public TokenVerification getUserTokenVerification(String userId) {
		TokenVerification tokenVerify = cacheService.hget(CacheUtil.CacheName.USER_TOKEN, userId,
				TokenVerification.class);

		if (tokenVerify == null) {
			tokenVerify = tokenDao.selectByPrimaryKey(userId);
		}

		return tokenVerify;
	}

	/**
	 * 将用户的Token信息放入到缓存中
	 * 
	 * @param token
	 *            用户Token对象
	 * @return 如果更新成功返回true， 否则返回false
	 */
	public boolean setUserTokenVerification(TokenVerification token) {

		Long result = cacheService.hset(CacheUtil.CacheName.USER_TOKEN, token.getUserid(), token);

		return result != 0;
	}

	/**
	 * 获取公平价车型信息
	 * 
	 * @return 返回公平价车型信息
	 */
	public JSONObject getCarModel(String brand) {
		return cacheService.get(CacheUtil.getCarModelKey(brand), JSONObject.class);
	}

	/**
	 * 设置公平价车型信息，保存到缓存中,车的模型信息涉及到过期，所以存储略有区别
	 * 
	 * @param model
	 *            车型信息
	 * @return 保存结果
	 */
	public String setCarMode(String brand, String model) {
		String key = CacheUtil.getCarModelKey(brand);

		String result = cacheService.set(key, model);

		cacheService.expire(key, PropertiesUtil.getProperty("gongpingjia.cache.expire.seconds", 3600 * 24 * 30));

		return result;
	}

	/**
	 * 获取公平价品牌信息
	 * 
	 * @return 平拍信息
	 */
	public JSONObject getCarBrand() {
		return cacheService.get(CacheUtil.CacheName.CAR_BRAND, JSONObject.class);
	}

	/**
	 * 保存公平价品牌信息到缓存
	 * 
	 * @param brand
	 *            品牌信息字符串
	 * @return 返回保存到缓存的结果
	 */
	public String setCarBrand(String brand) {

		String result = cacheService.set(CacheUtil.CacheName.CAR_BRAND, brand);

		cacheService.expire(CacheUtil.CacheName.CAR_BRAND,
				PropertiesUtil.getProperty("gongpingjia.cache.expire.seconds", 3600 * 24 * 30));

		return result;
	}

	/**
	 * 从缓存中获取EmchatToken信息
	 * 
	 * @return 返回获取的EmchatToken对象
	 */
	public EmchatToken getEmchatToken() {
		EmchatToken token = cacheService.get(CacheUtil.CacheName.EMCHAT_TOKEN, EmchatToken.class);
		
		if (token == null) {
			token = emchatTokenDao.selectFirstOne();
		}

		return token;
	}

	/**
	 * 将EmchatToken信息保存到缓存中
	 * 
	 * @param emchatToken
	 *            EmchatToken对象
	 * @return 返回保存结果
	 */
	public String setEmchatToken(EmchatToken emchatToken) {
		return cacheService.set(CacheUtil.CacheName.EMCHAT_TOKEN, emchatToken);
	}
}
