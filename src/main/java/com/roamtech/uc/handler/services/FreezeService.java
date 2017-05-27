package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;



public class FreezeService extends AbstractService {
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest ucRequest = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		User user = null;
		if(ucRequest.validate()) {
			user = ucService.freeze( ucRequest.getUserid());
			if(user == null) {
				status = ErrorCode.ERR_ACCOUNT_NOEXIST;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), ucRequest.getUserid(), null));
	}

}
