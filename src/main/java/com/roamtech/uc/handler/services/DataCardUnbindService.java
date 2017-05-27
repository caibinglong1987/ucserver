package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;

public class DataCardUnbindService extends AbstractService {
	private class DataCardUnbindReq extends UCRequest {
		private String datacardid;

		public DataCardUnbindReq(HttpServletRequest request) {
			super(request);
			datacardid = getParameter("datacardid");
			/*datacardid = -1L;
			if(temp != null) {
				datacardid = Long.parseLong(temp);
			}*/	
		}

		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(datacardid)/* != -1L*/);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DataCardUnbindReq datacardUnbindReq = new DataCardUnbindReq(request);
		int status = ErrorCode.SUCCESS;
		if(datacardUnbindReq.validate()) {
			if(ucService.isSessionValid(datacardUnbindReq.getSessionId())) {		
				ucService.unbindDataCard(datacardUnbindReq.getUserid(), datacardUnbindReq.datacardid);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		postProcess(baseRequest, response, UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), datacardUnbindReq.getUserid(),datacardUnbindReq.getSessionId()));
	}

}
