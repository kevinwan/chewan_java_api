package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.EmchatAccountDao;
import com.gongpingjia.carplay.po.EmchatAccount;

@Service
public class EmchatAccountDaoImpl implements EmchatAccountDao {

	@Override
	public int deleteByPrimaryKey(String userid) {
		return DASUtil.delete(EmchatAccount.class.getName(), "deleteByPrimaryKey", userid);
	}

	@Override
	public int insert(EmchatAccount record) {
		return DASUtil.save(EmchatAccount.class.getName(), "insert", record);
	}

	@Override
	public EmchatAccount selectByPrimaryKey(String userid) {
		return DASUtil.selectOne(EmchatAccount.class.getName(), "selectByPrimaryKey", userid);
	}

	@Override
	public int updateByPrimaryKey(EmchatAccount record) {
		return DASUtil.update(EmchatAccount.class.getName(), "updateByPrimaryKey", record);
	}

}
