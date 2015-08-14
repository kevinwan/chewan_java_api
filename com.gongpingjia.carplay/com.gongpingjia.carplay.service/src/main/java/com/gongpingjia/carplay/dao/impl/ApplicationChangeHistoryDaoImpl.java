package com.gongpingjia.carplay.dao.impl;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ApplicationChangeHistoryDao;
import com.gongpingjia.carplay.po.ApplicationChangeHistory;

@Service
public class ApplicationChangeHistoryDaoImpl implements ApplicationChangeHistoryDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(ApplicationChangeHistory.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(ApplicationChangeHistory record) {
		return DASUtil.save(ApplicationChangeHistory.class.getName(), "insert", record);
	}

	@Override
	public ApplicationChangeHistory selectByPrimaryKey(String id) {
		return DASUtil.selectOne(ApplicationChangeHistory.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(ApplicationChangeHistory record) {
		return DASUtil.update(ApplicationChangeHistory.class.getName(), "updateByPrimaryKey", record);
	}

}
