package com.gongpingjia.carplay.common.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gongpingjia.carplay.common.util.PropertiesUtil;

public class MessageService {

	private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

	/**
	 * 发送手机验证码消息
	 * 
	 * @param phone
	 *            手机消息
	 * @param verifyCode
	 *            验证码消息
	 * @return 返回发送结果，发送成功返回true，发送失败或者报错返回false
	 */
	public static boolean sendMessage(String phone, String verifyCode) {
		boolean success = true;

		int sendMaxCount = PropertiesUtil.getProperty("message.send.failure.max.times", 3);
		try {
			// 结合配置的次数信息，循环发送，有任何一次成功就退出
			// 先采用天翼，后采用移动QXT
			for (int i = 0; i < sendMaxCount; i++) {
				// 优先采用天翼
				success = sendByTianyi(phone, verifyCode);
				if (success) {
					break;
				}

				// 其次采用移动QXT
				success = sendByYidongQxt(phone, verifyCode);
				if (success) {
					break;
				}

			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			success = false;
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
