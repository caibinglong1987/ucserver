package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;

public class AccountDeleteService extends AbstractService {
	private class AccountDeleteRequest extends UCRequest {
		private String userame;

		public AccountDeleteRequest(HttpServletRequest request) {
			super(request);
			userame = getParameter("username");
		}

		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(userame));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		AccountDeleteRequest accountDeleteReq = new AccountDeleteRequest(request);
		int status = ErrorCode.SUCCESS;
		User user = null;
		if(accountDeleteReq.validate()) {
			Integer usertype = omService.checkSessionValid(accountDeleteReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER) {
				user = omService.deleteAccount(accountDeleteReq.userame);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), accountDeleteReq.getUserid(),accountDeleteReq.getSessionId());
		ucResp.addAttribute("user", user);
		postProcess(baseRequest, response, ucResp);
	}

}
