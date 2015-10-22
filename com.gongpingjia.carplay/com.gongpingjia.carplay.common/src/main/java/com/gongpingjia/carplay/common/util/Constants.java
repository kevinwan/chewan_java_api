package com.gongpingjia.carplay.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * 常量类
 *
 * @author licheng
 */
public class Constants {

    /**
     * HTTP请求响应200
     */
    public static final int HTTP_STATUS_OK = 200;

    /**
     * 标志位
     */
    public interface Flag {

        /**
         * 积极的，正面的， 肯定的，同意的
         */
        int POSITIVE = 1;

        /**
         * 消极的，负面的，否定的，拒绝的
         */
        int NEGATIVE = 0;
    }

    public interface DateFormat {

        /**
         * 发送验证码请求参数Timestamp
         */
        String PHONE_VERIFY_TIMESTAMP = "YYYY-MM-dd HH:mm:ss";

        /**
         * 活动分享格式
         */
        String ACTIVITY_SHARE = "MM月dd日";
    }

    public interface Result {

        String RESULT = "result";

        /**
         * 成功标识字符串
         */
        String SUCCESS = "success";

        /**
         * 失败标识字符串
         */
        String FAILURE = "failure";
    }

    public interface PhotoKey {
        /**
         * 用户头像上传的Key值
         */
        String AVATAR_KEY = "asset/user/{0}/avatar.jpg";

        /**
         * 用户认证图像上传的Key值
         */
        String PHOTO_KEY = "asset/user/{0}/photo.jpg";


        /**
         * 车主认证行驶证图像上传Key值
         */
        String DRIVING_LICENSE_KEY = "asset/user/{0}/drivingLicense.jpg";

        /**
         * 车主认证驾驶证图像上传Key值
         */
        String DRIVER_LICENSE_KEY = "asset/user/{0}/driverLicense.jpg";

        /**
         * 活动上传图片Key值
         */
        String COVER_KEY = "asset/activity/cover/{0}/cover.jpg";

        /**
         * 个人相册图片Key值
         */
        String USER_ALBUM_KEY = "asset/user/{0}/album/{1}.jpg";

        /**
         * 用户反馈图片Key值
         */
        String FEEDBACK_KEY = "asset/feedback/{0}.jpg";
    }

    /**
     * 个人详情（别人/自己）
     *
     * @author Administrator
     */
    public interface UserLabel {

        /**
         * 自己
         */
        public static final String USER_ME = "我";

        /**
         * 他人
         */
        public static final String USER_OTHERS = "TA";

    }

    public interface Channel {
        /**
         * 第三方注册登录渠道--qq
         */
        public static final String QQ = "qq";

        /**
         * 第三方注册登录渠道--微信
         */
        public static final String WECHAT = "wechat";

        /**
         * 第三方注册登录渠道--新浪微博
         */
        public static final String SINA_WEIBO = "sinaWeibo";

        /**
         * 第三方注册登录渠道--QQ，微博，微信
         */
        public static final List<String> CHANNEL_LIST = Arrays.asList(WECHAT, QQ, SINA_WEIBO);
    }

    public interface ActivityCatalog {
        String COMMON = "普通活动";

        String OFFICIAL = "官方活动";
    }

    public interface UserCatalog {

        String COMMON = "普通用户";

        String OFFICIAL = "官方用户";

        String ADMIN = "ADMIN";

        List<String> ROLES = Arrays.asList(COMMON, OFFICIAL, ADMIN);
    }

    public interface Charset {

        /**
         * UTF-8编码
         */
        String UTF8 = "UTF-8";

        /**
         * GBK编码
         */
        String GBK = "GBK";

    }

    /**
     * 认证状态
     */
    public interface AuthStatus {

        String UNAUTHORIZED = "未认证";

        String AUTHORIZING = "认证中";

        String REJECT = "认证未通过";

        String ACCEPT = "认证通过";
    }

    /**
     * 认证的类型
     */
    public interface AuthType {

        String PHOTO_AUTH = "图像认证";

        String LICENSE_AUTH = "车主认证";

        String ID_CARD_AUTH = "身份证认证";
    }

    /**
     * 消息类型
     */
    public interface MessageType {

        //图像认证
        int PHOTO_AUTH_MSG = 1;
        //车主认证
        int LICENSE_AUTH_MSG = 2;
        //身份证认证
        int ID_CARD_AUTH_MSG = 3;
        //约会信息认证
        int APPOINTMENT_EXPIRED_MSG = 4;
    }

    /**
     * 约会状态
     */
    public interface AppointmentStatus {
        //初始状态，没有被邀请
        int INITIAL = 0;
        //应邀申请中
        int APPLYING = 1;
        //已经接受邀请，已应邀
        int ACCEPT = 2;
        //拒绝邀请
        int REJECT = 3;
    }

    public interface Product {
        String DEFAULT_NAME = "android";
    }

    /**
     * 动态部分的服务端环信用户
     */
    public interface EmchatAdmin {
        /**
         * 感兴趣的人
         */
        String INTEREST = "InterestAdmin";

        /**
         * 活动动态
         */
        String ACTIVITY_STATE = "ActivityStateAdmin";

        /**
         * 谁看过我
         */
        String USER_VIEW = "UserViewAdmin";

        /**
         * 我关注的人
         */
        String SUBSCRIBE = "SubscribeAdmin";

        /**
         * 车玩官方
         */
        String OFFICIAL = "OfficialAdmin";
    }

    /**
     * 官方活动人数限制类型
     */
    public interface OfficialActivityLimitType {
        /**
         * 官方活动不设置人数限制
         */
        int NO_LIMIT = 0;
        /**
         * 官方活动设置总人数限制
         */
        int TOTAL_LIMIT = 1;
        /**
         * 官方活动设置不同性别人数限制
         */
        int GENDER_LIMIT = 2;
    }

    /**
     * 用户性别常量
     */
    public interface UserGender {
        String MALE = "男";
        String FEMALE = "女";
    }

    /**
     * 用户活动类型
     */
    public interface ActivityType {
        String EAT = "吃饭";

        String SING = "唱歌";

        String SHOPPING = "购物";

        String NIGHT_CLUB = "夜店";

        String FILM = "看电影";

        String WALK_DOG = "遛狗";

        String COFFEE = "咖啡";

        String SPORT = "运动";

        String DINK = "喝酒";

        String MIDNIGHT_SNACK = "夜宵";

        List<String> TYPE_LIST = Arrays.asList(EAT, SING, SHOPPING, NIGHT_CLUB, FILM, WALK_DOG, COFFEE, SPORT, DINK, MIDNIGHT_SNACK);
    }

    /**
     * 请客类型
     */
    public interface ActivityPayType {

        String AA = "AA制";

        String TREAT_ME = "请我吧";

        String MY_TREAT = "我请客";

        List<String> TYPE_LIST = Arrays.asList(AA, TREAT_ME, MY_TREAT);
    }
}
