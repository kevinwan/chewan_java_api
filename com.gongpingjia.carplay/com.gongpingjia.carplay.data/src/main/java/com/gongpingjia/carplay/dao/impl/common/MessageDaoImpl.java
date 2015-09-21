package com.gongpingjia.carplay.dao.impl.common;

import com.gongpingjia.carplay.dao.common.MessageDao;
import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.data.common.Message;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("messageDao")
public class MessageDaoImpl extends BaseDaoImpl<Message,String> implements MessageDao {
}
