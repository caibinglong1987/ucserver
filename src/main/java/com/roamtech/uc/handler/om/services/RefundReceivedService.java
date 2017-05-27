package com.roamtech.uc.handler.om.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Refund;
import com.roamtech.uc.model.User;
import com.roamtech.uc.util.JSONUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RefundReceivedService extends AbstractService {
	private class RefundReceivedRequest extends UCRequest {
		private List<Long> refundIds;
		private String invoiceNo;

		public RefundReceivedRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("refundids");
			if(StringUtils.isNotBlank(temp)) {
				refundIds = JSON.parseObject(temp, new TypeReference<List<Long>>() {
				});
			}
			invoiceNo = getParameter("invoice_no");
		}

		@Override
		public boolean validate() {
			return (super.validate() && (refundIds != null) && StringUtils.isNotBlank(invoiceNo));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RefundReceivedRequest refundReq = new RefundReceivedRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Refund> refunds = null;
		UCResponse ucResp = null;
		if(refundReq.validate()) {
			Integer usertype = omService.checkSessionValid(refundReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				refunds = ucService.receivedRefunds(refundReq.refundIds,refundReq.invoiceNo);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), refundReq.getUserid(),refundReq.getSessionId());
		ucResp.addAttribute("refunds", refunds);
		postProcess(baseRequest, response, ucResp);
	}
}
