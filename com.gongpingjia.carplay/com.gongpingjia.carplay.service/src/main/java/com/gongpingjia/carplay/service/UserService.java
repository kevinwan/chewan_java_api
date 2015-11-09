package com.gongpingjia.carplay.service;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户信息
     * @param json
     * @return 注册结果
     * @throws ApiException
     */
    ResponseDo register(User user, JSONObject json) throws ApiException;


    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 登录结果
     * @throws ApiException
     */
    ResponseDo loginUser(User user) throws ApiException;


    /**
     * 管理员用户登录
     *
     * @param user
     * @return
     * @throws ApiException
     */
    ResponseDo loginAdminUser(User user) throws ApiException;

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
    public ResponseDo snsLogin(String uid, String nickname, String avatar, String channel, String password) throws ApiException;

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
    ResponseDo getMyActivityAppointments(String userId, String token, Integer[] status, Integer limit, Integer ignore) throws ApiException;


    /**
     * 获取相册的查看信息；
     */
    ResponseDo getViewHistory(String userId, String token, int limit, int ignore) throws ApiException;

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

    /**
     * 三方登录绑定手机号
     *
     * @throws ApiException
     */
    ResponseDo bindingPhone(String uid, String channel, String snsPassword, String phone, String code) throws ApiException;

    /**
     * 检查手机号是否已经注册
     * @param phone
     * @return
     */
    ResponseDo checkPhoneAlreadyRegister(String phone) throws ApiException;

    ResponseDo getAuthenticationHistory(String userId, String token) throws ApiException;

    /**
     * 记录用户本次上传了照片的数量
     *
     * @param userId
     * @param token
     * @param count  上传的照片的数量  @return 返回处理结果
     */
    ResponseDo recordUploadPhotoCount(String userId, String token, Integer count) throws ApiException;


    /**
     * 获取某个用户的所有的活动
     *
     * @param viewUserId
     * @return
     * @throws ApiException
     */
    ResponseDo getUserActivityList(String viewUserId, String userId, int limit, int ignore) throws ApiException;

    /**
     * 根据用户的环信ID，获取用户的聊天信息
     *
     * @param emchatName 环信ID
     * @return 返回用户的聊天必须信息
     */
    ResponseDo getUserEmchatInfo(String emchatName) throws ApiException;

    /**
     * 用户修改密码接口
     *
     * @param userId
     * @param old
     * @param aNew
     * @return
     */
    ResponseDo changePassword(String userId, String old, String aNew) throws ApiException;

    /**
     * 获取用户动态--活动动态信息
     *
     * @param userId
     * @param token
     * @param status
     * @param limit
     * @param ignore
     * @return
     */
    ResponseDo getDynamicActivityAppointments(String userId, String token, Integer[] status, Integer limit, Integer ignore) throws ApiException;
}