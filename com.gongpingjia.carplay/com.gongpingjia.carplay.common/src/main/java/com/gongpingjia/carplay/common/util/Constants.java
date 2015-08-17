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

	/**
	 * 用户图像上传的Key值
	 */
	public static final String USER_PHOTO_KEY = "asset/user/{0}/avatar.jpg";

	/**
	 * 车主认证图像上传Key值
	 */
	public static final String LICENSE_PHOTO_KEY = "asset/user/{0}/license.jpg";

	/**
	 * 活动上传图片Key值
	 */
	public static final String COVER_PHOTO_KEY = "asset/activity/cover/{0}/cover.jpg";

	/**
	 * 个人相册图片Key值
	 */
	public static final String USER_ALBUM_PHOTO_KEY = "asset/user/{0}/album/{1}.jpg";

	/**
	 * 用户反馈图片Key值
	 */
	public static final String FEEDBACK_PHOTO_KEY = "asset/feedback/{0}.jpg";
	
	/**
	 * 车主认证申请状态
	 * @author Administrator
	 *
	 */
	public interface ApplyAuthenticationStatus{
		
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

	/**
	 * 活动分享格式
	 */
	public static final String ACTIVITY_SHARE_DATE_FORMAT = "MM月dd日";

	/**
	 * 获取热点活动的Key值
	 */
	public static final String ACTIVITY_KEY_HOTTEST = "hot";

	/**
	 * 获取附近的活动的Key值
	 */
	public static final String ACTIVITY_KEY_NEARBY = "nearby";

	/**
	 * 获取最新的活动的Key值
	 */
	public static final String ACTIVITY_KEY_LATEST = "latest";

	/**
	 * 个人详情（别人/自己）
	 * @author Administrator
	 *
	 */
	public interface UserLabel{
		
		/**
		 * 自己
		 */
		public static final String USER_ME = "我";
		
		/**
		 * 他人
		 */
		public static final String USER_OTHERS = "TA";
		
	}
	/**
	 * 活动列表Key值集合
	 */
	public static final List<String> ACTIVITY_KEY_LIST = Arrays.asList(Constants.ACTIVITY_KEY_HOTTEST,
			Constants.ACTIVITY_KEY_LATEST, Constants.ACTIVITY_KEY_NEARBY);
}
