package com.gongpingjia.carplay.dao.common.impl;

import com.gongpingjia.carplay.dao.common.MessageDao;
import com.gongpingjia.carplay.entity.common.Message;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("messageDao")
public class MessageDaoImpl extends BaseDaoImpl<Message,String> implements MessageDao {
}
