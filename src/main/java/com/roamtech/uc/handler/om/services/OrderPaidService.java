package com.roamtech.uc.handler.om.services;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCardTraffic;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class OrderPaidService extends AbstractService {
	private class OrderPaiedRequest extends UCRequest {
		private Long orderid;
		private String payvoucher;
		private Double amount;

		OrderPaiedRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderid = Long.parseLong(temp);
			}
			payvoucher = getParameter("payvoucher");
			amount = 0.0;
			temp = getParameter("amount");
			if(StringUtils.isNotBlank(temp)) {
				amount = Double.valueOf(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		OrderPaiedRequest orderPaiedReq = new OrderPaiedRequest(request);
		int status = ErrorCode.SUCCESS;
		Order order = null;
		if(orderPaiedReq.validate()) {
			Integer usertype = omService.checkSessionValid(orderPaiedReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER) {
				order = ucService.paidOrder(orderPaiedReq.orderid, orderPaiedReq.payvoucher, orderPaiedReq.amount);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), orderPaiedReq.getUserid(),orderPaiedReq.getSessionId());
		ucResp.addAttribute("order", order);
		postProcess(baseRequest, response, ucResp);
	}
}
