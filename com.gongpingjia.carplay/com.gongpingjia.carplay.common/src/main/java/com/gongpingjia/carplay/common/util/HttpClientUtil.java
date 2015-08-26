package com.gongpingjia.carplay.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gongpingjia.carplay.common.exception.ApiException;

public class HttpClientUtil {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * 全局的HttpClient
	 */
	private static CloseableHttpClient httpClient = getClient(true);

	/**
	 * 获取JSON对象工厂
	 */
	private static JsonNodeFactory jsonFactory = new JsonNodeFactory(false);

	/**
	 * Create a httpClient instance
	 * 
	 * @param isSSL
	 * @return HttpClient instance
	 */
	private static CloseableHttpClient getClient(boolean isSSL) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (isSSL) {
			X509TrustManager xtm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			try {

				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[] { xtm }, null);

				SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context);

				httpClient = HttpClients.custom().setSSLSocketFactory(factory).build();

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		return httpClient;
	}

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

			response = httpClient.execute(httpGet);
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
	 * 调用HTTP的Post请求
	 * 
	 * @param httpUrl
	 *            请求URL
	 * @param params
	 *            参数信息
	 * @return 返回响应结果HttpResponse,记得用完关闭流
	 * @throws ApiException
	 */
	public static CloseableHttpResponse post(String httpUrl, Map<String, String> params, List<Header> headers,
			String charSetName) throws ApiException {
		ObjectNode objectNode = jsonFactory.objectNode();
		for (Entry<String, String> param : params.entrySet()) {
			objectNode.put(param.getKey(), param.getValue());
		}
		return post(httpUrl, objectNode.toString(), headers, charSetName);
	}

	/**
	 * 调用HTTP的Post请求
	 * 
	 * @param httpUrl
	 *            请求URL
	 * @param params
	 *            参数信息
	 * @return 返回响应结果HttpResponse,记得用完关闭流
	 * @throws ApiException
	 */
	public static CloseableHttpResponse post(String httpUrl, String paramString, List<Header> headers,
			String charSetName) throws ApiException {
		LOG.debug("Request url:{}", httpUrl);
		LOG.debug("Request params:{}", paramString);

		CloseableHttpResponse response = null;

		try {
			URIBuilder uriBuilder = new URIBuilder(httpUrl);
			uriBuilder.setCharset(Charset.forName(charSetName));

			HttpPost httpPost = new HttpPost(uriBuilder.build());

			for (Header header : headers) {
				httpPost.addHeader(header);
			}

			httpPost.setEntity(new StringEntity(paramString, charSetName));

			response = httpClient.execute(httpPost);
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
			LOG.warn("Close http response failure");
		}
	}

	/**
	 * 解析HTTP响应，返回响应体的字符串
	 * 
	 * @param response
	 *            响应体
	 * @return 返回响应字符串
	 */
	public static String parseResponse(CloseableHttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				return EntityUtils.toString(entity);
			} catch (ParseException e) {
				LOG.warn("Convert response entity to String failure with ParseException", e);
			} catch (IOException e) {
				LOG.warn("Convert response entity to String failure with IOException", e);
			}
		}
		return "";
	}

	/**
	 * 解析HTTP响应，返回响应体的字节流，一般用户读取图片
	 * 
	 * @param response
	 *            响应体
	 * @return 返回响应字节流
	 */
	public static byte[] parseResponseGetBytes(CloseableHttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				return EntityUtils.toByteArray(entity);
			} catch (ParseException e) {
				LOG.warn("Convert response entity to String failure with ParseException", e);
			} catch (IOException e) {
				LOG.warn("Convert response entity to String failure with IOException", e);
			}
		}
		return new byte[0];
	}

	/**
	 * 解析HTTP响应，返回JSONObject对象
	 * 
	 * @param response
	 *            响应体
	 * @return 返回响应JSONObject对象
	 */
	public static JSONObject parseResponseGetJson(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		String entityString = "";
		if (entity != null) {
			try {
				entityString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				LOG.warn("Convert response entity to String failure with ParseException");
			} catch (IOException e) {
				LOG.warn("Convert response entity to String failure with IOException");
			}
		}
		return JSONObject.fromObject(entityString);
	}

	/**
	 * 判断返回的状态码是否为200，是200表示成功
	 * 
	 * @param response
	 *            响应结果
	 * @return 状态码为200，返回true，其他返回false
	 */
	public static boolean isStatusOK(HttpResponse response) {
		if (response == null) {
			return false;
		}

		return response.getStatusLine().getStatusCode() == Constants.HTTP_STATUS_OK;
	}

	public static CloseableHttpResponse put(String httpUrl, String paramString, List<Header> headers, String charSetName)
			throws ApiException {
		LOG.debug("Request url:{}", httpUrl);
		LOG.debug("Request params:{}", paramString);

		CloseableHttpResponse response = null;

		try {
			URIBuilder uriBuilder = new URIBuilder(httpUrl);
			uriBuilder.setCharset(Charset.forName(charSetName));

			HttpPut httpPut = new HttpPut(uriBuilder.build());

			for (Header header : headers) {
				httpPut.addHeader(header);
			}

			httpPut.setEntity(new StringEntity(paramString, charSetName));

			response = httpClient.execute(httpPut);
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

	public static CloseableHttpResponse delete(String httpUrl, List<Header> headers, String charSetName)
			throws ApiException {
		LOG.debug("Request url:{}", httpUrl);

		CloseableHttpResponse response = null;

		try {
			URIBuilder uriBuilder = new URIBuilder(httpUrl);
			uriBuilder.setCharset(Charset.forName(charSetName));

			HttpDelete httpDelete = new HttpDelete(uriBuilder.build());

			for (Header header : headers) {
				httpDelete.addHeader(header);
			}

			response = httpClient.execute(httpDelete);
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
	 * 执行HTTP请求，返回JSONObject对象
	 * 
	 * @param method
	 * @param httpUrl
	 * @param params
	 * @param headers
	 * @param charSetName
	 * @return
	 * @throws ApiException
	 */
	public JSONObject execute(HttpMethod method, String httpUrl, Map<String, String> params, List<Header> headers,
			String charSetName) throws ApiException {
		CloseableHttpResponse response = null;

		try {
			if (method == HttpMethod.POST) {
				response = post(httpUrl, params, headers, charSetName);
			} else if (method == HttpMethod.GET) {
				response = get(httpUrl, params, headers, charSetName);
			} else if (method == HttpMethod.DELETE) {
				response = delete(httpUrl, headers, charSetName);
			} else if (method == HttpMethod.PUT) {
				response = put(httpUrl, JSONObject.fromObject(params).toString(), headers, charSetName);
			}

			if (response != null) {
				return parseResponseGetJson(response);
			}
		} finally {
			close(response);
		}
		return new JSONObject();
	}

	// public static void main(String[] args) throws ApiException,
	// ParseException, IOException {
	// ObjectNode objectNode = jsonFactory.objectNode();
	// objectNode.put("username", "bebe84c777c3308d53ad81efda2d3365");
	// objectNode.put("password", "e10adc3949ba59abbe56e057f20f883e");
	//
	// List<Header> headers = new ArrayList<Header>(2);
	// headers.add(new BasicHeader("Content-Type", "application/json"));
	// headers.add(new BasicHeader("Authorization",
	// "Bearer YWMterwfoEsOEeWcnQPhxKXuVQAAAVCZQBpQvnlmA3ZdjXXVz6gnv_Czb3Ar1cU"));
	//
	// CloseableHttpResponse response =
	// post("https://a1.easemob.com/gongpingjia/carplayapp/users",
	// objectNode.toString(), headers, "UTF-8");
	// System.out.println(response.getStatusLine());
	// System.out.println(response.getEntity().toString());
	// System.out.println(EntityUtils.toString(response.getEntity()));
	// close(response);

	// ===========================
	// List<Header> headers = new ArrayList<Header>(0);
	//
	// Map<String, String> params = new HashMap<String, String>(0);
	//
	// CloseableHttpResponse response = get(
	// "http://7xknzo.com1.z0.glb.clouddn.com/asset/user/12abfe47-e0fd-4af6-a041-0cb67cbbabdd/avatar.jpg",
	// params, headers, "UTF-8");
	//
	// byte[] bytes = EntityUtils.toByteArray(response.getEntity());
	//
	// close(response);

	// =================================
	// JSONObject params = new JSONObject();
	// params.put("grant_type", "client_credentials");
	// params.put("app_id", "498313800000245057");
	// params.put("app_secret", "5239f57abf3f3ca71acd91fe7a973342");
	//
	// Header header = new BasicHeader("Accept",
	// "application/json; charset=UTF-8");
	//
	// CloseableHttpResponse response = post(
	// "https://oauth.api.189.cn/emp/oauth2/v3/access_token?grant_type=client_credentials&app_id=498313800000245057&app_secret=5239f57abf3f3ca71acd91fe7a973342",
	// "", Arrays.asList(header), "UTF-8");
	// System.out.println(response.getStatusLine());
	// System.out.println(response.getEntity().toString());
	// System.out.println(EntityUtils.toString(response.getEntity()));
	// close(response);
	//
	// }
}
