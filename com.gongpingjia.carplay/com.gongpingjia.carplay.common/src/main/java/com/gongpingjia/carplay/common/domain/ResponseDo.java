package com.gongpingjia.carplay.common.domain;

import org.springframework.util.StringUtils;

/**
 * 
 * @author licheng
 *
 */
public class ResponseDo {

	private int result;

	private Object data;

	private String errmsg;

	private ResponseDo() {

	}

	public static ResponseDo buildSuccessResponse(Object data) {
		ResponseDo response = new ResponseDo();
		response.result = 0;
		response.errmsg = "";

		if (data == null) {
			response.data = "";
		} else {
			response.data = data;
		}

		return response;
	}

	public static ResponseDo buildSuccessResponse(String data) {
		ResponseDo response = new ResponseDo();
		response.result = 0;
		response.errmsg = "";
		response.data = StringUtils.isEmpty(data) ? "" : data;

		return response;
	}
	
	public static ResponseDo buildSuccessResponse() {
		return buildSuccessResponse("");
	}

	public static ResponseDo buildFailureResponse(String errmsg) {
		ResponseDo response = new ResponseDo();
		response.result = 1;
		response.errmsg = errmsg;
		response.data = "";
		return response;
	}

	/**
	 * 该方法仅针对data为JSON字符串的场景，可以直接生成JSON字符串返回
	 * 
	 * @return JSON字符串
	 */
	public String toJsonString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"result\":").append(this.result).append(",\"data\":").append(this.data.toString())
				.append(",\"errmsg\":\"").append(this.errmsg).append("\"");
		return builder.toString();
	}

	public int getResult() {
		return result;
	}

	public Object getData() {
		return data;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public boolean isSuccess() {
		return result == 0;
	}

	public boolean isFailure() {
		return result != 0;
	}
}
