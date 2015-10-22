package com.gongpingjia.carplay.dao.history.impl;

import com.gongpingjia.carplay.dao.common.BaseDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.history.InterestMessageDao;
import com.gongpingjia.carplay.entity.history.InterestMessage;
import org.springframework.stereotype.Repository;

/**
 * Created by licheng on 2015/10/21.
 */
@Repository("interestMessageDao")
public class InterestMessageDaoImpl extends BaseDaoImpl<InterestMessage, String> implements InterestMessageDao {
}
