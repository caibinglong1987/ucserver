package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;


public class TouchInfoGetService extends AbstractService {
	private class TouchGetRequest extends AbstractRequest {
		private String devid;
		private String host;

		public TouchGetRequest(HttpServletRequest request) {
			super(request);
			devid = getParameter("devid");
			host = getParameter("host");
		}

		@Override
		public boolean validate() {
			return (StringUtils.isNotBlank(devid) && StringUtils.isNotBlank(host));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchGetRequest touchGetReq = new TouchGetRequest(request);
		int status = ErrorCode.SUCCESS;
		Touch touch = null;
		if(touchGetReq.validate()) {
			touch = ucService.getTouch(touchGetReq.devid,touchGetReq.host);
			if(touch == null) {
				status = ErrorCode.ERR_TOUCHID_NOEXIST;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		ucResp.addAttribute("touchdev", touch);
		postProcess(baseRequest, response, ucResp);
	}

}
