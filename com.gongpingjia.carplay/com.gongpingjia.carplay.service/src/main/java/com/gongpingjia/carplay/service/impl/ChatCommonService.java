package com.gongpingjia.carplay.service.impl;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.common.util.EncoderHandler;
import com.gongpingjia.carplay.dao.EmchatTokenDao;
import com.gongpingjia.carplay.po.EmchatToken;

@Service
public class ChatCommonService {

	private static final Logger LOG = LoggerFactory.getLogger(ChatCommonService.class);

	@Autowired
	private ChatThirdPartyService chatThirdService;

	@Autowired
	private EmchatTokenDao emchatTokenDao;

	@Autowired
	private CacheManager cacheManager;

	/**
	 * 获取应用的Token
	 * 
	 * @return 应用Token字符串
	 * @throws ApiException
	 */
	public String getChatToken() throws ApiException {

		LOG.debug("Query chat Token from cache server");
		EmchatToken token = cacheManager.getEmchatToken();
		if (token != null) {
			if (token.getExpire() > DateUtil.getTime()) {
				// 如果token时间大于当前时间表示没有过期，直接返回
				return token.getToken();
			}
		}

		LOG.debug("Query chat token from database while cache expired or not exist");
		// token不存在或者过期，需要重新获取
		JSONObject json = chatThirdService.getApplicationToken();
		EmchatToken refresh = new EmchatToken();
		refresh.setApplication(json.getString("application"));
		// 注意 聊天服务器返回的时间单位为：秒
		refresh.setExpire(DateUtil.getTime() + json.getLong("expires_in") * 1000);
		refresh.setToken(json.getString("access_token"));
		if (token == null) {
			emchatTokenDao.insert(refresh);
		} else {
			emchatTokenDao.updateByPrimaryKey(refresh);
		}

		LOG.debug("Update token in the database");
		cacheManager.setEmchatToken(refresh);

		return refresh.getToken();
	}

	/**
	 * 根据用户ID获取聊天用户对应的username
	 * 
	 * @param userid
	 *            用户ID
	 * @return 返回聊天服务器上存放的username
	 */
	public String getUsernameByUserid(String userid) {
		return EncoderHandler.encodeByMD5(userid);
	}
}
