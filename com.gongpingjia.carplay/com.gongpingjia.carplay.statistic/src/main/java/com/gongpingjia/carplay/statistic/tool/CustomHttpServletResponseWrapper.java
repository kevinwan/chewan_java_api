package com.gongpingjia.carplay.statistic.tool;

import com.gongpingjia.carplay.common.domain.ResponseDo;
import net.sf.json.JSONObject;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * Created by Administrator on 2015/11/2 0002.
 */
public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper{

    private final FastByteArrayOutputStream content = new FastByteArrayOutputStream(1024);

    private final ServletOutputStream outputStream = new ResponseServletOutputStream();

    private PrintWriter writer;

    private int statusCode = HttpServletResponse.SC_OK;

    private Integer contentLength;

    private ResponseDo responseDo;


    /**
     * Create a new ContentCachingResponseWrapper for the given servlet response.
     * @param response the original servlet response
     */
    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }


    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setStatus(int sc, String sm) {
        super.setStatus(sc, sm);
        this.statusCode = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc);
        }
        catch (IllegalStateException ex) {
            // Possibly on Tomcat when called too late: fall back to silent setStatus
            super.setStatus(sc);
        }
        this.statusCode = sc;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendError(int sc, String msg) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc, msg);
        }
        catch (IllegalStateException ex) {
            // Possibly on Tomcat when called too late: fall back to silent setStatus
            super.setStatus(sc, msg);
        }
        this.statusCode = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        copyBodyToResponse(false);
        super.sendRedirect(location);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            String characterEncoding = getCharacterEncoding();
            this.writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding) :
                    new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
        }
        return this.writer;
    }

    @Override
    public void setContentLength(int len) {
        this.content.resize(len);
        this.contentLength = len;
    }

    // Overrides Servlet 3.1 setContentLengthLong(long) at runtime
    public void setContentLengthLong(long len) {
        if (len > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Content-Length exceeds ShallowEtagHeaderFilter's maximum (" +
                    Integer.MAX_VALUE + "): " + len);
        }
        int lenInt = (int) len;
        this.content.resize(lenInt);
        this.contentLength = lenInt;
    }

    @Override
    public void setBufferSize(int size) {
        this.content.resize(size);
    }

    @Override
    public void resetBuffer() {
        this.content.reset();
    }

    @Override
    public void reset() {
        super.reset();
        this.content.reset();
    }

    /**
     * Return the status code as specified on the response.
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Return the cached response content as a byte array.
     */
    public byte[] getContentAsByteArray() {
        return this.content.toByteArray();
    }

    /**
     * Return an {@link java.io.InputStream} to the cached content.
     * @since 4.2
     */
    public InputStream getContentInputStream(){
        return this.content.getInputStream();
    }

    /**
     * Return the current size of the cached content.
     * @since 4.2
     */
    public int getContentSize(){
        return this.content.size();
    }

    /**
     * Copy the complete cached body content to the response.
     * @since 4.2
     */
    public void copyBodyToResponse() throws IOException {
        copyBodyToResponse(true);
    }


    public ResponseDo getResponseDo() {
        if (null == this.responseDo) {
            String contentStr = new String(getContentAsByteArray());
            JSONObject json = JSONObject.fromObject(contentStr);
            if (json.getInt("result") == 0) {
                this.responseDo = ResponseDo.buildSuccessResponse(json.get("data"));
            }else {
                this.responseDo = ResponseDo.buildFailureResponse(json.getString("errmsg"));
            }
        }
        return this.responseDo;
    }

    /**
     * Copy the cached body content to the response.
     * @param complete whether to set a corresponding content length
     * for the complete cached body content
     * @since 4.2
     */
    protected void copyBodyToResponse(boolean complete) throws IOException {
        if (this.content.size() > 0) {
            HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
            if ((complete || this.contentLength != null) && !rawResponse.isCommitted()){
                rawResponse.setContentLength(complete ? this.content.size() : this.contentLength);
                this.contentLength = null;
            }
            this.content.writeTo(rawResponse.getOutputStream());
            this.content.reset();
        }
    }


    private class ResponseServletOutputStream extends ServletOutputStream {

        @Override
        public void write(int b) throws IOException {
            content.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            content.write(b, off, len);
        }
    }


    private class ResponsePrintWriter extends PrintWriter {

        public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter(content, characterEncoding));
        }

        @Override
        public void write(char buf[], int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }




}
