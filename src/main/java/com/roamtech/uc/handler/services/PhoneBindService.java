package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PhoneBindService extends AbstractService {
	private class PhoneBindRequest extends UCRequest {
		private String mobile;
		private String checkid;
		private String checkcode;

		public PhoneBindRequest(HttpServletRequest request) {
			super(request);
			mobile = getParameter("phone");
			checkid = getParameter("checkid");
			checkcode = getParameter("checkcode");
		}

		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(checkid) && StringUtils.isNotBlank(checkcode));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		PhoneBindRequest phoneBindReq = new PhoneBindRequest(request);
		int status = ErrorCode.SUCCESS;
		if(phoneBindReq.validate()) {
			if(ucService.isSessionValid(phoneBindReq.getSessionId())) {
			    if(!ucService.verifyCode(Long.valueOf(phoneBindReq.checkid), phoneBindReq.checkcode)) {
					status = ErrorCode.ERR_VCODE_MISMATCH;
					ucService.bindPhone(phoneBindReq.getUserid(), phoneBindReq.mobile, phoneBindReq.getTenantId(),false);
				} else {				
					ucService.bindPhone(phoneBindReq.getUserid(), phoneBindReq.mobile, phoneBindReq.getTenantId(),true);
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), phoneBindReq.getUserid(),phoneBindReq.getSessionId()));
	}

}
