package com.gongpingjia.carplay.dao;

import java.util.List;

import com.gongpingjia.carplay.po.AlbumPhoto;

public interface AlbumPhotoDao {
	int deleteByPrimaryKey(String id);

	int insert(AlbumPhoto record);

	AlbumPhoto selectByPrimaryKey(String id);

	int updateByPrimaryKey(AlbumPhoto record);

	List<AlbumPhoto> selectListByAlbumid(String albumId);
	
	Integer selectPhotosCoutByAlbumid(String albumId);
	
	List<AlbumPhoto> selectAlbumPhotoUrl(String userId);
}