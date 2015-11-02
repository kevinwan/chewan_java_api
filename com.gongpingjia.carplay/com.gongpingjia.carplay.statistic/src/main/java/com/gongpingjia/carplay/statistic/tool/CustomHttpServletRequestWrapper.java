package com.gongpingjia.carplay.statistic.tool;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * Created by Administrator on 2015/10/29 0029.
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {


    private final byte[] body; // 报文

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        byte[] buffer = new byte[512];
        ServletInputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = 0;
        while ((len = inputStream.read(buffer)) > -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        body = outputStream.toByteArray();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteInStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return byteInStream.read();
            }
        };
    }
}
