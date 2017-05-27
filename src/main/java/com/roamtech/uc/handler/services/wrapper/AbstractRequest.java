package com.roamtech.uc.handler.services.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractRequest.class);	
	protected Long userid;
	protected HttpServletRequest request;
	private Map<String, Object> requestMap;
	public AbstractRequest(HttpServletRequest request) {
		this.request = request;
		ByteArrayBuffer buffer = (ByteArrayBuffer)request.getAttribute("post_body");
		requestMap = doGetParameters(request,buffer);
		String userId = getParameter("userid");
		userid = (long)0;
		if(StringUtils.isNotBlank(userId)) {
			userid = Long.parseLong(userId);
		} else {
			Long id = (Long) request.getAttribute("userid");
			if (id != null) {
				userid = id;
			}
		}
	}

	public Long getUserid() {
		return userid;
	}
	public abstract boolean validate();
	 @SuppressWarnings("rawtypes")
	 protected Map<String, Object> doGetParameters(HttpServletRequest request,ByteArrayBuffer buffer) {

		/**
		 * http请求方式的参数解析
		 */
		Map requestMap = request.getParameterMap();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		for (Object key : requestMap.keySet()) {
			Object value = requestMap.get(key);
			if (value instanceof String[]) {
				if (((String[]) value).length == 1) {
					paraMap.put((String) key, ((String[]) value)[0]);
				}
			} else {
				paraMap.put((String) key, value);
			}
		}

		/**
		 * 解析json方式发送的请求参数
		 */
		String decodeData = null;

		if(buffer != null) {
			try {
				decodeData = URLDecoder.decode(new String(buffer.array(), "UTF-8"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.warn(e.getMessage());
			}
		}
		if (StringUtils.isNotBlank(decodeData) && decodeData.length() > 0) {
			String content_type = HttpFields.valueParameters(request.getContentType(),null);
			if(content_type.contains("json")) {
				Map<String, Object> map = JSON.parseObject(decodeData,new TypeReference<Map<String, Object>>(){});
				LOG.info("post map:{}",map);
				paraMap.putAll(map);
				return paraMap;
			}
		}
		return paraMap;
	}
	public String getParameter(String name) {
		Object val = requestMap.get(name);
		if(val == null || val instanceof String) {
			return (String) val;
		}
		return val.toString();
	}
	public JSONArray getParameters(String name) {
		Object val = requestMap.get(name);
		if(val == null || val instanceof JSONArray) {
			return (JSONArray) val;
		}
		return null;
	}
	public JSONObject getParametersJSON(String name) {
		Object val = requestMap.get(name);
		if(val == null || val instanceof JSONObject) {
			return (JSONObject) val;
		}
		return null;
	}
	public String getParameter(JSONObject json, String name) {
		if(json != null) {
			String value = json.getString(name);
			if(value != null) {
				return value;
			}
		}
		Object val = requestMap.get(name);
		if(val == null || val instanceof String) {
			return (String) val;
		}
		return val.toString();
	}
	public Map<String, Object> getParametersMap() {
		return requestMap;
	}
}
