package com.gongpingjia.carplay.dao;

import java.util.List;

import com.gongpingjia.carplay.po.UserAlbum;

public interface UserAlbumDao {
	int deleteByPrimaryKey(String id);

	int insert(UserAlbum record);

	UserAlbum selectByPrimaryKey(String id);

	int updateByPrimaryKey(UserAlbum record);

	List<UserAlbum> selectListByUserId(String userId);
}