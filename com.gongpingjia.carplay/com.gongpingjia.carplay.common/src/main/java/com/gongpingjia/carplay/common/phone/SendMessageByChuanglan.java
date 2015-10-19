package com.gongpingjia.carplay.common.phone;

import com.gongpingjia.carplay.common.exception.ApiException;
import com.gongpingjia.carplay.common.util.Constants;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.common.util.PropertiesUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by licheng on 2015/10/19.
 */
public class SendMessageByChuanglan {

    private static final Logger LOG = LoggerFactory.getLogger(SendMessageByChuanglan.class);

    /**
     * 发送短消息
     *
     * @param phone      手机号
     * @param verifyCode 验证码
     * @throws ApiException 业务异常
     */
    public static void sendMessage(String phone, String verifyCode) throws ApiException {
        LOG.debug("Send message by Chuanglan begin, send phone:{} verifyCode:{}", phone, verifyCode);
        // 调用运营商接口发送验证码短信
        String httpUrl = PropertiesUtil.getProperty("message.send.chuanglan.url", "");

        Map<String, String> params = new HashMap<>(8, 1);
        params.put("account", PropertiesUtil.getProperty("message.send.chuanglan.account", ""));
        params.put("pswd", PropertiesUtil.getProperty("message.send.chuanglan.pswd", ""));
        params.put("mobile", phone);
        params.put("needstatus", PropertiesUtil.getProperty("message.send.chuanglan.needstatus", "true"));
        params.put("msg", MessageFormat.format(PropertiesUtil.getProperty("message.send.chuanglan.msg", ""), verifyCode));
        params.put("product", null);
        params.put("extno", null);

        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtil.get(httpUrl, params, new ArrayList<Header>(0), Constants.Charset.UTF8);
            if (HttpClientUtil.isStatusOK(response)) {
                LOG.info("Send verifyCode success");
                LOG.info(HttpClientUtil.parseResponse(response));
            } else {
                LOG.warn("Send verifyCode message faliure, phone:{}, verifyCode:{}", phone, verifyCode);
                LOG.warn(HttpClientUtil.parseResponse(response));
            }
        } finally {
            if (response != null) {
                HttpClientUtil.close(response);
            }
        }
    }

//
//    public static void main(String[] args) throws ApiException {
//        Map<String, String> params = new HashMap<>();
//        params.put("account", "VIP_gpj");
//        params.put("pswd", "Tch123456");
//        params.put("mobile", "15365087965");
//        params.put("needstatus", "true");
//        params.put("msg", MessageFormat.format("尊敬的用户，您的验证码为{0}，感谢您使用【车玩】", "1234"));
//        params.put("product", null);
//        params.put("extno", null);
//
//        CloseableHttpResponse response = HttpClientUtil.get("http://222.73.117.158/msg/HttpBatchSendSM",
//                params, new ArrayList<Header>(0), Constants.Charset.UTF8);
//        System.out.println(response.getStatusLine());
//        System.out.println(HttpClientUtil.parseResponse(response));
//        HttpClientUtil.close(response);
//    }
}
