package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.AuthenticationChangeHistoryDao;
import com.gongpingjia.carplay.po.AuthenticationApplication;
import com.gongpingjia.carplay.po.AuthenticationChangeHistory;

@Service
public class AuthenticationChangeHistoryDaoImpl implements
		AuthenticationChangeHistoryDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(AuthenticationChangeHistory.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(AuthenticationChangeHistory record) {
		return DASUtil.save(AuthenticationChangeHistory.class.getName(), "insert", record);
	}

	@Override
	public int insertSelective(AuthenticationChangeHistory record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AuthenticationChangeHistory selectByPrimaryKey(String id) {
		return DASUtil.selectOne(AuthenticationChangeHistory.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKeySelective(AuthenticationChangeHistory record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AuthenticationChangeHistory record) {
		return DASUtil.update(AuthenticationApplication.class.getName(), "updateByPrimaryKey", record);
	}

}
