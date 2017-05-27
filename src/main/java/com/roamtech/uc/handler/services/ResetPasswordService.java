package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;


public class ResetPasswordService extends AbstractService {
	private class ResetPasswordRequest extends AbstractRequest {
		private String password;
		private String checkid;
		private String checkcode;
		public ResetPasswordRequest(HttpServletRequest request) {
			super(request);
			password = getParameter("npassword");
			checkid = getParameter("checkid");
			checkcode = getParameter("checkcode");
		}
		@Override
		public boolean validate() {
			return (StringUtils.isNotBlank(password)
					&& StringUtils.isNotBlank(checkid) && StringUtils.isNotBlank(checkcode));
		}
		public String getPassword() {
			return password;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ResetPasswordRequest resetPasswordReq = new ResetPasswordRequest(request);
		int status;
		if(resetPasswordReq.validate()) {
			if(ucService.verifyCode(Long.valueOf(resetPasswordReq.checkid), resetPasswordReq.checkcode)) { 
			    status = ucService.resetPassword(resetPasswordReq.getUserid(),
			    		                             resetPasswordReq.getPassword());
			} else {
				status = ErrorCode.ERR_VCODE_MISMATCH;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
	    postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), resetPasswordReq.getUserid(),null));
	}
}
