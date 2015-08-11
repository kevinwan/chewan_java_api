package com.gongpingjia.carplay.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * 调用HTTP的get请求
	 * 
	 * @param httpUrl
	 *            请求URL
	 * @param params
	 *            参数信息
	 * @return 返回响应结果
	 */
	public static CloseableHttpResponse get(String httpUrl, Map<String, String> params, List<Header> headers) {
		LOG.debug("Request url:" + httpUrl);
		LOG.debug("Request params:" + params);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		CloseableHttpResponse response = null;
		try {
			URIBuilder uriBilder = new URIBuilder(httpUrl);

			for (Entry<String, String> entry : params.entrySet()) {
				uriBilder.setParameter(entry.getKey(), entry.getValue());
			}

			HttpGet httpGet = new HttpGet(uriBilder.build());

			for (Header header : headers) {
				httpGet.addHeader(header);
			}

			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		return response;
	}

	// public static Map<String, String> parseResponse(CloseableHttpResponse
	// response) {
	// Map<String, String> map = new HashMap<String, String>();
	// if (response == null) {
	// return map;
	// }
	//
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// InputStream instream = null;
	// try {
	// instream = entity.getContent();
	//
	// } catch (UnsupportedOperationException e) {
	// LOG.error(e.getMessage(), e);
	// } catch (IOException e) {
	// LOG.error(e.getMessage(), e);
	// } finally {
	// try {
	// if (instream != null) {
	// instream.close();
	// }
	// } catch (IOException e) {
	// LOG.error(e.getMessage(), e);
	// }
	// }
	// }
	// return map;
	// }

}
