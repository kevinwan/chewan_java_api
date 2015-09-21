package com.gongpingjia.carplay.dao.impl.user;

import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.EmchatTokenDao;
import com.gongpingjia.carplay.data.user.EmchatToken;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("emchatTokenDao")
public class EmchatTokenDaoImpl extends BaseDaoImpl<EmchatToken,String> implements EmchatTokenDao{
}
