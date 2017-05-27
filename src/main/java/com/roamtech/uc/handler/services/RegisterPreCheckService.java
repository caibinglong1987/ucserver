package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class RegisterPreCheckService extends AbstractService {
	private class RegisterPreRequest extends AbstractRequest {
		private String username;
		private String mobile;


		public RegisterPreRequest(HttpServletRequest request) {
			super(request);
			mobile = getParameter("phone");
			username = getParameter("username");
		}

		@Override
		public boolean validate() {
			return (StringUtils.isNotBlank(username));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RegisterPreRequest registerReq = new RegisterPreRequest(request);
		int status = ErrorCode.SUCCESS;
		if(registerReq.validate()) {
		    if(ucService.isUsernameExist( registerReq.username)) {
				status = ErrorCode.ERR_ACCOUNT_EXIST;
			} else if(ucService.isMobileExist(registerReq.mobile)) {
				status = ErrorCode.ERR_MOBILE_EXIST;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status)));
	}

}
