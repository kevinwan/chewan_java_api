package com.gongpingjia.carplay.common.domain;

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

	public static ResponseDo buildFailureResponse(String errmsg) {
		ResponseDo response = new ResponseDo();
		response.result = 1;
		response.errmsg = errmsg;
		response.data = "";
		return response;
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
