package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class RegisterService extends AbstractService {
	private class RegisterRequest extends AbstractRequest {
		private String username;
		private String password;
		private String email;
		private String mobile;
		private String checkid;
		private String checkcode;

		public RegisterRequest(HttpServletRequest request) {
			super(request);
			email = getParameter("email");
			mobile = getParameter("phone");
			username = getParameter("username");
			password = getParameter("password");
			checkid = getParameter("checkid");
			checkcode = getParameter("checkcode");
		}

		@Override
		public boolean validate() {
			return (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)&& StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(checkid) && StringUtils.isNotBlank(checkcode));
		}

		public User getUser() {
			User user = new User();
			
			user.setEmail(email);
			user.setPhone(mobile);
			user.setPassword(password);
			user.setUserName(username);
			user.setType(User.NORMAL_USER);
			return user;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RegisterRequest registerReq = new RegisterRequest(request);
		User user = null;
		Session session = null;
		int status = ErrorCode.SUCCESS;
		if(registerReq.validate()) {
			user = registerReq.getUser();
		    if(ucService.isUsernameExist( user.getUserName())) {
				status = ErrorCode.ERR_ACCOUNT_EXIST;
			} else if(user.getEmail()!=null&&ucService.isEmailExist(user.getEmail())) {
				status = ErrorCode.ERR_EMAIL_EXIST;
			} else if(user.getPhone()!=null&&ucService.isMobileExist(user.getPhone())) {
				status = ErrorCode.ERR_MOBILE_EXIST;
			} else if(!ucService.verifyCode(Long.valueOf(registerReq.checkid), registerReq.checkcode)) {
				status = ErrorCode.ERR_VCODE_MISMATCH;
			} else {	
				user.setStatus(User.STATUS_ACTIVE);
				user = ucService.save(user);
				session = ucService.login(user);
				ucService.bindPhone(user.getUserId(), user.getPhone(), user.getTenantId(),true);
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), user);
		ucResp.setSessionId(session!=null?session.getSessionId():null);
	    postProcess(baseRequest, response, ucResp);
	}

}
