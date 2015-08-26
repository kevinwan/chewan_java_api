package com.gongpingjia.carplay.common.chat;

import java.util.List;
import java.util.Map;

import com.gongpingjia.carplay.common.exception.ApiException;

import net.sf.json.JSONObject;

public interface ChatThirdPartyService {
	/**
	 * 获取授权用户Token信息
	 * 
	 * @return 返回用户Token对象
	 * @throws ApiException 
	 */
	JSONObject getApplicationToken() throws ApiException;

	/**
	 * 注册聊天用户
	 * 
	 * @param token
	 *            授权用户token
	 * @param userMap
	 *            用户username， password键值对列表
	 * @return 返回注册结果信息
	 * @throws ApiException 
	 */
	JSONObject registerChatUser(String token, Map<String, String> userMap) throws ApiException;

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
	 * @throws ApiException 
	 */
	JSONObject createChatGroup(String token, String groupName, String description, String owner, List<String> members) throws ApiException;

	/**
	 * 修改聊天群组信息
	 * 
	 * @param token
	 *            授权token
	 * @param groupId
	 *            群组ID
	 * @param groupName
	 *            群组名称
	 * @param description
	 *            描述信息
	 * @return 返回修改结果
	 * @throws ApiException 
	 */
	JSONObject modifyChatGroup(String token, String groupId, String groupName, String description) throws ApiException;

	/**
	 * 删除聊天群组信息
	 * 
	 * @param token
	 *            授权token
	 * @param groupId
	 *            群组ID
	 * @return 返回删除结果
	 * @throws ApiException 
	 */
	JSONObject deleteChatGroup(String token, String groupId) throws ApiException;

	/**
	 * 向群组中添加成员
	 * 
	 * @param token
	 *            授权Token
	 * @param groupId
	 *            群组ID
	 * @param username
	 *            用户名称（对应聊天服务器中的username）
	 * @return 返回添加结果信息
	 * @throws ApiException 
	 */
	JSONObject addUserToChatGroup(String token, String groupId, String username) throws ApiException;

	/**
	 * 从群组中删除成员
	 * 
	 * @param token
	 *            授权Token
	 * @param groupId
	 *            群组ID
	 * @param username
	 *            用户名称（对应聊天服务器中的username）
	 * @return 返回删除结果信息
	 * @throws ApiException 
	 */
	JSONObject deleteUserFromChatGroup(String token, String groupId, String username) throws ApiException;

	/**
	 * 向用户聊天群发送text类型消息
	 * 
	 * @param token
	 *            授权Token
	 * @param username
	 *            消息发送人员
	 * @param groupId
	 *            用户组ID
	 * @param textMessage
	 *            Text消息内容
	 * @return 返回发送结果
	 * @throws ApiException 
	 */
	JSONObject sendChatGroupTextMessage(String token, String username, String groupId, String textMessage) throws ApiException;

	/**
	 * 更改用户密码
	 * 
	 * @param token
	 *            授权Token
	 * @param username
	 *            用户名称
	 * @param newpassword
	 *            用户新密码
	 * @return 执行结果
	 * @throws ApiException 
	 */
	JSONObject alterUserPassword(String token, String username, String newpassword) throws ApiException;
}
