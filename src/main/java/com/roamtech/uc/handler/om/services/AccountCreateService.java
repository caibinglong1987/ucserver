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

public class AccountCreateService extends AbstractService {
	private class AccountCreateRequest extends UCRequest {
		private Long companyid;
		private Long roleid;
		private Integer quantity;
		private String username;
		private String password;

		public AccountCreateRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("companyid");
			companyid = 0L;
			if(temp != null) {
				companyid = Long.valueOf(temp);
			}
			temp = getParameter("roleid");
			if(temp != null) {
				roleid = Long.valueOf(temp);
			}
			temp = getParameter("quantity");
			quantity = 1;
			if(temp != null) {
				quantity = Integer.valueOf(temp);
			}
			username = getParameter("username");
			password = getParameter("password");
		}

		@Override
		public boolean validate() {
			return (super.validate() && companyid !=0L && roleid != 0L && quantity !=0);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		AccountCreateRequest accountCreateReq = new AccountCreateRequest(request);
		int status = ErrorCode.SUCCESS;
		List<User> users = null;
		if(accountCreateReq.validate()) {
			Integer usertype = omService.checkSessionValid(accountCreateReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER) {
				users = omService.createAccounts(accountCreateReq.companyid,accountCreateReq.roleid,accountCreateReq.quantity, accountCreateReq.username, accountCreateReq.password);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), accountCreateReq.getUserid(),accountCreateReq.getSessionId());
		ucResp.addAttribute("users", users);
		postProcess(baseRequest, response, ucResp);
	}

}
