package com.gongpingjia.carplay.dao.impl.activity;

import com.gongpingjia.carplay.dao.activity.ActivityDao;
import com.gongpingjia.carplay.dao.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.activity.Activity;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("activityDao")
public class ActivityDaoImpl extends BaseDaoImpl<Activity,String> implements ActivityDao {
}
