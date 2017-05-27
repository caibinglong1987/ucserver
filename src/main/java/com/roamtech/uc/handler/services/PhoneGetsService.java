package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Phone;

public class PhoneGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest phoneGetsReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Phone> phones = null;
		if(phoneGetsReq.validate()) {
			if(ucService.isSessionValid(phoneGetsReq.getSessionId())) {
				phones = ucService.getPhones(phoneGetsReq.getUserid());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), phoneGetsReq.getUserid(),phoneGetsReq.getSessionId());
		ucResp.addAttribute("phones", phones);
		postProcess(baseRequest, response, ucResp);
	}
}
