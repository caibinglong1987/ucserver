package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.exception.UCBaseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.UCService;
import com.roamtech.uc.handler.services.wrapper.CartSetRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.OrderDetail;

public class OrderDetailSetService extends AbstractService {
	private class ODSetRequest extends CartSetRequest {
		private Long orderId;
		private Long orderDetailId;

		public ODSetRequest(HttpServletRequest request, UCService ucService) {
			super(request,ucService);
			String temp = getParameter("orderdetailid");
			orderDetailId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderDetailId = Long.parseLong(temp);
			}
			temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			/*temp = request.getParameter("quantity");
			quantity = 1;
			if(temp != null) {
				quantity = Integer.parseInt(temp);
			}
			startTime = request.getParameter("effect_datetime");
			endTime = request.getParameter("failure_datetime");*/
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderDetailId != -1L || orderId != -1L) && productId != -1L);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ODSetRequest odSetReq = new ODSetRequest(request, ucService);
		int status = ErrorCode.SUCCESS;
		Order od = null;
		UCResponse ucResp = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				try {
					if(odSetReq.orderId != -1L) {
						od = ucService.addOrderDetail(odSetReq.getUserid(), odSetReq.orderId, odSetReq.getProductId(), odSetReq.getQuantity(), odSetReq.getCartPrdAttrs());
					} else {
						od = ucService.updateOrderDetail(odSetReq.getUserid(), odSetReq.orderDetailId, odSetReq.getProductId(), odSetReq.getQuantity(), odSetReq.getCartPrdAttrs());
					}
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp != null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(),odSetReq.getSessionId());
		ucResp.addAttribute("order", od);
		postProcess(baseRequest, response, ucResp);
	}
}
