package com.gongpingjia.carplay.controller;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.CommonUtil;
import com.gongpingjia.carplay.entity.common.Landmark;
import com.gongpingjia.carplay.entity.user.User;
import com.gongpingjia.carplay.service.ActivityService;
import com.gongpingjia.carplay.service.UserService;
import com.gongpingjia.carplay.service.impl.ParameterChecker;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class UserInfoController {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ParameterChecker parameterChecker;


    /**
     * 注册
     *
     * @param json 参数列表
     * @return 注册结果
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo register(@RequestBody JSONObject json) {

        LOG.debug("register is called, request parameter produce:");
        try {
            // 检查必须参数是否为空
            if (CommonUtil.isEmpty(json, Arrays.asList("nickname", "gender", "birthday", "avatar", "landmark"))) {
                throw new ApiException("输入参数错误");
            }

            if (!json.containsKey("landmark")) {
                LOG.warn("Input parameter landmark is not exist");
                throw new ApiException("输入参数错误");
            }
            JSONObject jsonObject = json.getJSONObject("landmark");
            if (CommonUtil.isEmpty(jsonObject, Arrays.asList("longitude", "latitude"))) {
                throw new ApiException("输入参数错误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

            userService.checkRegisterParameters(user, json);

            return userService.register(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 登录
     *
     * @param user 参数列表
     * @return 登录结果
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo loginUser(@RequestBody User user) {
        LOG.debug("login is called, request parameter produce:");

        try {
            if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPhone())) {
                LOG.warn("Input parameters password or phone is empty");
                throw new ApiException("输入参数有误");
            }

            return userService.loginUser(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 忘记密码
     *
     * @param json 参数列表
     * @return 忘记密码结果
     */
    @RequestMapping(value = "/user/password", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo forgetPassword(@RequestBody JSONObject json) {

        LOG.debug("forgetPassword is called, request parameter produce:");

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("phone", "code", "password"))) {
                throw new ApiException("输入参数有误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

            return userService.forgetPassword(user, json.getString("code"));
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 三方登录
     *
     * @return 返回登录结果
     */
    @RequestMapping(value = "/sns/login", method = RequestMethod.POST, headers = {
            "Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo snsLogin(@RequestBody JSONObject json) {
        LOG.info("snsLogin begin");

        try {
            if (CommonUtil.isEmpty(json, Arrays.asList("uid", "nickname", "avatar", "channel", "password"))) {
                throw new ApiException("输入参数有误");
            }

            User user = (User) JSONObject.toBean(json, User.class);

            return userService.snsLogin(user);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 个人详情，获取个人信息，当beViewUser与viewUser相同，表示查看自己的个人信息，否则为查看他的详情
     *
     * @param beViewedUser 被查看的用户
     * @param viewUser     当前查看的人
     * @param token        当前查看的人的会话Token
     * @return 返回个人详情结果
     */
    @RequestMapping(value = "/user/{beViewedUser}/info", method = RequestMethod.GET)
    public ResponseDo getUserInfo(@PathVariable("beViewedUser") String beViewedUser,
                                  @RequestParam("viewUser") String viewUser, @RequestParam("token") String token) {
        LOG.info("Begin get user information, user:{}", beViewedUser);

        try {
            return userService.getUserInfo(beViewedUser, viewUser, token);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取我的约会信息
     */
    @RequestMapping(value = "/user/{userId}/appointment/list", method = RequestMethod.GET)
    public ResponseDo getAppointments(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                      @RequestParam(value = "status", required = false) Integer[] status,
                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                      @RequestParam(value = "ignore", defaultValue = "0") Integer ignore) {
        LOG.debug("/user/{}/appointment", userId);
        try {
            return userService.getAppointments(userId, token, status, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取用户信息、相册被查看的历史信息
     *
     * @param userId
     * @param token
     * @param limit
     * @param ignore
     * @return
     */
    @RequestMapping(value = "/user/{userId}/view/history")
    public ResponseDo getViewHistory(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam(value = "limit", defaultValue = "10") int limit, @RequestParam(value = "ignore", defaultValue = "0") int ignore) {
        LOG.debug("/user/{}/view/history", userId);
        try {
            return userService.getViewHistory(userId, token, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 查看用户的官方认证的审核结果信息
     *
     * @param userId
     * @param token
     * @param limit
     * @param ignore
     * @return
     */
    @RequestMapping(value = "/user/{userId}/auth/history")
    public ResponseDo getAuthHistory(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                     @RequestParam("limit") int limit, @RequestParam("ignore") int ignore) {

        LOG.debug("/user/{}/auth/history", userId);
        try {
            return userService.getAuthHistory(userId, token, limit, ignore);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 修改用户信息
     *
     * @param userId
     * @param token
     * @param json   修改的属性参数信息
     * @return
     */
    @RequestMapping(value = "/user/{userId}/info", method = RequestMethod.POST)
    public ResponseDo alterUserInfo(@PathVariable("userId") String userId, @RequestParam("token") String token, @RequestBody JSONObject json) {
        LOG.debug("alter user information");

        try {
            return userService.alterUserInfo(userId, token, json);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 变更用户位置信息
     *
     * @param json 请求Body参数信息
     * @return 变更结果信息
     */
    @RequestMapping(value = "/user/{userId}/location", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo changeLocation(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                     @RequestBody JSONObject json) {
        try {
            LOG.debug("Begin change user loaction, request:{}", json);
            if (CommonUtil.isEmpty(json, Arrays.asList("userId", "token", "longitude", "latitude"))) {
                throw new ApiException("输入参数有误");
            }
            Landmark landmark = (Landmark) JSONObject.toBean(json, Landmark.class);

            return userService.changeLocation(userId, token, landmark);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 获取用户的感兴趣的人
     *
     * @param userId 用户Id
     * @param token  会话Token
     * @param ignore 忽略记录数
     * @param limit  查询记录数
     * @return 返回结果信息
     */
    @RequestMapping(value = "/user/{userId}/interest/list", method = RequestMethod.GET)
    public ResponseDo listInterests(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                    @RequestParam(value = "ignore", defaultValue = "0") Integer ignore,
                                    @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        try {
            LOG.debug("Begin get user listInterests,user:{}", userId);
            return userService.listInterests(userId, token, ignore, limit);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 删除用户相册中的照片
     *
     * @param userId 用户Id
     * @param token  用户会话Token
     * @param json   请求JSON对象
     * @return 返回处理结果
     */
    @RequestMapping(value = "/user/{userId}/album/photos", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo deleteAlbumPhotos(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                        @RequestBody JSONObject json) {
        try {
            LOG.debug("Begin delete user's album photos , user : {}", userId);
            return userService.deleteAlbumPhotos(userId, token, json);
        } catch (Exception e) {
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取用户的审批历史信息
     *
     * @param userId 用户Id
     * @param token  用户会话token
     * @return 返回查询的结果信息
     */
    @RequestMapping(value = "/user/{userId}/authentication/history", method = RequestMethod.GET)
    public ResponseDo getAuthenticationHistory(@PathVariable("userId") String userId, @RequestParam("token") String token) {

        try {
            LOG.debug("Begin getAuthenticationHistory ,user : {}", userId);
            return userService.getAuthenticationHistory(userId, token);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 用户绑定手机号，通过三方登录，参加活动时，需要绑定手机号码
     *
     * @param userId 用户Id
     * @param token  用户会话Token
     * @param json   请求Body
     * @return 返回处理结果
     */
    @RequestMapping(value = "/user/{userId}/binding", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo bindingPhone(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                   @RequestBody JSONObject json) {
        try {
            LOG.debug("Begin user binding phone,user:{}", userId);
            if (CommonUtil.isEmpty(json, Arrays.asList("phone", "code"))) {
                throw new ApiException("输入参数有误");
            }
            String phone = json.getString("phone");
            String code = json.getString("code");

            return userService.bindingPhone(userId, token, phone, code);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }

    /**
     * 记录当前用户上传图片的数量
     *
     * @param userId 用户Id
     * @param token  用户会话Token
     * @param json   请求Body
     * @return 返回处理结果
     */
    @RequestMapping(value = "/user/{userId}/photoCount", method = RequestMethod.POST,
            headers = {"Accept=application/json; charset=UTF-8", "Content-Type=application/json"})
    public ResponseDo recordUploadPhotoCount(@PathVariable("userId") String userId, @RequestParam("token") String token,
                                             @RequestBody JSONObject json) {
        LOG.info("Begin record user upload photo count, userId:{}, requestBody:{}", userId, json);
        try {
            if (CommonUtil.isEmpty(json, "count")) {
                throw new ApiException("输入参数有误");
            }

            Integer count = json.getInt("count");

            return userService.recordUploadPhotoCount(userId, token, count);
        } catch (ApiException e) {
            LOG.warn(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }


    /**
     * 获取他的活动详情
     *
     * @param viewUserId
     * @param userId
     * @param token
     * @param limit
     * @param ignore
     * @return
     */
    @RequestMapping(value = "/user/{viewUserId}/activity/list", method = RequestMethod.GET)
    public ResponseDo getUserActivityList(@PathVariable("viewUserId") String viewUserId, @RequestParam("userId") String userId, @RequestParam("token") String token,
                                          @RequestParam("limit") Integer limit, @RequestParam("ignore") Integer ignore) {
        LOG.info("Begin view user activity viewUserId:{} userId:{}", viewUserId, userId);
        try {
            parameterChecker.checkUserInfo(userId, token);

            return userService.getUserActivityList(viewUserId, userId, limit, ignore);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return ResponseDo.buildFailureResponse(e.getMessage());
        }

    }

    /**
     * @param userId
     * @param token
     * @param emchatName
     * @return
     */
    @RequestMapping(value = "/user/emchatInfo", method = RequestMethod.GET)
    public ResponseDo getUserChatInfo(@RequestParam("userId") String userId, @RequestParam("token") String token,
                                      @RequestParam("emchatName") String emchatName) {

        LOG.info("Begin query user chat info by userId:{}", userId);

        try {
            parameterChecker.checkUserInfo(userId, token);

            return userService.getUserEmchatInfo(emchatName);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
            return ResponseDo.buildFailureResponse(e.getMessage());
        }
    }
}