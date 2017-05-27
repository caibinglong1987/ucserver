package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;


import com.roamtech.uc.handler.ServiceHandler;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class ChangePasswordService extends AbstractService {
	private class ChangePasswordRequest extends UCRequest {
		private String opassword;
		private String npassword;
		public ChangePasswordRequest(HttpServletRequest request) {
			super(request);
			opassword = getParameter("opassword");
			npassword = getParameter("npassword");
		}
		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(opassword) && StringUtils.isNotBlank(npassword));
		}
		public String getOldPassword() {
			return opassword;
		}
		public String getNewPassword() {
			return npassword;
		}
	}
	
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ChangePasswordRequest changePasswordReq = new ChangePasswordRequest(request);
		int status;
		if(changePasswordReq.validate()) {
			if(ucService.isSessionValid(changePasswordReq.getSessionId())) {
				status = ucService.changePassword(changePasswordReq.getUserid(),
		    		                             changePasswordReq.getOldPassword(), changePasswordReq.getNewPassword());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
	    postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), changePasswordReq.getUserid(), changePasswordReq.getSessionId()));
	}
}
