package com.gongpingjia.carplay.thirdparty.huanxin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

@Service
public class HuanxinChatServiceImpl implements ChatThirdPartyService {

	private static final Logger LOG = LoggerFactory.getLogger(HuanxinChatServiceImpl.class);

	private static final String AUTH_HEADER_FORMAT = "Bearer {0}";

	@Override
	public JSONObject getApplicationToken() {
		LOG.debug("Begin get ApplicationToken from chat server");
		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("token");

		Map<String, String> params = new HashMap<String, String>(3, 1);
		params.put("grant_type", "client_credentials");
		params.put("client_id", PropertiesUtil.getProperty("huanxin.client.id", ""));
		params.put("client_secret", PropertiesUtil.getProperty("huanxin.client.secret", ""));

		List<Header> headers = new ArrayList<Header>(1);
		headers.add(new BasicHeader("Content-Type", "application/json"));

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), params, headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}
		return new JSONObject();
	}

	@Override
	public JSONObject registerChatUser(String token, List<Map<String, String>> userList) {
		LOG.debug("Begin register chat user");
		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("users");

		List<User> userParams = new ArrayList<User>(userList.size());
		for (Map<String, String> map : userList) {
			userParams.add(new User(map.get("username"), map.get("password")));
		}

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), userParams, headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				// 只有在返回为200 的情况向才解析结果
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}
		return new JSONObject();
	}

	private StringBuilder buildRequestUrl() {
		StringBuilder httpUrl = new StringBuilder();
		httpUrl.append(PropertiesUtil.getProperty("huanxin.server.url", "https://a1.easemob.com:443/"));
		httpUrl.append(PropertiesUtil.getProperty("huanxin.organization", "gongpingjia")).append("/");
		httpUrl.append(PropertiesUtil.getProperty("huanxin.application", "carplayapp")).append("/");
		return httpUrl;
	}

	@Override
	public JSONObject createChatGroup(String token, String groupName, String description, String owner,
			List<String> members) {
		LOG.debug("Begin create chat group");

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups");

		JSONObject json = new JSONObject();
		json.put("groupname", groupName);
		json.put("desc", description);
		json.put("public", PropertiesUtil.getProperty("huanxin.group.public", true));
		json.put("maxusers", PropertiesUtil.getProperty("huanxin.group.maxusers", 500));
		json.put("approval", PropertiesUtil.getProperty("huanxin.group.approval", true));
		json.put("owner", owner);
		json.put("members", members);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), json.toString(), headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}
		return new JSONObject();
	}

	private List<Header> buildCommonHeaders(String token) {
		List<Header> headers = new ArrayList<Header>(2);
		headers.add(new BasicHeader("Content-Type", "application/json"));
		headers.add(new BasicHeader("Authorization", MessageFormat.format(AUTH_HEADER_FORMAT, token)));
		return headers;
	}

	@Override
	public JSONObject modifyChatGroup(String token, String groupId, String groupName, String description) {
		LOG.debug("Modify chat group, groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId);

		JSONObject json = new JSONObject();
		json.put("groupname", groupName);
		json.put("desc", description);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.put(httpUrl.toString(), json.toString(), headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject deleteChatGroup(String token, String groupId) {
		LOG.debug("Delete chat group, groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.delete(httpUrl.toString(), headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject addUserToChatGroup(String token, String groupId, String username) {
		LOG.debug("Begin add user to chat group");

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId).append("/");
		httpUrl.append("users").append("/").append(username);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), "", headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject deleteUserFromChatGroup(String token, String groupId, String username) {
		LOG.debug("Delete user from chat group, groupId:{}, username:{}", groupId, username);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId).append("/");
		httpUrl.append("users").append("/").append(username);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.delete(httpUrl.toString(), headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject sendChatGroupTextMessage(String token, String username, String groupId, String textMessage) {
		LOG.debug("Send text message to chat group with groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("messages");

		List<Header> headers = buildCommonHeaders(token);

		JSONObject json = new JSONObject();
		json.put("target_type", "chatgroups");
		json.put("target", groupId);

		JSONObject msg = new JSONObject();
		msg.put("type", "txt");
		msg.put("msg", textMessage);

		json.put("msg", msg);
		json.put("from", username);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), json.toString(), headers, "UTF-8");
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} catch (ApiException e) {
			LOG.error(e.getMessage());
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

}
