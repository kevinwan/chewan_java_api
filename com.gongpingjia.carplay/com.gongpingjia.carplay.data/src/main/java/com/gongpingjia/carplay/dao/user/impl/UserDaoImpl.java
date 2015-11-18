package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User,String> implements UserDao {

    @Override
    public User findUserByPhone(String phone) {
        return super.findOne(Query.query(Criteria.where("phone").is(phone)));
    }
}
