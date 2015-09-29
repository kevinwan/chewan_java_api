package com.gongpingjia.carplay.dao.activity.impl;

import com.gongpingjia.carplay.dao.activity.OfficialActivityDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.entity.activity.OfficialActivity;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/29.
 */
@Repository("officialActivityDao")
public class OfficialActivityDaoImpl extends BaseDaoImpl<OfficialActivity, String> implements OfficialActivityDao {
}
