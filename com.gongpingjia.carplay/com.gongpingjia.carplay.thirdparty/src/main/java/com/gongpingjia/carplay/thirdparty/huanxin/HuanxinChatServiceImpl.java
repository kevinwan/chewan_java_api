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
        } catch (Exception e) {
            LOG.error("Create emchat user failure", e.getMessage());
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
        } catch (Exception e) {
            LOG.error("Create emchat group failure", e.getMessage());
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
        json.put("description", description);

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

    @Override
    public JSONObject sendUserGroupMessage(String token, String adminUser, List<String> toUsers, String textMessage, Object ext) throws ApiException {
        LOG.debug("Begin send message by {}", adminUser);

        if (toUsers == null || toUsers.isEmpty()) {
            return new JSONObject();
        }

        StringBuilder httpUrl = buildRequestUrl();
        httpUrl.append("messages");

        List<Header> headers = buildCommonHeaders(token);

        JSONObject param = new JSONObject();
        param.put("target_type", "users");

        JSONObject msg = new JSONObject();
        msg.put("type", "txt");
        msg.put("msg", textMessage);
        param.put("msg", msg);

        param.put("from", adminUser);
        if (ext != null) {
            param.put("ext", ext);
        }

        final int limit = 20;
        int fromIndex = 0;
        int toIndex = (toUsers.size() > limit) ? limit : toUsers.size();
        int count = 1;
        while (toUsers.size() > toIndex) {
            if (count % 20 == 0) {
                try {
                    //环信存在每秒钟能够发送消息的限制, 发送次数超过达到20次时，休息一秒钟
                    LOG.debug("Sleep one second for send message limit by emchat");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOG.warn(e.getMessage());
                }
            }
            List<String> subUsers = toUsers.subList(fromIndex, toIndex);
            param.put("target", subUsers);
            sendEmchatMessage(httpUrl, headers, param);
            //计算下一次截断尺寸
            fromIndex = toIndex;
            if (toUsers.size() - toIndex > limit) {
                toIndex += limit;
            } else {
                toIndex = toUsers.size();
            }
        }

        List<String> finalUsers = toUsers.subList(fromIndex, toIndex);
        param.put("target", finalUsers);

        sendEmchatMessage(httpUrl, headers, param);

        return new JSONObject();
    }

    @Override
    public JSONObject sendUserGroupMessage(String token, String adminUser, String toUserEmchatName, String textMessage) throws ApiException {
        List<String> emchatNames = new ArrayList<>(1);
        emchatNames.add(toUserEmchatName);
        return this.sendUserGroupMessage(token, adminUser, emchatNames, textMessage, new Object());
    }

    /**
     * 发送消息
     *
     * @param httpUrl URL请求
     * @param headers
     * @param param
     * @return
     */
    private CloseableHttpResponse sendEmchatMessage(StringBuilder httpUrl, List<Header> headers, JSONObject param) {
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtil.post(httpUrl.toString(), param.toString(), headers, Constants.Charset.UTF8);
            if (!HttpClientUtil.isStatusOK(response)) {
                LOG.warn("Send emchat message failure, param:{}", param);
            } else {
                LOG.info("Send emchat message success, param:{}", param);
            }
        } catch (Exception e) {
            LOG.error("Request for send emchat message falure", e);
        } finally {
            HttpClientUtil.close(response);
        }
        return response;
    }

    public static void main(String[] args) throws ApiException {
        String httpUrl = "https://a1.easemob.com:443/gongpingjia/carplayapp/messages";

        List<Header> headers = new ArrayList<Header>(2);
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Authorization",
                MessageFormat.format(AUTH_HEADER_FORMAT,
                        "YWMtsxGcunDDEeWm6k8ZZ_uVGwAAAVGQX3Ib5a1W7kr5MTSKQkN1BAAgP53U2tE")));

        List<String> users = new ArrayList<>(2);
        users.add("c4fee7d8c48234cf09c41b0013271fac");

        JSONObject param = new JSONObject();
        param.put("target_type", "users");
        param.put("target", users);

        JSONObject msg = new JSONObject();
        msg.put("type", "txt");
        msg.put("msg", "Test for send message");
        param.put("msg", msg);

        param.put("from", "SubscribeAdmin");

        Map<String, Object> ext = new HashMap<>(1);
        ext.put("avatar", "李程测试ext属性avatar");
        param.put("ext", ext);

        System.out.println(param.toString());
        CloseableHttpResponse response = HttpClientUtil.post(httpUrl.toString(),
                param.toString(), headers,
                Constants.Charset.UTF8);

        JSONObject result = HttpClientUtil.parseResponseGetJson(response);

        System.out.println(result);
    }
}
