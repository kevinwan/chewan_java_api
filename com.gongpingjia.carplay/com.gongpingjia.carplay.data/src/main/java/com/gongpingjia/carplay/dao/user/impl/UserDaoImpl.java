package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.common.PhotoDao;
import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.common.Photo;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User, String> implements UserDao {

    @Autowired
    private PhotoDao photoDao;

    @Override
    public String getCover(String userId) {
        Photo photo = photoDao.findOne(Query.query(Criteria.where("userId").is(userId).and("type").is(Constants.PhotoType.USER_ALBUM))
                .with(new Sort(new Sort.Order(Sort.Direction.DESC, "uploadTime"))));
        if (photo != null) {
            return CommonUtil.getThirdPhotoServer() + photo.getKey();
        }

        User user = findById(userId);
        return CommonUtil.getLocalPhotoServer() + user.getAvatar();
    }
}
