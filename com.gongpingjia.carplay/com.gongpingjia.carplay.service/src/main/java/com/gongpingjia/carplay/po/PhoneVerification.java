package com.gongpingjia.carplay.po;

public class PhoneVerification {
	private String phone;

	private String code;

	private Long expire;

	private Long modifiedtime;

	private Integer sendtimes;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public Long getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(Long modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public Integer getSendtimes() {
		return sendtimes;
	}

	public void setSendtimes(Integer sendtimes) {
		this.sendtimes = sendtimes;
	}

}