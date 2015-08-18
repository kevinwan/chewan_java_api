package com.gongpingjia.carplay.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.ActivityMemberDao;
import com.gongpingjia.carplay.po.ActivityMember;
import com.gongpingjia.carplay.po.ActivityMemberKey;

@Service
public class ActivityMemberDaoImpl implements ActivityMemberDao {

	@Override
	public int deleteByPrimaryKey(ActivityMemberKey key) {
		return DASUtil.delete(ActivityMember.class.getName(), "deleteByPrimaryKey", key);
	}

	@Override
	public int insert(ActivityMember record) {
		return DASUtil.save(ActivityMember.class.getName(), "insert", record);
	}

	@Override
	public ActivityMember selectByPrimaryKey(ActivityMemberKey key) {
		return DASUtil.selectOne(ActivityMember.class.getName(), "selectByPrimaryKey", key);
	}

	@Override
	public int updateByPrimaryKey(ActivityMember record) {
		return DASUtil.update(ActivityMember.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public int deleteByParam(ActivityMemberKey key) {
		return DASUtil.delete(ActivityMember.class.getName(), "deleteByParam", key);
	}

	@Override
	public List<Map<String, String>> selectByActivity(Map<String, Object> param) {
		return DASUtil.selectList(ActivityMember.class.getName(), "selectByActivity", param);
	}

	@Override
	public List<Map<String, Object>> selectByUserId(Map<String, Object> param) {
		return DASUtil.selectList(ActivityMember.class.getName(), "selectByUserId", param);
	}

	@Override
	public List<ActivityMember> selectByParam(Map<String, Object> param) {
		return DASUtil.selectList(ActivityMember.class.getName(), "selectByParam", param);
	}

}
