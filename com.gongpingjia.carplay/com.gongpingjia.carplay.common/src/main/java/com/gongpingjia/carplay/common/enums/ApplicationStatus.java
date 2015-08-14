package com.gongpingjia.carplay.common.enums;

/**
 * 活动申请的审核状态
 * 
 * @author licheng
 *
 */
public enum ApplicationStatus {

	/**
	 * 活动"待处理"状态
	 */
	PENDING_PROCESSED("待处理"),

	/**
	 * 活动"已同意"状态
	 */
	APPROVED("已同意"),

	/**
	 * 活动"已拒绝"状态
	 */
	DECLINED("已拒绝");

	private String name;

	private ApplicationStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
