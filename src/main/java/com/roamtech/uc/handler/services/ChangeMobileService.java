package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.util.ProfileUtils;


public class ChangeMobileService extends AbstractService {
	private class ChangeMobileRequest extends UCRequest {
		private String mobile;

		public ChangeMobileRequest(HttpServletRequest request) {
			super(request);
			mobile = getParameter("mobile");
		}
		@Override
		public boolean validate() {
			return (super.validate() && ProfileUtils.isPhoneNumberValid(mobile));
		}
	}
	
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ChangeMobileRequest changeMobileReq = new ChangeMobileRequest(request);
		int status;
		if(changeMobileReq.validate()) {
			if(ucService.isSessionValid(changeMobileReq.getSessionId())) {
			    status = ucService.changeMobile( changeMobileReq.getUserid(),
			    		changeMobileReq.mobile);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
	    postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), changeMobileReq.getUserid(),changeMobileReq.getSessionId()));
	}
}
