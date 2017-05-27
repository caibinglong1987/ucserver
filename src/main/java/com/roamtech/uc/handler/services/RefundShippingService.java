package com.roamtech.uc.handler.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Refund;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefundShippingService extends AbstractService {
	private class RefundShippingRequest extends UCRequest {
		private Long refundId;
		private String shippingName;
		private String invoiceNo;

		public RefundShippingRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("refundid");
			refundId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				refundId = Long.parseLong(temp);
			}
			shippingName = getParameter("shipping_name");
			invoiceNo = getParameter("invoice_no");
		}

		@Override
		public boolean validate() {
			return (super.validate() && (refundId != -1L) && StringUtils.isNotBlank(shippingName) && StringUtils.isNotBlank(invoiceNo));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RefundShippingRequest refundReq = new RefundShippingRequest(request);
		int status = ErrorCode.SUCCESS;
		Refund refund = null;
		UCResponse ucResp = null;
		if(refundReq.validate()) {
			if(ucService.isSessionValid(refundReq.getSessionId())) {
				try {
					refund = ucService.shippingRefund(refundReq.getUserid(),refundReq.refundId, refundReq.shippingName, refundReq.invoiceNo);
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
