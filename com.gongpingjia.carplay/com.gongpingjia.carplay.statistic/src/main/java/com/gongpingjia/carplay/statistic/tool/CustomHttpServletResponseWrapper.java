package com.gongpingjia.carplay.statistic.tool;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2015/11/2 0002.
 */
public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper{

    private PrintWriter printWriter;

    private CharArrayWriter charArrayWriter;

    private ServletOutputStream outputStream;

    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        charArrayWriter = new CharArrayWriter();
        printWriter = new PrintWriter(charArrayWriter);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }
}
