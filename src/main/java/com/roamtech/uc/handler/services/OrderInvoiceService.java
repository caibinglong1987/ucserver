package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.exception.UCBaseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;

public class OrderInvoiceService extends AbstractService {
	private class ODInvoiceRequest extends UCRequest {
		private Long orderId;
		private Boolean receipt;
		private String invPayee;
		private String invContent;

		public ODInvoiceRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			temp = getParameter("receipt");
			receipt = false;
			if(StringUtils.isNotBlank(temp)) {
				receipt = Boolean.valueOf(temp);
			}
			if(receipt) {
				invPayee = getParameter("inv_payee");
				invContent = getParameter("inv_content");
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderId != -1L) && (receipt && StringUtils.isNotBlank(invPayee) && StringUtils.isNotBlank(invContent)));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ODInvoiceRequest odSetReq = new ODInvoiceRequest(request);
		int status = ErrorCode.SUCCESS;
		Order od = null;
		UCResponse ucResp = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				try {
					od = ucService.updateOrder(odSetReq.getUserid(), odSetReq.orderId, odSetReq.receipt, odSetReq.invPayee, odSetReq.invContent);
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp!=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(),odSetReq.getSessionId());
		ucResp.addAttribute("order", od);
		postProcess(baseRequest, response, ucResp);
	}
}
