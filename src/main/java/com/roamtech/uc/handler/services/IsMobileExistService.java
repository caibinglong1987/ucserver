package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.model.Phone;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.util.ProfileUtils;


public class IsMobileExistService extends AbstractService {
	private class MobileExistRequest extends AbstractRequest {
		private String mobile;
		public MobileExistRequest(HttpServletRequest request) {
			super(request);
			mobile = getParameter("phone");
		}

		@Override
		public boolean validate() {
			return true/*(ProfileUtils.isPhoneNumberValid(mobile))*/;
		}		
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		MobileExistRequest mobileExistReq = new MobileExistRequest(request);
		int status = ErrorCode.SUCCESS;
		Boolean isExist = false;
		Phone phone = null;
		if(mobileExistReq.validate()) {
			if(ProfileUtils.isPhoneNumberValid(mobileExistReq.mobile)) {
				phone = ucService.findPhone(mobileExistReq.mobile);
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse hsidResponse = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		hsidResponse.addAttribute("is_exist", phone!=null?true:false);
		hsidResponse.addAttribute("userid", phone!=null?phone.getUserId():0);
		Map<String, Object> extraLog = new HashMap<String,Object>();
		extraLog.put("result", isExist);
		postProcess(baseRequest, response, hsidResponse, extraLog);
	}


}
