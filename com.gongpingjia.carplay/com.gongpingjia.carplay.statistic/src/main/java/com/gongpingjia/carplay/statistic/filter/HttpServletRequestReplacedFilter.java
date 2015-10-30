package com.gongpingjia.carplay.statistic.filter;

import com.gongpingjia.carplay.statistic.tool.MAPIHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class HttpServletRequestReplacedFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        ServletRequest requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest temp = (HttpServletRequest) servletRequest;
            //只对POST方法进行包装
            if (temp.getMethod().toUpperCase().equals("POST")) {
                requestWrapper = new MAPIHttpServletRequestWrapper((HttpServletRequest) servletRequest);
            }

        }
        if (requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
