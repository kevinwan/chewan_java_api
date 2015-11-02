package com.gongpingjia.carplay.statistic.interceptor;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import com.gongpingjia.carplay.statistic.tool.ReqResContentUtil;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class TestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("post")) {
            JSONObject postJson = ReqResContentUtil.toJsonObject(request);
            String data = postJson.getString("data");
            System.out.println(data);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ResponseDo responseDo = ReqResContentUtil.getResponseDo(response);
        if (responseDo.success()) {
            System.out.println(responseDo.getData());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
