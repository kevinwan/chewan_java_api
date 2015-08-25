package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.EmchatTokenDao;
import com.gongpingjia.carplay.po.EmchatToken;

@Service
public class EmchatTokenDaoImpl implements EmchatTokenDao {

	@Override
	public int deleteByPrimaryKey(String application) {
		return DASUtil.delete(EmchatToken.class.getName(), "deleteByPrimaryKey", application);
	}

	@Override
	public int insert(EmchatToken record) {
		return DASUtil.save(EmchatToken.class.getName(), "insert", record);
	}

	@Override
	public EmchatToken selectByPrimaryKey(String application) {
		return DASUtil.selectOne(EmchatToken.class.getName(), "selectByPrimaryKey", application);
	}

	@Override
	public int updateByPrimaryKey(EmchatToken record) {
		return DASUtil.update(EmchatToken.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public EmchatToken selectFirstOne() {
		return DASUtil.selectOne(EmchatToken.class.getName(), "selectFirstOne");
	}

}
