package com.gongpingjia.carplay.dao.user.impl;

import com.gongpingjia.carplay.dao.common.impl.BaseDaoImpl;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.User;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2015/9/21.
 */
@Repository("userDao")
public class UserDaoImpl extends BaseDaoImpl<User,String> implements UserDao {
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

    @Override
    public User findUserByPhone(String phone) {
        return super.findOne(Query.query(Criteria.where("phone").is(phone)));
    }
}
