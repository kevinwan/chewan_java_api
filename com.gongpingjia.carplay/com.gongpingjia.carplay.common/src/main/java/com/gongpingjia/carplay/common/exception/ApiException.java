package com.gongpingjia.carplay.common.exception;

/**
 * 
 * 异常统一封装，识别API业务异常
 * 
 * @author licheng
 *
 */
public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ApiException(Throwable throwable) {
		super(throwable);
	}

}
