
package com.roamtech.uc.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;



public class HttpClientManager implements InitializingBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(HttpClientManager.class);

	private final static String DEF_CLIENT_NAME = "$__DEFAULT_HTTP_CLIENT";

	/**
	 * 配置信息
	 */
	private List<Map<String, String>> clientConfigs = new ArrayList<Map<String, String>>();

	/**
	 * 客户端映射表
	 */
	private Map<String, HttpClient> clientMap = new ConcurrentHashMap<String, HttpClient>();

	/**
	 * @return the clientConfigs
	 */
	public List<Map<String, String>> getClientConfigs() {
		return clientConfigs;
	}

	/**
	 * @param clientConfigs
	 *            the clientConfigs to set
	 */
	public void setClientConfigs(List<Map<String, String>> clientConfigs) {
		this.clientConfigs = clientConfigs;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug("HttpClientManager initialization begin...");
		clientMap.put(DEF_CLIENT_NAME, createClient(null));
		if (null != clientConfigs) {
			for (Map<String, String> config : clientConfigs) {
				HttpClient client = createClient(config);
				clientMap.put(
						((QueuedThreadPool) client.getThreadPool()).getName(),
						client);
			}
		}
		LOG.debug("HttpClientManager initialization end.");
	}

	/**
	 * 获取默认命名的Client
	 * 
	 * @return
	 */
	public HttpClient getClient() {
		return getClient(DEF_CLIENT_NAME);
	}

	/**
	 * 获取指定命名的Client(如果指定命名的Client不存在，返回默认命名的Client)
	 * 
	 * @param name
	 *            Client名称
	 * @return
	 */
	public HttpClient getClient(String name) {
		HttpClient client = clientMap.get(name);
		return null == client ? clientMap.get(DEF_CLIENT_NAME) : client;
	}

	/**
	 * 创建Client
	 * 
	 * @param config
	 *            Client配置
	 * @return
	 * @throws Exception
	 */
	private HttpClient createClient(Map<String, String> config)
			throws Exception {
		if (null == config || !config.containsKey("name")) {
			config = new HashMap<String, String>();
			config.put("name", DEF_CLIENT_NAME);
		}
		String name = StringUtils.isEmpty(config.get("name")) ? DEF_CLIENT_NAME
				: config.get("name");
		// 防止创建重名的Client
		if (null != clientMap.get(name)) {
			return clientMap.get(name);
		}
		
		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		
		Integer maxThreads = StringUtils.isEmpty(config.get("maxThreads")) ? 100
				: Integer.valueOf(config.get("maxThreads"));
		if (0 < maxThreads) {
			client.setThreadPool(new QueuedThreadPool(maxThreads));
		} else {
			client.setThreadPool(new QueuedThreadPool());
		}
		
		((QueuedThreadPool) client.getThreadPool()).setName(name);		

		Integer maxConnections = StringUtils.isEmpty(config
				.get("maxConnections")) ? 100 : Integer.valueOf(config
				.get("maxConnections"));
		if (0 < maxConnections) {
			client.setMaxConnectionsPerAddress(maxConnections);
		}

		Long timeout = StringUtils.isEmpty(config.get("timeout")) ? 10000L : Long
				.valueOf(config.get("timeout"));
		if (0L < timeout) {
			client.setTimeout(timeout);
		}

		Long idleTimeout = StringUtils.isEmpty(config.get("idleTimeout")) ? 0L
				: Long.valueOf(config.get("idleTimeout"));
		if (0L < idleTimeout) {
			client.setIdleTimeout(idleTimeout);
		}

		Integer requestHeaderSize = StringUtils.isEmpty(config
				.get("requestHeaderSize")) ? 0 : Integer.valueOf(config
				.get("requestHeaderSize"));
		if (0 < requestHeaderSize) {
			client.setRequestHeaderSize(requestHeaderSize);
		}

		Integer responseHeaderSize = StringUtils.isEmpty(config
				.get("responseHeaderSize")) ? 0 : Integer.valueOf(config
				.get("responseHeaderSize"));
		if (0 < responseHeaderSize) {
			client.setResponseHeaderSize(responseHeaderSize);
		}

		Integer requestBufferSize = StringUtils.isEmpty(config
				.get("requestBufferSize")) ? 0 : Integer.valueOf(config
				.get("requestBufferSize"));
		if (0 < requestBufferSize) {
			client.setRequestBufferSize(requestBufferSize);
		}

		Integer responseBufferSize = StringUtils.isEmpty(config
				.get("responseBufferSize")) ? 0 : Integer.valueOf(config
				.get("responseBufferSize"));
		if (0 < responseBufferSize) {
			client.setResponseBufferSize(responseBufferSize);
		}
		LOG.debug("HttpClient [" + name + "] has been created.");
		return client;
	}
	public static String buildUrlParams(Map<String,String> params, String query) {
		if (null != params) {
			for (String key : params.keySet()) {
				String value = params.get(key);
				if (StringUtils.isEmpty(query)) {
					query = key.concat("=").concat(value);
				} else {
					query = query.concat("&").concat(key).concat("=")
							.concat(value);
				}
			}
		}
		return query;
	}
	public ContentExchange send(HttpClient client, String serviceUri,
			Map<String, String> params) {
		ContentExchange exchange = new ContentExchange();
		// exchange.setRequestHeader("Content-Type",
		// "html/text; charset=utf-8");
		URI uri = URI.create(serviceUri);
		String query = uri.getQuery();
		if (null != params) {
			query = buildUrlParams(params, query);
		}
		try {
			URI realUri = new URI(uri.getScheme(), uri.getUserInfo(),
					uri.getHost(), uri.getPort(), uri.getPath(), query,
					uri.getFragment());
			exchange.setURI(realUri);

			client.send(exchange);
			return exchange;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 发起HttpClient服务调用并获取响应结果
	 * 
	 * @param serviceUri
	 * @param params
	 * @return
	 */
	public String sendReceive(HttpClient client, String serviceUri,
			Map<String, String> params,String body, String contentType) {
		return sendReceive(client, serviceUri, null , params, body, contentType);
	}

	public String sendReceive(HttpClient client, String serviceUri,
							  Map<String, String> headers,Map<String, String> params,String body, String contentType) {
		ContentExchange exchange = sendReceiveWithStatus(client, serviceUri, headers, params, body, contentType);
		if(exchange.getResponseStatus() != 200) {
			return null;
		}
		try {
			return exchange.getResponseContent();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	public ContentExchange sendReceiveWithStatus(HttpClient client, String serviceUri,
							  Map<String, String> headers,Map<String, String> params,String body, String contentType) {
		ContentExchange exchange = new ContentExchange();
		URI uri = URI.create(serviceUri);
		String query = uri.getQuery();
		if (null != params) {
			query = buildUrlParams(params, query);
		}
		try {
			URI realUri = new URI(uri.getScheme(), uri.getUserInfo(),
					uri.getHost(), uri.getPort(), uri.getPath(), query,
					uri.getFragment());
			exchange.setURI(realUri);
			if(headers != null) {
				for(Map.Entry<String,String> header:headers.entrySet()) {
					exchange.addRequestHeader(header.getKey(),header.getValue());
				}
			}
			LOG.info(realUri.toASCIIString());
			if(StringUtils.isNotBlank(body)) {
				exchange.setRequestHeader("Content-Type",contentType);
				exchange.setMethod(HttpMethods.POST);
				byte[] sbody = body.getBytes();
				ByteArrayBuffer buffer = new ByteArrayBuffer(sbody.length);
				buffer.put(sbody);
				exchange.setRequestContent(buffer);
				LOG.info("Post Body:"+body);
			}
			client.send(exchange);

			// Waits until the exchange is terminated
			int exchangeState = exchange.waitForDone();
			if (exchangeState == HttpExchange.STATUS_COMPLETED) {
				return exchange;
			} else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
				return null;
			} else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
				return null;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	public String paramsToString(Map<String, String> params) {
		String PostBody = "";
		if (null != params) {
			for (String key : params.keySet()) {
				String value = params.get(key);
				if (StringUtils.isEmpty(PostBody)) {
					PostBody = key.concat("=").concat(value);
				} else {
					PostBody = PostBody.concat("&").concat(key).concat("=")
							.concat(value);
				}
			}
		}
		return PostBody;
	}
}
