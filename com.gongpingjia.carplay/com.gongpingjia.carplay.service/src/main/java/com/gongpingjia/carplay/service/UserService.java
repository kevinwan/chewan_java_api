package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return 注册结果
     * @throws ApiException
     */
    ResponseDo register(User user) throws ApiException;


    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 登录结果
     * @throws ApiException
     */
    ResponseDo loginUser(User user) throws ApiException;


    /**
     * 检查注册用户的参数是否正确
     *
     * @param user    用户信息
     * @param request 请求参数
     * @throws ApiException 业务异常
     */
    void checkRegisterParameters(User user, JSONObject request) throws ApiException;

    /**
     * 忘记密码
     *
     * @param user 用户信息
     * @param code 手机验证码
     * @return 业务结果
     * @throws ApiException
     */
    ResponseDo forgetPassword(User user, String code) throws ApiException;

    /**
     * 第三方登录
     *
     * @return 返回登录结果
     * @throws ApiException
     */
    public ResponseDo snsLogin(User user) throws ApiException;

    /**
     * 查看用户详细信息
     *
     * @param beViewedUser 被查看的用户
     * @param viewUser     查看的用户
     * @param token        会话Token
     * @return 返回查看结果
     */
    ResponseDo getUserInfo(String beViewedUser, String viewUser, String token) throws ApiException;

    /**
     * 获取我的约会信息
     */
    public ResponseDo getAppointment(String userId, String token, String status, Integer limit, Integer ignore) throws ApiException;


    /**
     * 获取相册的查看信息；
     */
    public ResponseDo getViewHistory(String userId, String token, int limit, int ignore) throws ApiException;

    /**
     * 获取车玩官方认证的历史信息
     *
     * @param userId
     * @param token
     * @param limit
     * @param ignore
     * @return
     * @throws ApiException
     */
    public ResponseDo getAuthHistory(String userId, String token, int limit, int ignore) throws ApiException;

    /**
     * 修改用户信息
     */
    ResponseDo alterUserInfo(String userId, String token, JSONObject json) throws ApiException;

    ResponseDo changeLocation(String userId, String token, Landmark landmark) throws ApiException;

    ResponseDo listInterests(String userId, String token, Integer ignore, Integer limit) throws ApiException;

    ResponseDo deleteAlbumPhotos(String userId, String token, JSONObject json) throws ApiException;
}