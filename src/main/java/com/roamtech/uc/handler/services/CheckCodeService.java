package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.User;
import com.roamtech.uc.model.VerifyCode;

public class CheckCodeService extends AbstractService {
	private class CheckCodeRequest extends AbstractRequest {
		private String phone;
		
		private String email;
		public CheckCodeRequest(HttpServletRequest request) {
			super(request);
			phone = getParameter("phone");
			email = getParameter("email");
		}
		public boolean validate() {
			return ((StringUtils.isNotBlank(phone)) || (StringUtils.isNotBlank(email)));
		}
		public String getPhone() {
			return phone;
		}
		public String getEmail() {
			return email;
		}
		public boolean isSMSCode() {
			return (StringUtils.isNotBlank(phone));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		CheckCodeRequest ccReq = new CheckCodeRequest(request);
		int status = ErrorCode.SUCCESS;
		Long checkId=0L;
		Long userId=0L;
		if(ccReq.validate()) {
			userId = ccReq.getUserid();
			if(userId == 0) {
				Phone phone = ucService.findPhone(ccReq.getPhone());
				if(null != phone) {
					User user = ucService.findOne(phone.getUserId());
					userId = user.getUserId();
				}
			}
			VerifyCode vCode = ucService.getCode(userId, ccReq.getPhone(), ccReq.getEmail());
			
			/*if(ccReq.isSMSCode()) {
				//send a sms
			} else {
				// send a email
			}*/
			if(vCode.getStatus() != 0) {
				//status = ErrorCode.ERR_VCODE_SEND_FAIL;
				checkId = vCode.getCheckId();
			} else {				
				checkId = vCode.getCheckId();
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse hsidResponse = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		hsidResponse.setUserId(userId);
		hsidResponse.addAttribute("checkid", checkId);
		Map<String, Object> extraLog = new HashMap<String,Object>();
		extraLog.put("checkid", checkId);
		postProcess(baseRequest, response, hsidResponse, extraLog);
	}

}
