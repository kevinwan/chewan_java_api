package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.AuthApplicationDao;
import com.gongpingjia.carplay.entity.user.AuthApplication;
import org.springframework.stereotype.Repository;

/**
 * Created by licheng on 2015/9/23.
 */
@Repository("authApplicationDao")
public class AuthApplicationDaoImpl extends BaseDaoImpl<AuthApplication, String> implements AuthApplicationDao {
}
