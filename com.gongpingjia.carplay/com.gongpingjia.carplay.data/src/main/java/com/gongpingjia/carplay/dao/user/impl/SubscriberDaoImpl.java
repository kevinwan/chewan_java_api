package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.entity.user.Subscriber;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("subscriberDao")
public class SubscriberDaoImpl extends BaseDaoImpl<Subscriber,String> implements SubscriberDao {
}
