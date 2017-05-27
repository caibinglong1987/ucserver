package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class TouchUnbindService extends AbstractService {
	private class TouchBindRequest extends UCRequest {
		private String devid;

		public TouchBindRequest(HttpServletRequest request) {
			super(request);
			devid = getParameter("devid");
		}

		@Override
		public boolean validate() {
			return (super.validate() && devid != null && !devid.isEmpty());
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchBindRequest touchBindReq = new TouchBindRequest(request);
		int status = ErrorCode.SUCCESS;
		if(touchBindReq.validate()) {
			if(ucService.isSessionValid(touchBindReq.getSessionId())) {
				ucService.unbindTouch(touchBindReq.getUserid(), touchBindReq.devid);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchBindReq.getUserid(),touchBindReq.getSessionId()));
	}

}
