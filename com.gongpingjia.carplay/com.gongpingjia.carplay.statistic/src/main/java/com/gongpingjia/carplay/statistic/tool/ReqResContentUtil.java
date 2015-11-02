package com.gongpingjia.carplay.statistic.tool;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.common.util.Constants;
import net.sf.json.JSONObject;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class ReqResContentUtil {

    public static JSONObject toJsonObject(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        StringBuffer buffer = new StringBuffer();
        byte content[] = new byte[512];
        while (inputStream.read(content) != -1) {
            buffer.append(new String(content, Constants.Charset.UTF8));
        }
        JSONObject json = JSONObject.fromObject(buffer.toString());
        return json;
    }

    public static ResponseDo getResponseDo(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper wrapRes = (ContentCachingResponseWrapper) response;
        String contentStr = new String(wrapRes.getContentAsByteArray());
        wrapRes.reset();
        JSONObject json = JSONObject.fromObject(contentStr);
        if (json.getInt("result") == 0) {
            return ResponseDo.buildSuccessResponse(json.get("data"));
        } else {
            return ResponseDo.buildFailureResponse(json.getString("errmsg"));
        }
    }
}
