package com.gongpingjia.carplay.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gongpingjia.carplay.common.exception.ApiException;

public class HttpClientUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * 调用HTTP的get请求
	 * 
	 * @param httpUrl
	 *            请求URL
	 * @param params
	 *            参数信息
	 * @return 返回响应结果HttpResponse,记得用完关闭流
	 * @throws ApiException
	 */
	public static CloseableHttpResponse get(String httpUrl, Map<String, String> params, List<Header> headers,
			String charSetName) throws ApiException {
		LOG.debug("Request url:{}", httpUrl);
		LOG.debug("Request params:{}", params);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		CloseableHttpResponse response = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(httpUrl);
			uriBuilder.setCharset(Charset.forName(charSetName));

			for (Entry<String, String> entry : params.entrySet()) {
				uriBuilder.setParameter(entry.getKey(), entry.getValue());
			}

			HttpGet httpGet = new HttpGet(uriBuilder.build());

			for (Header header : headers) {
				httpGet.addHeader(header);
			}

			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("请求异常");
		} catch (IOException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("请求异常");
		} catch (URISyntaxException e) {
			LOG.warn(e.getMessage(), e);
			throw new ApiException("请求异常");
		}

		return response;
	}

	/**
	 * 关闭响应流
	 * 
	 * @param response
	 *            CloseableHttpResponse 对象
	 */
	public static void close(CloseableHttpResponse response) {
		try {
			if (response != null) {
				response.close();
			}
		} catch (IOException e) {
			LOG.error("Close http response failure");
		}
	}

	/**
	 * 解析HTTP响应，返回响应体的字符串
	 * 
	 * @param response
	 *            响应体
	 * @return 返回响应字符串
	 */
	public static String parseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				return EntityUtils.toString(entity);
			} catch (ParseException e) {
				LOG.error("Convert response entity to String failure with ParseException");
			} catch (IOException e) {
				LOG.error("Convert response entity to String failure with IOException");
			}
		}
		return "";
	}

}
