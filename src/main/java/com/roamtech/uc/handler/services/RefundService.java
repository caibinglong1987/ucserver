package com.roamtech.uc.handler.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.Refund;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefundService extends AbstractService {
	private class RefundRequest extends UCRequest {
		private Long orderId;
		private Long orderDetailId;
		private Integer reasonId;
		private String reason;

		public RefundRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			temp = getParameter("orderdetailid");
			orderDetailId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderDetailId = Long.parseLong(temp);
			}
			reason = getParameter("reason");
			temp = getParameter("reasonid");
			reasonId = 0;
			if(StringUtils.isNotBlank(temp)) {
				reasonId = Integer.parseInt(temp);
			}

		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderId != -1L || orderDetailId != -1L) && (reasonId != 0 || StringUtils.isNotBlank(reason)));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RefundRequest refundReq = new RefundRequest(request);
		int status = ErrorCode.SUCCESS;
		Refund refund = null;
		UCResponse ucResp = null;
		if(refundReq.validate()) {
			if(ucService.isSessionValid(refundReq.getSessionId())) {
				try {
					refund = ucService.applyRefundOrder(refundReq.getUserid(),refundReq.orderId, refundReq.orderDetailId, refundReq.reasonId, refundReq.reason);
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), refundReq.getUserid(),refundReq.getSessionId());
		ucResp.addAttribute("refund", refund);
		postProcess(baseRequest, response, ucResp);
	}
}
