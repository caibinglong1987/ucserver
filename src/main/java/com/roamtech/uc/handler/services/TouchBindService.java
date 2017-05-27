package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class TouchBindService extends AbstractService {
	private class TouchBindRequest extends UCRequest {
		private String mobile;
		private String devid;
		private String wifi_ssid;
		private String wifi_password;

		public TouchBindRequest(HttpServletRequest request) {
			super(request);
			mobile = getParameter("phone");
			devid = getParameter("devid");
			wifi_ssid = getParameter("wifi_ssid");
			wifi_password = getParameter("wifi_password");
		}

		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(devid));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchBindRequest touchBindReq = new TouchBindRequest(request);
		int status = ErrorCode.SUCCESS;
		if(touchBindReq.validate()) {
			if(ucService.isSessionValid(touchBindReq.getSessionId())) {
					ucService.bindTouch(touchBindReq.getUserid(), touchBindReq.devid, touchBindReq.mobile, touchBindReq.wifi_ssid, touchBindReq.wifi_password);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchBindReq.getUserid(),touchBindReq.getSessionId()));
	}

}
