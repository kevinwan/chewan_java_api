package com.gongpingjia.carplay.common.chat;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ChatThirdPartyService {
	/**
	 * 获取授权用户Token信息
	 * 
	 * @return 返回用户Token对象
	 */
	JSONObject getApplicationToken();

	/**
	 * 注册聊天用户
	 * 
	 * @param token
	 *            授权用户token
	 * @param userList
	 *            用户username， password键值对列表
	 * @return 返回注册结果信息
	 */
	JSONObject registerChatUser(String token, List<Map<String, String>> userList);

	/**
	 * 创建聊天组
	 * 
	 * @param token
	 *            授权用户token
	 * @param groupName
	 *            用户群名称
	 * @param description
	 *            群描述信息
	 * @param owner
	 *            群主ID
	 * @param members
	 *            群成员ID列表
	 * @return 返回创建结果信息
	 */
	JSONObject createChatGroup(String token, String groupName, String description, String owner, List<String> members);
}
