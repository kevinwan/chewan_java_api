package com.gongpingjia.carplay.dao.common.impl;

import com.gongpingjia.carplay.dao.common.FeedBackDao;
import com.gongpingjia.carplay.entity.common.FeedBack;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("feedBackDao")
public class FeedBackDaoImpl extends BaseDaoImpl<FeedBack,String> implements FeedBackDao {
}
