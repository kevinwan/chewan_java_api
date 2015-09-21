package com.gongpingjia.carplay.dao.impl.user;

import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.SubscriberDao;
import com.gongpingjia.carplay.data.user.Subscriber;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("subscriberDao")
public class SubscriberDaoImpl extends BaseDaoImpl<Subscriber,String> implements SubscriberDao {
}
