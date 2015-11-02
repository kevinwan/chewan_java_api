package com.gongpingjia.carplay.official.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.user.UserAuthentication;
import net.sf.json.JSONObject;

/**
 * Created by licheng on 2015/9/28.
 * 官方审批服务
 */
public interface OfficialApproveService {

    /**
     * 审批用户的车主认证信息
     *
     * @param userId
     * @param json
     * @return
     */
    public ResponseDo approveUserDrivingAuthentication(String userId, JSONObject json) throws ApiException;

    public ResponseDo getAuthApplicationList(String userId, String type, String status, Long start, Long end, int ignore, int limit, String phone);

    /**
     * 根据application的Id获取Application的信息
     *
     * @param applicationId
     * @return
     */
    public ResponseDo getApplicationInfo(String applicationId) throws ApiException;

    /**
     * 用户userID获取authenticationId信息
     *
     * @param authenticationId
     * @param userId
     * @return
     * @throws ApiException
     */
    ResponseDo getUserAuthenticationInfo(String authenticationId, String userId) throws ApiException;

    /**
     * 修改用户的认证信息
     *
     * @param userId 用户Id
     * @return 返回
     * @throws ApiException
     */
    ResponseDo modifyUserAuthenticationInfo(String userId, JSONObject json) throws ApiException;

    /**
     * 用户头像认证
     *
     * @param userId 审批用户Id
     * @param json   审批结果信息
     * @return 返回审批结果
     */
    ResponseDo approveUserPhotoAuthentication(String userId, JSONObject json) throws ApiException;
}
