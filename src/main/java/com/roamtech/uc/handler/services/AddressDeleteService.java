package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Cart;

public class AddressDeleteService extends AbstractService {
	private class AddressDeleteRequest extends UCRequest {
		private Long addrId;

		public AddressDeleteRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("addrid");
			addrId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				addrId = Long.parseLong(temp);
			}	
		}

		@Override
		public boolean validate() {
			return (super.validate());
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		AddressDeleteRequest addrSetReq = new AddressDeleteRequest(request);
		int status = ErrorCode.SUCCESS;
		if(addrSetReq.validate()) {
			if(ucService.isSessionValid(addrSetReq.getSessionId())) {
			    ucService.removeAddress(addrSetReq.getUserid(),addrSetReq.addrId);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), addrSetReq.getUserid(),addrSetReq.getSessionId());
		postProcess(baseRequest, response, ucResp);
	}
}
