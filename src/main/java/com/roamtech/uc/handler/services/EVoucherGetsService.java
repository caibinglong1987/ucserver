package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.UserEVoucher;

public class EVoucherGetsService extends AbstractService {
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest evReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		List<UserEVoucher> evouchers = null;
		if(evReq.validate()) {
			if(ucService.isSessionValid(evReq.getSessionId())) {
				evouchers = ucService.getEVouchers(evReq.getUserid(),evReq.getParameter("orderid"));
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), evReq.getUserid(),evReq.getSessionId());
		ucResp.addAttribute("evouchers", evouchers);
		postProcess(baseRequest, response, ucResp);
	}
}
