package com.gongpingjia.carplay.common.util;

public class Constants {

	/**
	 * HTTP请求响应200
	 */
	public static final int HTTP_STATUS_OK = 200;
	
	/**
	 * 图片路径
	 * 
	 * @author Administrator
	 *
	 */
	public interface Photo{
		
		/**
		 * 拼接图片路径前段
		 */
		public static final String PHOTO_HEAD = "asset/user/";
		
		/**
		 * 拼接图片路径后端
		 */
		public static final String PHOTO_END = "/avatar.jpg";
		
	}
	
	public interface Logo{
		
		/**
		 * logo根目录
		 */
		public static final String LOGO_ROOT = "http://img.gongpingjia.com/img/logo/";
	}
	

}
