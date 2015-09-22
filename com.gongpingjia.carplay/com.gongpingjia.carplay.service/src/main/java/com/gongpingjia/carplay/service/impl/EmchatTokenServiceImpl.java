package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.cache.CacheService;
import com.gongpingjia.carplay.cache.util.CacheUtil;
import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.DateUtil;
import com.gongpingjia.carplay.dao.user.EmchatTokenDao;
import com.gongpingjia.carplay.entity.user.EmchatToken;
import com.gongpingjia.carplay.service.EmchatTokenService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by heyongyu on 2015/9/22.
 */
@Service("emchatService")
public class EmchatTokenServiceImpl implements EmchatTokenService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(EmchatTokenService.class);

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ChatThirdPartyService chatThirdPartyService;

    @Autowired
    private EmchatTokenDao emchatTokenDao;

    @Override
    public String getToken() throws ApiException {
        LOG.debug("Query chat Token from cache server");
        EmchatToken token = cacheService.get(CacheUtil.CacheName.EMCHAT_TOKEN, EmchatToken.class);
        if (token != null) {
            if (token.getExpire().getTime() > DateUtil.getTime()) {
                // 如果token时间大于当前时间表示没有过期，直接返回
                return token.getToken();
            }
        }
        LOG.debug("Query chat token from database while cache expired or not exist");
        // token不存在或者过期，需要重新获取
        JSONObject json = chatThirdPartyService.getApplicationToken();
        EmchatToken refresh = new EmchatToken();
        refresh.setApplication(json.getString("application"));
        // 注意 聊天服务器返回的时间单位为：秒
        refresh.setExpire(new Date(DateUtil.getTime() + json.getLong("expires_in") * 1000));
        refresh.setToken(json.getString("access_token"));
        if (token == null) {
            emchatTokenDao.save(refresh);
        } else {
            emchatTokenDao.update(refresh.getId(), refresh);
        }
        LOG.debug("Update token in the database");
        cacheService.set(CacheUtil.CacheName.EMCHAT_TOKEN, refresh);
        return refresh.getToken();
    }
}
