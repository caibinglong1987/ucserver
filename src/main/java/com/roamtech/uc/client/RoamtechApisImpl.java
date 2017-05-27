package com.roamtech.uc.client;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.client.retrofit.FastJsonConverterFactory;
import com.roamtech.uc.client.wx.WXMPService;
import com.roamtech.uc.client.wx.WXToken;
import com.roamtech.uc.util.HttpClientManager;
import com.roamtech.uc.util.HttpsUtils;
import com.roamtech.uc.util.JSONUtils;
import okhttp3.OkHttpClient;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.observables.BlockingObservable;
import sun.misc.BASE64Encoder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin03 on 2017/1/6.
 */
@Component("roamtechApis")
public class RoamtechApisImpl implements RoamtechApis, InitializingBean {
    private static final Logger LOG = LoggerFactory
            .getLogger(RoamtechApisImpl.class);
    @Autowired
    private HttpClientManager httpClientManager;
    private String httpClientName="UCSERVER_HTTP_CLIENT";
    private HttpClient _client;
    private String roamApiUri="https://www.roam-tech.com:8443/uc/services/";
    private final static String DEF_CLIENT_NAME = "$__DEFAULT_HTTP_CLIENT";
    private WXMPService wxmpService;
    private String wxmpUri="https://api.weixin.qq.com/sns/oauth2/";
    private String wxmpAppId="wxa02261a86e83d8d2";
    private String wxmpAppSecret="f4e1003f6388a65bb1e46a100ad8ca0c";
    class TokenRequest implements Serializable {
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
    public ContentExchange sendReceive(String serviceUri,
                              Map<String, String> headers, Map<String, String> params, String body, String contentType) {
        return httpClientManager.sendReceiveWithStatus(_client,serviceUri,headers, params,body,contentType);
        //return sendReceive(_client,serviceUri,headers, params,body,contentType);
    }
    @Override
    public RoamtechTokenResponse getToken(String appId, String appKey, String phone) {
        TokenRequest tokenReq = new TokenRequest();
        tokenReq.setPhone(phone);
        Map<String, String> headers = new HashMap<>();
        BASE64Encoder encoder = new BASE64Encoder();
        String authkey = appId+":"+appKey;
        headers.put("Authorization","Basic "+encoder.encode(authkey.getBytes()));
        ContentExchange exchange = sendReceive(roamApiUri+"token_get",headers, null, JSONUtils.serialize(tokenReq), MimeTypes.TEXT_JSON_UTF_8);
        try {
            return JSON.parseObject(exchange.getResponseContent(),RoamtechTokenResponse.class);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public Observable<WXToken> getWXMPTokenAsync(String code, String state) {
        Map<String, String> params = new HashMap<String,String>();
        params.put("appid", wxmpAppId);
        params.put("secret", wxmpAppSecret);
        params.put("code", code);
        params.put("grant_type","authorization_code");
        String url = "access_token?"+buildUrlParams(params);
        return wxmpService.GetAccessToken(url);
    }

    @Override
    public Observable<WXToken> getWXMPRefreshTokenAsync(String refreshToken) {
        Map<String, String> params = new HashMap<String,String>();
        params.put("appid", wxmpAppId);
        params.put("refresh_token", refreshToken);
        params.put("grant_type","refresh_token");
        String url = "refresh_token?"+buildUrlParams(params);
        return wxmpService.GetRefreshToken(url);
    }

    @Override
    public WXToken getWXMPToken(String code, String state) {
        return (WXToken)getWXMPTokenAsync(code, state).toBlocking().single();
    }

    @Override
    public WXToken getWXMPRefreshToken(String refreshToken) {
        return getWXMPRefreshTokenAsync(refreshToken).toBlocking().single();
    }

    private String buildUrlParams(Map<String,String> params) {
        return HttpClientManager.buildUrlParams(params, null);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        _client = httpClientManager.getClient(httpClientName);
        if(!_client.isStarted()){
            _client.start();
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        wxmpService = new Retrofit.Builder()
                .baseUrl(wxmpUri)
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(WXMPService.class);
    }
/*    private HttpClient createClient(Map<String, String> config)
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

    public ContentExchange sendReceive(HttpClient client, String serviceUri,
                              Map<String,String> headers, Map<String, String> params,String body, String contentType) {
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
            if(headers != null) {
				for(Map.Entry<String,String> header:headers.entrySet()) {
					exchange.addRequestHeader(header.getKey(),header.getValue());
				}
			}
            System.out.println(new Date()+realUri.toASCIIString());
            exchange.setTimeout(30000);
            if(StringUtils.isNotBlank(body)) {
                exchange.setRequestHeader("Content-Type",contentType);
                exchange.setMethod(HttpMethods.POST);
                byte[] sbody = body.getBytes();
                ByteArrayBuffer buffer = new ByteArrayBuffer(sbody.length);
                buffer.put(sbody);
                exchange.setRequestContent(buffer);
                System.out.println("POST body:"+body);
            }
            client.send(exchange);

            // Waits until the exchange is terminated
            int exchangeState = exchange.waitForDone();
            if (exchangeState == HttpExchange.STATUS_COMPLETED) {
               return exchange;
            } else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
                System.out.println("HttpExchange status="+exchangeState);
                return null;
            } else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
                System.out.println(new Date()+"HttpExchange status="+exchangeState);
                return null;
            }
            System.out.println("HttpExchange status="+exchangeState);
            return null;
        } catch (Exception e) {
            System.out.println("exception"+e.getMessage());
            return null;
        }
    }

    public RoamtechApisImpl() {
        Map<String, String> config = new HashMap<String, String>();
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
        if (!_client.isStarted()) {
            try {
                _client.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        final RoamtechApisImpl roamtechApis = new RoamtechApisImpl();
        RoamtechTokenResponse tokenResp = roamtechApis.getToken("com.caissa.teamtouristic", "18acdb3c-b7da-41cb-81cd-ca701c5edffc","13967124998");
        System.out.println(JSONUtils.serialize(tokenResp));
    }*/
    public String getRoamApiUri() {
        return roamApiUri;
    }

    public void setRoamApiUri(String roamApiUri) {
        this.roamApiUri = roamApiUri;
    }
}
