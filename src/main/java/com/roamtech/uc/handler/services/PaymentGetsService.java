package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Payment;

public class PaymentGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest paymentGetsReq = new GuestRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Payment> payments = null;
		if(paymentGetsReq.validate()) {
			String terminal = paymentGetsReq.getParameter("terminaltype");
			Integer terminalType = 1;
			if(StringUtils.isNotBlank(terminal)) {
				terminalType = Integer.parseInt(terminal);
			}
			payments = ucService.getPayments(terminalType);
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), paymentGetsReq.getUserid(),paymentGetsReq.getSessionId());
		ucResp.addAttribute("payments", payments);
		postProcess(baseRequest, response, ucResp);
	}
}
