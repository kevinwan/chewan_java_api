package com.gongpingjia.carplay.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.util.DASUtil;
import com.gongpingjia.carplay.dao.AlbumPhotoDao;
import com.gongpingjia.carplay.po.AlbumPhoto;

@Service
public class AlbumPhotoDaoImpl implements AlbumPhotoDao {

	@Override
	public int deleteByPrimaryKey(String id) {
		return DASUtil.delete(AlbumPhoto.class.getName(), "deleteByPrimaryKey", id);
	}

	@Override
	public int insert(AlbumPhoto record) {
		return DASUtil.save(AlbumPhoto.class.getName(), "insert", record);
	}

	@Override
	public AlbumPhoto selectByPrimaryKey(String id) {
		return DASUtil.selectOne(AlbumPhoto.class.getName(), "selectByPrimaryKey", id);
	}

	@Override
	public int updateByPrimaryKey(AlbumPhoto record) {
		return DASUtil.update(AlbumPhoto.class.getName(), "updateByPrimaryKey", record);
	}

	@Override
	public List<AlbumPhoto> selectListByAlbumid(String albumId) {
		return DASUtil.selectList(AlbumPhoto.class.getName(), "selectListByAlbumid", albumId);
	}

	@Override
	public Integer selectPhotosCoutByAlbumid(String albumId) {
		return DASUtil.selectOne(AlbumPhoto.class.getName(), "selectPhotosCoutByAlbumid", albumId);
	}

	@Override
	public List<AlbumPhoto> selectAlbumPhotoUrl(String userId) {
		return DASUtil.selectList(AlbumPhoto.class.getName(), "selectAlbumPhotoUrl", userId);
	}

}
