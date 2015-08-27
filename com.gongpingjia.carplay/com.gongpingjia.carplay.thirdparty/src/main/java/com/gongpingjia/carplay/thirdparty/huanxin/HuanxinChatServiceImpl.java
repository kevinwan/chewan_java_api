package com.gongpingjia.carplay.thirdparty.huanxin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;

@Service
public class HuanxinChatServiceImpl implements ChatThirdPartyService {

	private static final Logger LOG = LoggerFactory.getLogger(HuanxinChatServiceImpl.class);

	private static final String AUTH_HEADER_FORMAT = "Bearer {0}";

	@Override
	public JSONObject getApplicationToken() throws ApiException {
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
			response = HttpClientUtil.post(httpUrl.toString(), params, headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}
		return new JSONObject();
	}

	@Override
	public JSONObject registerChatUser(String token, Map<String, String> userMap) throws ApiException {
		LOG.debug("Begin register chat user");
		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("users");

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), userMap, headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				// 只有在返回为200 的情况向才解析结果
				return HttpClientUtil.parseResponseGetJson(response);
			}
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
			List<String> members) throws ApiException {
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
			response = HttpClientUtil.post(httpUrl.toString(), json.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
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
	public JSONObject modifyChatGroup(String token, String groupId, String groupName, String description)
			throws ApiException {
		LOG.debug("Modify chat group, groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId);

		JSONObject json = new JSONObject();
		json.put("groupname", groupName);
		json.put("desc", description);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.put(httpUrl.toString(), json.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject deleteChatGroup(String token, String groupId) throws ApiException {
		LOG.debug("Delete chat group, groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.delete(httpUrl.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject addUserToChatGroup(String token, String groupId, String username) throws ApiException {
		LOG.debug("Begin add user to chat group");

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId).append("/");
		httpUrl.append("users").append("/").append(username);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), "", headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject deleteUserFromChatGroup(String token, String groupId, String username) throws ApiException {
		LOG.debug("Delete user from chat group, groupId:{}, username:{}", groupId, username);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("chatgroups").append("/").append(groupId).append("/");
		httpUrl.append("users").append("/").append(username);

		List<Header> headers = buildCommonHeaders(token);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.delete(httpUrl.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject sendChatGroupTextMessage(String token, String username, String groupId, String textMessage)
			throws ApiException {
		LOG.debug("Send text message to chat group with groupId:{}", groupId);

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("messages");

		List<Header> headers = buildCommonHeaders(token);

		JSONObject json = new JSONObject();
		json.put("target_type", "chatgroups");
		
		JSONArray array = new JSONArray();
		array.add(groupId);
		json.put("target", array);

		JSONObject msg = new JSONObject();
		msg.put("type", "txt");
		msg.put("msg", textMessage);

		json.put("msg", msg);
		json.put("from", username);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.post(httpUrl.toString(), json.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}
		} finally {
			HttpClientUtil.close(response);
		}

		return new JSONObject();
	}

	@Override
	public JSONObject alterUserPassword(String token, String username, String newpassword) throws ApiException {
		LOG.debug("Begin alter user password");

		StringBuilder httpUrl = buildRequestUrl();
		httpUrl.append("users").append("/");
		httpUrl.append(username).append("/").append("password");

		List<Header> headers = buildCommonHeaders(token);

		JSONObject param = new JSONObject();
		param.put("newpassword", newpassword);

		CloseableHttpResponse response = null;
		try {
			response = HttpClientUtil.put(httpUrl.toString(), param.toString(), headers, Constants.Charset.UTF8);
			if (HttpClientUtil.isStatusOK(response)) {
				return HttpClientUtil.parseResponseGetJson(response);
			}

		} finally {
			HttpClientUtil.close(response);
		}
		return new JSONObject();
	}

	// public static void main(String[] args) throws ApiException {
	// String httpUrl =
	// "https://a1.easemob.com:443/gongpingjia/carplayapp/messages";
	//
	// List<Header> headers = new ArrayList<Header>(2);
	// headers.add(new BasicHeader("Content-Type", "application/json"));
	// headers.add(new BasicHeader("Authorization",
	// MessageFormat.format(AUTH_HEADER_FORMAT,
	// "YWMterwfoEsOEeWcnQPhxKXuVQAAAVCZQBpQvnlmA3ZdjXXVz6gnv_Czb3Ar1cU")));
	//
	// JSONObject json = new JSONObject();
	// json.put("target_type", "chatgroups");
	//
	// JSONArray array = new JSONArray();
	// array.add("99123551197462944");
	// json.put("target", array);
	//
	// JSONObject msg = new JSONObject();
	// msg.put("type", "txt");
	// msg.put("msg", "Test send message");
	//
	// json.put("msg", msg);
	// json.put("from", "4c5dd2e14f07667b244176e782206910");
	//
	// CloseableHttpResponse response = HttpClientUtil.post(httpUrl.toString(),
	// json.toString(), headers,
	// Constants.Charset.UTF8);
	//
	// JSONObject result = HttpClientUtil.parseResponseGetJson(response);
	//
	// System.out.println(result);
	// }
}
