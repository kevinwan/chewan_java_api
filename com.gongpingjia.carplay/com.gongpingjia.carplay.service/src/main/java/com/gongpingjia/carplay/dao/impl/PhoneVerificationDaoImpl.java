package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.PhoneVerificationDao;
import com.gongpingjia.carplay.po.PhoneVerification;

@Service
public class PhoneVerificationDaoImpl implements PhoneVerificationDao {

	@Override
	public int deleteByPrimaryKey(String phone) {
		return DASUtil.delete(PhoneVerification.class.getName(), "deleteByPrimaryKey", phone);
	}

	@Override
	public int insert(PhoneVerification phone) {
		return DASUtil.save(PhoneVerification.class.getName(), "insert", phone);
	}

	@Override
	public PhoneVerification selectByPrimaryKey(String phone) {
		return DASUtil.selectOne(PhoneVerification.class.getName(), "selectByPrimaryKey", phone);
	}

	@Override
	public int updateByPrimaryKey(PhoneVerification phone) {
		return DASUtil.update(PhoneVerification.class.getName(), "updateByPrimaryKey", phone);
	}

}
