package com.gongpingjia.carplay.dao.impl.history;

import com.gongpingjia.carplay.dao.history.AuthenticationHistoryDao;
import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.data.history.AuthenticationHistory;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("authenticationHistoryDao")
public class AuthenticationHistoryDaoImpl extends BaseDaoImpl<AuthenticationHistory,String> implements AuthenticationHistoryDao {
}
