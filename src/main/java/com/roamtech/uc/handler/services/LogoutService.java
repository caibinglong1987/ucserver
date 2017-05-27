package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;


public class LogoutService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		UCRequest ucRequest = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		if(ucRequest.validate()) {
			ucService.logout(ucRequest.getUserid(), ucRequest.getSessionId());
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status)/*, ucRequest.getUserid(), ucRequest.getSessionId()*/));
	}

}
