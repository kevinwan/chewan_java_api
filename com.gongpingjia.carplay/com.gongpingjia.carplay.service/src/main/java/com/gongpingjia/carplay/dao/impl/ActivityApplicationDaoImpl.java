package com.gongpingjia.carplay.dao.impl;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityApplicationDao;
import com.gongpingjia.carplay.po.ActivityApplication;

public class ActivityApplicationDaoImpl implements ActivityApplicationDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete("deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ActivityApplication record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActivityApplication selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKey(ActivityApplication record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
