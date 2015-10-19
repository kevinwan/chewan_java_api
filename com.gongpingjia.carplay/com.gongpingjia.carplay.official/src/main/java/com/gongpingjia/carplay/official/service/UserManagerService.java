package com.gongpingjia.carplay.official.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import net.sf.json.JSONObject;

/**
 * Created by licheng on 2015/10/19.
 */
public interface UserManagerService {

    /**
     * 按照条件查询用户信息列表
     *
     * @return
     */
    ResponseDo listUsers(String phone, String nickname, String licenseAuthStatus, String photoAuthStatus,
                         Long start, Long end, Integer limit, Integer ignore);


    /**
     * 获取用户详细信息
     *
     * @param userId 用户Id
     * @return 返回用户详细信息
     */
    ResponseDo viewUserDetail(String userId) throws ApiException;

    /**
     * 更新用户详细信息
     *
     * @param userId     被修改的用户Id
     * @param jsonObject 参数信息
     * @return 更新结果
     */
    ResponseDo updateUserDetail(String userId, JSONObject jsonObject) throws ApiException;
}
