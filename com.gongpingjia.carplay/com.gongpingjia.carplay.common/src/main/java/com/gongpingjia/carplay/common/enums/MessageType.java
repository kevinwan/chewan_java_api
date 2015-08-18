package com.gongpingjia.carplay.common.enums;

/**
 * 消息类型枚举类
 * 
 * @author licheng
 *
 */
public enum MessageType {

	/**
	 * 官方消息
	 */
	OFFICIAL("官方消息"),
	/**
	 * 活动申请处理
	 */
	APPLICATION_PROCESS("活动申请处理"),
	/**
	 * 活动申请结果
	 */
	APPLICATION_RESULT("活动申请结果"),
	/**
	 * 活动邀请
	 */
	INVITATION("活动邀请"),
	/**
	 * 留言
	 */
	COMMENT("留言"),

	/**
	 * 车主认证
	 */
	AUTHENTICATION("车主认证");

	private String name;

	private MessageType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
