package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.util.ProfileUtils;


public class ChangeEmailService extends AbstractService {
	private class ChangeEmailRequest extends UCRequest {
		private String email;

		public ChangeEmailRequest(HttpServletRequest request) {
			super(request);
			email = getParameter("email");
		}
		@Override
		public boolean validate() {
			return (super.validate() && ProfileUtils.isEmail(email));
		}
	}
	
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ChangeEmailRequest changeEmailReq = new ChangeEmailRequest(request);
		int status;
		if(changeEmailReq.validate()) {
			if(ucService.isSessionValid(changeEmailReq.getSessionId())) {
			    status = ucService.changeEmail( changeEmailReq.getUserid(),
			    		changeEmailReq.email);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
	    postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), changeEmailReq.getUserid(),changeEmailReq.getSessionId()));
	}
}
