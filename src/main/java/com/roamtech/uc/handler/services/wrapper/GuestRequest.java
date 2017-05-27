package com.roamtech.uc.handler.services.wrapper;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.annotation.JSONField;


public class GuestRequest extends AbstractRequest {
	@JSONField(name = "sessionid")
	private String sessionId;

	public GuestRequest(HttpServletRequest request) {
		super(request);
		sessionId = getParameter("sessionid");
	}

	@Override
	public boolean validate() {
		return true;
	}
	public String getSessionId() {
		return sessionId;
	}
}
