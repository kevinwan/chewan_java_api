package com.gongpingjia.carplay.statistic.tool;

import com.gongpingjia.carplay.common.util.Constants;
import net.sf.json.JSONObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class PostContentUtil {

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
}
