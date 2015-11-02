package com.gongpingjia.carplay.statistic.filter;

import com.gongpingjia.carplay.statistic.tool.CustomHttpServletRequestWrapper;
import com.gongpingjia.carplay.statistic.tool.CustomHttpServletResponseWrapper;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class ServletWrapFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest temp = (HttpServletRequest) servletRequest;
            //只对POST方法进行包装
            if (temp.getMethod().toUpperCase().equals("POST")) {
                requestWrapper = new CustomHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            }
        }
        if (servletResponse instanceof HttpServletResponse) {
            responseWrapper = new CustomHttpServletResponseWrapper((HttpServletResponse) servletResponse);
//            responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
        }
        if (requestWrapper != null) {
            servletRequest = requestWrapper;
        }
        if (responseWrapper != null) {
            servletResponse = responseWrapper;
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
