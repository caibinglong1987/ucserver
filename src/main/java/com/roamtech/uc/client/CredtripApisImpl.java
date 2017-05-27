package com.roamtech.uc.client;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.credtrip.gateway.CredtripService;
import com.roamtech.uc.util.HttpClientManager;

@Component("credtripApis")
public class CredtripApisImpl implements CredtripApis, InitializingBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(CredtripApisImpl.class);

	@Autowired
	private HttpClientManager httpClientManager;
	private String httpClientName="UCSERVER_HTTP_CLIENT";
    private HttpClient _client;	
    private static final String CREDTRIP_BASE_URI = "https://api-test.credtrip.com/";	

	private String toUriParams(Map<String, String> params) {
		String query = "";
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
	@Override
	public CredtripCredit queryCredit(Map<String, String> params) {
		Map<String, String> inparams = CredtripService.buildRequestPara(params);
		String response = httpClientManager.sendReceive(_client,CREDTRIP_BASE_URI+"api/user/queryCredit",null,toUriParams(inparams),MimeTypes.FORM_ENCODED);
		Map<String, String> result = JSON.parseObject(response,new TypeReference<Map<String,String>>(){});
		if(result.get("retCode").equalsIgnoreCase("SUCCESS")) {
			CredtripCredit credit = new CredtripCredit();
			credit.setAccountNo(result.get("accountNo"));
			credit.setStatus(result.get("status"));
			credit.setCredit(Integer.valueOf(result.get("accountCredit"))/100.0);
			credit.setBalcredit(Integer.valueOf(result.get("balCredit"))/100.0);
			return credit;
		} else {
			return null; 
		}
	}
/*    private final static String DEF_CLIENT_NAME = "$__DEFAULT_HTTP_CLIENT";
    public CredtripApisImpl() {
    	Map<String, String> config = new HashMap<String,String>();
    	config.put("name", httpClientName);
    	config.put("maxThreads", "250");
    	config.put("maxConnections", "500");
    	config.put("timeout", "60000");
    	try {
			_client = createClient(config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!_client.isStarted()){
			try {
				_client.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
	private HttpClient createClient(Map<String, String> config)
			throws Exception {
		if (null == config || !config.containsKey("name")) {
			config = new HashMap<String, String>();
			config.put("name", DEF_CLIENT_NAME);
		}
		String name = StringUtils.isEmpty(config.get("name")) ? DEF_CLIENT_NAME
				: config.get("name");
		
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
	public String sendReceive(HttpClient client, String serviceUri,
			Map<String, String> params,String body, String contentType) {
		ContentExchange exchange = new ContentExchange();
		URI uri = URI.create(serviceUri);
		String query = uri.getQuery();
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
		try {
			URI realUri = new URI(uri.getScheme(), uri.getUserInfo(),
					uri.getHost(), uri.getPort(), uri.getPath(), query,
					uri.getFragment());
			exchange.setURI(realUri);
			if(StringUtils.isNotBlank(body)) {
				exchange.setRequestHeader("Content-Type",contentType);
				exchange.setMethod(HttpMethods.POST);
				byte[] sbody = body.getBytes();
				ByteArrayBuffer buffer = new ByteArrayBuffer(sbody.length);
				buffer.put(sbody);
				exchange.setRequestContent(buffer);
			}
			client.send(exchange);

			// Waits until the exchange is terminated
			int exchangeState = exchange.waitForDone();
			if (exchangeState == HttpExchange.STATUS_COMPLETED) {
				if (exchange.getResponseStatus() != 200) {
					return null;
				}
				String response = exchange.getResponseContent();
				return response;
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
	public static void main(String[] args) {
		CredtripApisImpl credtripApis = new CredtripApisImpl();
		Map<String,String> params = new HashMap<String,String>();
		params.put("version", "1.0");
		params.put("product", "01");
		params.put("mobile", "13967124998");
		params.put("merchantId", "2016010003");
		CredtripCredit credit = credtripApis.queryCredit(params);
		System.out.println(credit);
	}*/
	@Override
	public void afterPropertiesSet() throws Exception {
		_client = httpClientManager.getClient(httpClientName);
		if(!_client.isStarted()){
			_client.start();
		}
	}
}
