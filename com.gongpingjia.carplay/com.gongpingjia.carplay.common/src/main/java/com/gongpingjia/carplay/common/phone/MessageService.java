package com.gongpingjia.carplay.common.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author licheng
 *
 */
public class MessageService {

	private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

	/**
	 * 发送手机验证码消息
	 * 
	 * @param phone
	 *            手机消息
	 * @param verifyCode
	 *            验证码消息
	 * @param sendTimes
	 *            发送次数
	 * @return 返回发送结果，发送成功返回true，发送失败或者报错返回false
	 */
	public static boolean sendMessage(String phone, String verifyCode, Integer sendTimes) {
		boolean success = true;

		// 结合配置的次数信息，循环发送，有任何一次成功就退出
		// 先采用天翼，后采用移动QXT, 即基数次采用天翼，偶数次采用移动
		if (sendTimes % 2 == 1) {
			success = sendByTianyi(phone, verifyCode);
		} else {
			success = sendByYidongQxt(phone, verifyCode);
		}

		return success;
	}

	private static boolean sendByTianyi(String phone, String verifyCode) {
		boolean sendResult = true;

		try {
			SendMessageByTianyi.sendMessage(phone, verifyCode);
		} catch (Exception e) {
			LOG.warn("Send message by Tianyi failure");
			LOG.error(e.getMessage(), e);
			sendResult = false;
		}

		return sendResult;
	}

	private static boolean sendByYidongQxt(String phone, String verifyCode) {
		boolean sendResult = true;

		try {
			SendMessageByYidongQxt.sendMessage(phone, verifyCode);
		} catch (Exception e) {
			LOG.warn("Send message by Yidong QXT failure");
			LOG.error(e.getMessage(), e);
			sendResult = false;
		}

		return sendResult;
	}
}
