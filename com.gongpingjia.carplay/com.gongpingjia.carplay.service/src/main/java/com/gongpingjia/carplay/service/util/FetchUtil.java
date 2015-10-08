package com.gongpingjia.carplay.service.util;

import com.gongpingjia.carplay.entity.user.User;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class FetchUtil {

    public static User getUserFromList(List<User> userList, String userId) {
        for (User user : userList) {
            if (StringUtils.equals(userId, user.getUserId())) {
                return user;
            }
        }
        return null;
    }
}
