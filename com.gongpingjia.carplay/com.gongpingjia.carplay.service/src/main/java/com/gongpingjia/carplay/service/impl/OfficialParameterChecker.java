package com.gongpingjia.carplay.service.impl;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.dao.user.UserDao;
import com.gongpingjia.carplay.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 123 on 2015/10/10.
 */
@Service
public class OfficialParameterChecker extends ParameterChecker {

    private static final Logger LOG = LoggerFactory.getLogger(OfficialParameterChecker.class);

    @Autowired
    private UserDao userDao;

    /**
     * 检查用户是否为管理员，官方操作只能为管理员
     *
     * @param userId
     * @throws ApiException
     */
    public void checkAdministrator(String userId) throws ApiException {
        User user = userDao.findById(userId);
        if (user == null) {
            LOG.warn("User is not exist, userId:{}", userId);
            throw new ApiException("用户不存在");
        }
        //只有管理员才能查看
        if (!Constants.UserCatalog.ADMIN.equals(user.getRole())) {
            LOG.warn("Query user infomation is not administrator");
            throw new ApiException("操作用户不是管理员");
        }
    }

    /**
     * @param userId
     * @param toke
     * @throws ApiException
     */
    public void checkAdminUserInfo(String userId, String toke) throws ApiException {
        checkUserInfo(userId, toke);

        checkAdministrator(userId);
    }
}
