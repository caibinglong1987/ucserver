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


public class IsUserNameExistService extends AbstractService {
	private class UserNameExistRequest extends AbstractRequest {
		private String username;
		public UserNameExistRequest(HttpServletRequest request) {
			super(request);
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
		UserNameExistRequest userNameExistReq = new UserNameExistRequest(request);
		int status = ErrorCode.SUCCESS;
		Boolean isExist = false;
		if(userNameExistReq.validate()) {
			isExist = ucService.isUsernameExist(userNameExistReq.username);
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
