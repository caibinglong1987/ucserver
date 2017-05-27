package com.roamtech.uc.handler.services.wrapper;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;


public class UCRequest extends AbstractRequest {
	@JSONField(name = "sessionid")
	private String sessionId;
	@JSONField(name = "tenantid")
	private Long tenantId;
	@JSONField(name = "clientid")
	private String clientId;

	public UCRequest(HttpServletRequest request) {
		super(request);
		sessionId = getParameter("sessionid");
		if (StringUtils.isBlank(sessionId)) {
			sessionId = (String) request.getAttribute("sessionid");
			tenantId = (Long) request.getAttribute("tenantid");
			clientId = (String) request.getAttribute("clientid");
		} else {
			tenantId = User.ROAM_TENANT_ID;
		}
	}

	@Override
	public boolean validate() {
		return ( userid != null && userid != 0);
	}

	public String getSessionId() {
		return sessionId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public String getClientId() {
		return clientId;
	}
}
