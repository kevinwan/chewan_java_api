package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserTokenDao;
import com.gongpingjia.carplay.entity.user.UserToken;
import org.springframework.stereotype.Repository;

/**
 * Created by licheng on 2015/9/22.
 */
@Repository("userToken")
public class UserTokenImpl extends BaseDaoImpl<UserToken, String> implements UserTokenDao {
}
