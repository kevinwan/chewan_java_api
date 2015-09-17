package com.gongpingjia.carplay.po;

public class UserLocation {
	private String userid;

	private String deviceToken;

	private Double longitude;

	private Double latitude;

	private Long refreshtime;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Long getRefreshtime() {
		return refreshtime;
	}

	public void setRefreshtime(Long refreshtime) {
		this.refreshtime = refreshtime;
	}

}