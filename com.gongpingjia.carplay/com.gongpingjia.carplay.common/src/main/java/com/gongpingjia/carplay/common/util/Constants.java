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
     * 车主认证申请状态
     *
     * @author Administrator
     */
    public interface ApplyAuthenticationStatus {

        /**
         * 待处理状态
         */
        public static final String STATUS_PENDING_PROCESSED = "待处理";

        /**
         * 已同意状态
         */
        public static final String STATUS_APPROVED = "已同意";

        /**
         * 已拒绝状态
         */
        public static final String STATUS_DECLINED = "已拒绝";

    }

    public interface ActivityKey {
        /**
         * 获取热点活动的Key值
         */
        public static final String HOTTEST = "hot";

        /**
         * 获取附近的活动的Key值
         */
        public static final String NEARBY = "nearby";

        /**
         * 获取最新的活动的Key值
         */
        public static final String LATEST = "latest";

        /**
         * 活动列表Key值集合
         */
        public static final List<String> KEY_LIST = Arrays.asList(HOTTEST, LATEST, NEARBY);
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

        public static final String COMMON = "普通活动";

        public static final String OFFICIAL = "官方活动";
    }

    public interface ActivityType {

        public static final String DINE = "吃饭";

        public static final String SING = "唱歌";

        public static final String FILM = "电影";

        public static final String TRAVEL = "旅行";

        public static final String SPORT = "运动";

        public static final String CAR_POLL = "拼车";

        public static final String PILOT = "代驾";
    }

    public interface UserCatalog {

        public static final String COMMON = "普通用户";

        public static final String OFFICIAL = "官方用户";
    }

    public interface Charset {

        /**
         * UTF-8编码
         */
        public static final String UTF8 = "UTF-8";

        /**
         * GBK编码
         */
        public static final String GBK = "GBK";

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
     * 约会状态
     */
    public interface AppointmentStatus {

        String ACCEPT = "应邀";

        String REJECT = "拒绝";

        String APPLYING = "邀请中";
    }

    public interface Product{
        public static final String DEFAULT_NAME = "android";
    }
}
