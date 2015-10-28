package com.gongpingjia.carplay.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(CommonInterceptor.class);

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object paramObject,
                                Exception paramException) throws Exception {
        LOG.info("=======afterCompletion=========" + request.getParameterMap().toString());
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object paramObject,
                           ModelAndView paramModelAndView) throws Exception {
        LOG.info("=======postHandle=========" + request.getParameterMap().toString());
        LOG.info("=======paramModelAndView==" + paramModelAndView);
        if (paramModelAndView != null) {
            LOG.info("=======postHandle======ViewName===" + paramModelAndView.getViewName());
            LOG.info("=======postHandle======ModelMap===" + paramModelAndView.getModelMap().toString());
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object paramObject)
            throws Exception {
        LOG.info("======Request URL: {}, ServletPath:{}", request.getRequestURL(), request.getServletPath());
        LOG.info("======Request parameter:{}", request.getParameterMap().toString());
        return true;
    }

}
