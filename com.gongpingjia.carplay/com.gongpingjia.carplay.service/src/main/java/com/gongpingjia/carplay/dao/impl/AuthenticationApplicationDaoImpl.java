package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.AuthenticationApplicationDao;
import com.gongpingjia.carplay.po.AuthenticationApplication;

@Service
public class AuthenticationApplicationDaoImpl implements AuthenticationApplicationDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(AuthenticationApplication.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(AuthenticationApplication record) {
		return DASUtil.save(AuthenticationApplication.class.getName(), "insert", record);
	}

	@Override
	public AuthenticationApplication selectByPrimaryKey(String id) {
		return DASUtil.selectOne(AuthenticationApplication.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(AuthenticationApplication record) {
		return DASUtil.update(AuthenticationApplication.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<AuthenticationApplication> selectByParam(Map<String, Object> param) {
		return DASUtil.selectList(AuthenticationApplication.class.getName(), "selectByParam", param);
	}

	@Override
	public List<Map<String, Object>> selectCarModelbyId(Map<String, Object> param) {
		return DASUtil.selectList(AuthenticationApplication.class.getName(), "selectCarModelbyId", param);
	}

}
