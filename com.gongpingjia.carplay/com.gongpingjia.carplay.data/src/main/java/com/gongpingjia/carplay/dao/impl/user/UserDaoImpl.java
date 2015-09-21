package com.gongpingjia.carplay.dao.impl.user;

import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User,String> implements UserDao {
}
