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
import com.roamtech.uc.model.Cart;
import com.roamtech.uc.model.Order;

public class OrderCancelService extends AbstractService {
	private class OrderCancelRequest extends UCRequest {
		private Long orderId;
		private Boolean delete;

		public OrderCancelRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			temp = getParameter("delete");
			delete = false;
			if(StringUtils.isNotBlank(temp)) {
				delete = Boolean.valueOf(temp);
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
		OrderCancelRequest odSetReq = new OrderCancelRequest(request);
		int status = ErrorCode.SUCCESS;
		Order order = null;
		UCResponse ucResp = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				try {
					order = ucService.cancelOrder(odSetReq.getUserid(),odSetReq.orderId, odSetReq.delete);
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
