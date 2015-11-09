package com.gongpingjia.carplay.thirdparty.huanxin;

import com.gongpingjia.carplay.common.chat.ChatThirdPartyService;
import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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
            LOG.info("response:{}", response);
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

//    public static void main(String[] args) throws ApiException {
//        String httpUrl = "https://a1.easemob.com/gongpingjia/chewanapi/users";   //https://a1.easemob.com:443/gongpingjia/carplayapp/messages";
//
//        List<Header> headers = new ArrayList<Header>(2);
//        headers.add(new BasicHeader("Content-Type", "application/json"));
//        headers.add(new BasicHeader("Authorization",
//                MessageFormat.format(AUTH_HEADER_FORMAT,
//                        "YWMtD9GEQISyEeWFbu3fFVpJqAAAAVIS_kmOlETtJ6vv47meoQiicHA1jrjTV4Y")));
//
////        List<String> users = new ArrayList<>(2);
////        users.add("2575a950e18f4a003e1ab082861f572c");
////
////        JSONObject param = new JSONObject();
////        param.put("target_type", "users");
////        param.put("target", users);
////
////        JSONObject msg = new JSONObject();
////        msg.put("type", "txt");
////        msg.put("msg", "李程测试发送消息，准备换行\n下一行，原因：XXXX");
////        param.put("msg", msg);
////
////        param.put("from", "OfficialAdmin");
////
////        Map<String, Object> ext = new HashMap<>(1);
////        ext.put("avatar", "李程测试ext属性avatar");
////        param.put("ext", ext);
//
//        String[] array = new String[]{"563c6572f729f2c1ae5c75c2-695796c979bddc3f493b01447c42a8d7",
//                "563c6573f729f2c1ae5c75c5-40b7213253648bc6ed31e2987dd550c6",
//                "563c6573f729f2c1ae5c75c8-2269531b7feae0a0079ef45a06beef5f",
//                "563c6574f729f2c1ae5c75cb-bccef5bff48bc7587b2fe420ceebe290",
//                "563c6574f729f2c1ae5c75ce-bf8afae32fec2950cecf076581590ffb",
//                "563c6574f729f2c1ae5c75d1-05d6728369f30685aa1be59f4c8c1eb1",
//                "563c6575f729f2c1ae5c75d4-179b91d5c3f0d2cc502b6e48e4e3cd3d",
//                "563c6575f729f2c1ae5c75d7-a6543db233d11722e0a064074e894837",
//                "563c6576f729f2c1ae5c75da-7b1f7bfcbaded11ee4d8b9ac8e773521",
//                "563c6576f729f2c1ae5c75dd-38f24bd67d8d70dcda57633388d63a98",
//                "563c6577f729f2c1ae5c75e0-94111782550dd087af5a6cd2d2bd0cd4",
//                "563c6577f729f2c1ae5c75e3-f62503bb66f9a2609c8ce812dc76c11a",
//                "563c6578f729f2c1ae5c75e6-e9634616f174d7a63f55c98715a9d305",
//                "563c6578f729f2c1ae5c75e9-6c19fa09e9ffb7a743a4a35b91060644",
//                "563c6578f729f2c1ae5c75ec-9c7b48f79aa379087a298fe1ce3763bb",
//                "563c6579f729f2c1ae5c75ef-5d6dcc1b5c81f048052048b326bd54ce",
//                "563c6579f729f2c1ae5c75f2-56277ab2e98e325dd1d519c0d9cd9e9a",
//                "563c657af729f2c1ae5c75f5-318307fd96609f05d36a2dee901b0214",
//                "563c657af729f2c1ae5c75f8-a1f6d247f034ef4762eec98530de5825",
//                "563c657bf729f2c1ae5c75fb-78e61154b35691dd0e75f9e0fe6641a6",
//                "563c657bf729f2c1ae5c75fe-a5708f7027d5bb57f43f3a85956ce30f",
//                "563c657cf729f2c1ae5c7601-f52680c400a8afc7ae57906edd467b53",
//                "563c657cf729f2c1ae5c7604-b8ee647fb0fe2e4d67297838598c1292",
//                "563c657cf729f2c1ae5c7607-290f82beac9a9777802b2bae22e23a28",
//                "563c657df729f2c1ae5c760a-b11e8845628da4365c2163ec0ea165b1",
//                "563c657df729f2c1ae5c760d-0243460031b44a97a96c93ddba67c8cc",
//                "563c657ef729f2c1ae5c7610-9399e7d087d2098d74a8697b96d8cc06",
//                "563c657ef729f2c1ae5c7613-0e08eb111909f7b11561948ed0d96f15",
//                "563c657ff729f2c1ae5c7616-02f02b9431b4d35889015820891d68d5",
//                "563c657ff729f2c1ae5c7619-2ec0f75a85f7a1912a4d814845e2d4a1",
//                "563c657ff729f2c1ae5c761c-708900a561ba4a20b5f71fd3dc0fdd89",
//                "563c6580f729f2c1ae5c761f-60c044f2d257f7622cab8072ed72069f",
//                "563c6580f729f2c1ae5c7622-9a8e97efcfa90b27ad2909405dc25409"};
////        System.out.println(param.toString());
//        for (String item : array) {
//            String[] itemArray = item.split("-");
//            Map<String, String> chatUser = new HashMap<>(2, 1);
//
//            chatUser.put("username", EncoderHandler.encodeByMD5(itemArray[0]));
//            chatUser.put("password", itemArray[1]);
//            CloseableHttpResponse response = HttpClientUtil.post(httpUrl.toString(), chatUser, headers, Constants.Charset.UTF8);
//            System.out.println(response);
//            JSONObject result = HttpClientUtil.parseResponseGetJson(response);
//
//            System.out.println(result);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
////        CloseableHttpResponse response = HttpClientUtil.get(httpUrl.toString(),
////                new HashMap<String, String>(0), headers,
////                Constants.Charset.UTF8);
//
////        CloseableHttpResponse response = HttpClientUtil.put(httpUrl.toString(),
////                json.toString(), headers,
////                Constants.Charset.UTF8);
//
//
//
//    }
}
