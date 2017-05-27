package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Company;

public class CompanyGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest paymentGetsReq = new GuestRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Company> companys = null;
		if(paymentGetsReq.validate()) {
			companys = ucService.getCompanys();
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), paymentGetsReq.getUserid(),paymentGetsReq.getSessionId());
		ucResp.addAttribute("companys", companys);
		postProcess(baseRequest, response, ucResp);
	}
}
