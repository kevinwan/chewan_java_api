package com.gongpingjia.carplay.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * 常量类
 * 
 * @author licheng
 *
 */
public class Constants {

	/**
	 * HTTP请求响应200
	 */
	public static final int HTTP_STATUS_OK = 200;

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
		 * 用户图像上传的Key值
		 */
		public static final String USER_KEY = "asset/user/{0}/avatar.jpg";

		/**
		 * 车主认证图像上传Key值
		 */
		public static final String LICENSE_KEY = "asset/user/{0}/license.jpg";

		/**
		 * 活动上传图片Key值
		 */
		public static final String COVER_KEY = "asset/activity/cover/{0}/cover.jpg";

		/**
		 * 个人相册图片Key值
		 */
		public static final String USER_ALBUM_KEY = "asset/user/{0}/album/{1}.jpg";

		/**
		 * 用户反馈图片Key值
		 */
		public static final String FEEDBACK_KEY = "asset/feedback/{0}.jpg";
	}

	/**
	 * 车主认证申请状态
	 * 
	 * @author Administrator
	 *
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
	 *
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
}
