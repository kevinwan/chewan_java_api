package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.TokenVerificationDao;
import com.gongpingjia.carplay.po.TokenVerification;

@Service
public class TokenVerificationDaoImpl implements TokenVerificationDao {

	@Override
	public int deleteByPrimaryKey(String userid) {
		return DASUtil.delete(TokenVerification.class.getName(), "deleteByPrimaryKey", userid);
	}

	@Override
	public int insert(TokenVerification record) {
		return DASUtil.save(TokenVerification.class.getName(), "insert", record);
	}

	@Override
	public TokenVerification selectByPrimaryKey(String userid) {
		return DASUtil.selectOne(TokenVerification.class.getName(), "selectByPrimaryKey", userid);
	}

	@Override
	public int updateByPrimaryKey(TokenVerification record) {
		return DASUtil.update(TokenVerification.class.getName(), "updateByPrimaryKey", record);
	}

}
