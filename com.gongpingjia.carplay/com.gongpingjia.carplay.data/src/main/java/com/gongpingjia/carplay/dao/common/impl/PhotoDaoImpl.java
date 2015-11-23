package com.gongpingjia.carplay.dao.common.impl;

import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.common.PhotoDao;
import com.gongpingjia.carplay.entity.common.Photo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by licheng on 2015/11/17.
 */
@Repository("photoDao")
public class PhotoDaoImpl extends BaseDaoImpl<Photo, String> implements PhotoDao {
    @Override
    public List<Photo> getUserAlbum(String userId) {
        List<Photo> photos = find(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM)));
        String photoServer = CommonUtil.getThirdPhotoServer();
        for (Photo item : photos) {
            item.setUrl(photoServer + item.getKey());
        }
        return photos;
    }

    @Override
    public long getUserAlbumCount(String userId) {
        return count(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM)));
    }

    @Override
    public void deleteUserPhotos(String userId, List<String> photoIds) {
        delete(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM).and("id").in(photoIds)));
    }

    @Override
    public Photo getLatestAlbumPhoto(String userId) {
        return findOne(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "uploadTime"))));
    }
}
