package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.client.CredtripCredit;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class CredtripQueryCreditService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest ucReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		CredtripCredit credit = null;
		if(ucReq.validate()) {
			if(ucService.isSessionValid(ucReq.getSessionId())) {
				credit = ucService.queryCredtripCredit(ucReq.getUserid());
				if(credit == null) {
					status = ErrorCode.ERR_CREDTRIPACCOUNT_NOEXIST;
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), ucReq.getUserid(),ucReq.getSessionId());
		ucResp.addAttribute("credtripcredit", credit);
		postProcess(baseRequest, response, ucResp);
	}
}
