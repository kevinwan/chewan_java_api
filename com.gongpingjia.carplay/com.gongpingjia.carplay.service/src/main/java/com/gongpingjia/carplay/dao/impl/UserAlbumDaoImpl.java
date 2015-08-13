package com.gongpingjia.carplay.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.UserAlbumDao;
import com.gongpingjia.carplay.po.UserAlbum;

@Service
public class UserAlbumDaoImpl implements UserAlbumDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(UserAlbum.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(UserAlbum record) {
		return DASUtil.save(UserAlbum.class.getName(), "insert", record);
	}

	@Override
	public UserAlbum selectByPrimaryKey(String id) {
		return DASUtil.selectOne(UserAlbum.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(UserAlbum record) {
		return DASUtil.update(UserAlbum.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<UserAlbum> selectListByUserId(String userId) {
		return DASUtil.selectList(UserAlbum.class.getName(), "selectListByUserId", userId);
	}

}
