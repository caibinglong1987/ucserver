package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.util.ProfileUtils;


public class IsEmailExistService extends AbstractService {
	private class EmailExistRequest extends AbstractRequest {
		private String email;
		public EmailExistRequest(HttpServletRequest request) {
			super(request);
			email = getParameter("email");
		}

		@Override
		public boolean validate() {
			return (ProfileUtils.isEmail(email));
		}		
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		EmailExistRequest emailExistReq = new EmailExistRequest(request);
		int status = ErrorCode.SUCCESS;
		Boolean isExist = false;
		if(emailExistReq.validate()) {
			isExist = ucService.isEmailExist(emailExistReq.email);
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse hsidResponse = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		hsidResponse.addAttribute("is_exist", isExist);
		Map<String, Object> extraLog = new HashMap<String,Object>();
		extraLog.put("result", isExist);
		postProcess(baseRequest, response, hsidResponse, extraLog);
	}
}
