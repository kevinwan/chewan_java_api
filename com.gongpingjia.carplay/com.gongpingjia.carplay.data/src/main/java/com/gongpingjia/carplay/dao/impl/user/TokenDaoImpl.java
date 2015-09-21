package com.gongpingjia.carplay.dao.impl.user;

import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.TokenDao;
import com.gongpingjia.carplay.entity.user.Token;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("tokenDao")
public class TokenDaoImpl extends BaseDaoImpl<Token,String> implements TokenDao {
}
