package com.roamtech.uc.handler.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderConfirmService extends AbstractService {
	private class OrderConfirmRequest extends UCRequest {
		private Long orderId;

		public OrderConfirmRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && orderId != -1L);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		OrderConfirmRequest odSetReq = new OrderConfirmRequest(request);
		int status = ErrorCode.SUCCESS;
		Order order = null;
		UCResponse ucResp = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				try {
					order = ucService.confirmOrder(odSetReq.getUserid(),odSetReq.orderId);
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(),odSetReq.getSessionId());
		ucResp.addAttribute("order", order);
		postProcess(baseRequest, response, ucResp);
	}
}
