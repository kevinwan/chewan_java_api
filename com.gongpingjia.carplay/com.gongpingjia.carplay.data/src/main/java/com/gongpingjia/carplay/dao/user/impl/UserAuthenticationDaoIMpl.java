package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserAuthenticationDao;
import com.gongpingjia.carplay.entity.user.UserAuthentication;
import org.springframework.stereotype.Repository;

/**
 * Created by licheng on 2015/10/10.
 */
@Repository("userAuthenticationDao")
public class UserAuthenticationDaoImpl extends BaseDaoImpl<UserAuthentication, String> implements UserAuthenticationDao {
}
