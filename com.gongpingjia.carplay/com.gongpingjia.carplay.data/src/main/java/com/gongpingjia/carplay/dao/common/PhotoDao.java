package com.gongpingjia.carplay.dao.common;

import com.gongpingjia.carplay.entity.common.Photo;

import java.util.List;

/**
 * Created by 123 on 2015/11/17.
 */
public interface PhotoDao extends BaseDao<Photo, String> {

    /**
     * 根据用户Id获取用户的相册信息
     *
     * @param userId
     * @return
     */
    List<Photo> getUserAlbum(String userId);

    /**
     * 获取用户相册的照片的数量
     *
     * @param userId
     * @return
     */
    long getUserAlbumCount(String userId);


    /**
     * 删除用户相册中的照片
     *
     * @param userId
     * @param photoIds
     */
    void deleteUserPhotos(String userId, List<String> photoIds);

    /**
     * 获取用户相册中的最近的一张照片
     * @param userId  用户Id
     * @return
     */
    Photo getLatestAlbumPhoto(String userId);
}
