package com.roamtech.uc.handler.om.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Refund;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefundAuditService extends AbstractService {
	private class RefundAuditRequest extends UCRequest {
		private Long refundId;
		private Boolean confirmed;

		public RefundAuditRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("refundid");
			refundId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				refundId = Long.parseLong(temp);
			}
			temp = getParameter("confirmed");
			confirmed = false;
			if(StringUtils.isNotBlank(temp)) {
				confirmed = Boolean.parseBoolean(temp);
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (refundId != -1L));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RefundAuditRequest refundReq = new RefundAuditRequest(request);
		int status = ErrorCode.SUCCESS;
		Refund refund = null;
		UCResponse ucResp = null;
		if(refundReq.validate()) {
			Integer usertype = omService.checkSessionValid(refundReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				try {
					refund = ucService.confirmRefund(refundReq.refundId,refundReq.confirmed);
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), refundReq.getUserid(),refundReq.getSessionId());
		ucResp.addAttribute("refund", refund);
		postProcess(baseRequest, response, ucResp);
	}
}
