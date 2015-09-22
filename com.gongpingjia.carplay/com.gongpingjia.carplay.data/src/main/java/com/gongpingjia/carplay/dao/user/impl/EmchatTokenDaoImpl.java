package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.EmchatTokenDao;
import com.gongpingjia.carplay.entity.user.EmchatToken;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("emchatTokenDao")
public class EmchatTokenDaoImpl extends BaseDaoImpl<EmchatToken,String> implements EmchatTokenDao{
}
