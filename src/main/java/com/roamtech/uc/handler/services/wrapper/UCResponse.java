package com.roamtech.uc.handler.services.wrapper;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.model.User;

import java.util.HashMap;
import java.util.Map;


public class UCResponse extends AbstractResponse {
	@JSONField(name = "userid")
	private Long userId;
	@JSONField(name = "sessionid")
	private String sessionId;
	@JSONField(name ="result")
	private Map<String, Object> attributes;
	
	public UCResponse() {
		super();
	}
	public UCResponse(int errorNo, String errorInfo) {
		super(errorNo, errorInfo);
	}
	public UCResponse(String sessionId, Long userid, int errorNo, String errorInfo) {
		this(sessionId, userid, errorNo, errorInfo, null);
	}
	public UCResponse(String sessionId, Long userid, int errorNo, String errorInfo, Map<String,Object> attributes) {
		super(errorNo, errorInfo);
		this.sessionId = sessionId;
		this.userId = userid;
		this.attributes = attributes;
	}
	public void init(User user) {
		this.userId = user.getUserId();
		if(user.getUserName() != null) {
			addAttribute("username", user.getUserName());
		}
		if(user.getPhone() != null) {
			addAttribute("phone", user.getPhone());
		}
		if(user.getEmail() != null) {
			addAttribute("email", user.getEmail());
		}
		addAttribute("userid", user.getUserId());
		addAttribute("status", user.getStatus());
		addAttribute("type", user.getType());
		addAttribute("tenantid", user.getTenantId());
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public Object getAttribute(String key) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(key);
	}

	public void addAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		attributes.put(key, value);
	}
	
	public static UCResponse buildResponse(int errorNo, String errorInfo, User user) {
		UCResponse hsidResponse = new UCResponse(errorNo, errorInfo);
		if(errorNo == ErrorCode.SUCCESS || errorNo == ErrorCode.ERR_PASSWD_DEFAULT) {
			hsidResponse.init(user);
		}
		return hsidResponse;
	}

	public static UCResponse buildResponse(int errorNo, String errorInfo, Long hsid,String sessionId) {
		return new UCResponse(sessionId, hsid, errorNo, errorInfo);
	}
	@Override
	protected void logAttributes(Map<String, Object> attributes) {
		attributes.put("sessionid", sessionId);
		if(userId != null) {
			attributes.put("userid", userId);
		}
	}
	public static UCResponse buildResponse(int errorNo, String errorInfo) {
		return new UCResponse(errorNo, errorInfo);
	}
}
