package com.gongpingjia.carplay.dao;

import com.gongpingjia.carplay.po.AlbumPhoto;

public interface AlbumPhotoDao {
    int deleteByPrimaryKey(String id);

    int insert(AlbumPhoto record);


    AlbumPhoto selectByPrimaryKey(String id);


    int updateByPrimaryKey(AlbumPhoto record);
}