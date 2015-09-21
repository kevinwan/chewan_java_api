package com.gongpingjia.carplay.dao.impl.common;

import com.gongpingjia.carplay.dao.common.FeedBackDao;
import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.common.FeedBack;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("feedBackDao")
public class FeedBackDaoImpl extends BaseDaoImpl<FeedBack,String> implements FeedBackDao {
}
