package com.gongpingjia.carplay.dao.user;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.entity.user.User;

/**
 * Created by Administrator on 2015/9/21.
 */
public interface UserDao extends BaseDao<User, String> {

    String getCover(String userId);
}
