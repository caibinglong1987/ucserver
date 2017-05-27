package com.roamtech.uc.handler.om.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.User;


public class OrderShippedService extends AbstractService {
	private class ODShippedRequest extends UCRequest {
		private Long orderId;
		private String invoiceNo;

		public ODShippedRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			invoiceNo = getParameter("invoice_no");
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderId != -1L) && StringUtils.isNotBlank(invoiceNo));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ODShippedRequest odSetReq = new ODShippedRequest(request);
		int status = ErrorCode.SUCCESS;
		Order od = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				Integer usertype = omService.checkSessionValid(odSetReq.getSessionId());
				if(usertype == -1) {
					status = ErrorCode.ERR_SESSIONID_INVALID;
				} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
					od = ucService.shippedOrder(odSetReq.orderId, odSetReq.invoiceNo);
				} else {
					status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
				}	
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(),odSetReq.getSessionId());
		ucResp.addAttribute("order", od);
		postProcess(baseRequest, response, ucResp);
	}
}
