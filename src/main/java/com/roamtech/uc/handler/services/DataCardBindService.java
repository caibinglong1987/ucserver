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
import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.User;

public class DataCardBindService extends AbstractService {
	private class DataCardBindReq extends UCRequest {
		private String datacardid;

		public DataCardBindReq(HttpServletRequest request) {
			super(request);
			datacardid = getParameter("datacardid");
			/*datacardid = -1L;
			if(temp != null&&temp.trim().length()<20&&StringUtils.isNumeric(temp)) {
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
		DataCardBindReq datacardBindReq = new DataCardBindReq(request);
		int status = ErrorCode.SUCCESS;
		DataCard dc = null;
		if(datacardBindReq.validate()) {
			if(ucService.isSessionValid(datacardBindReq.getSessionId())) {		
				dc = ucService.bindDataCard(datacardBindReq.getUserid(), datacardBindReq.datacardid);
				if(dc == null) {
					status = ErrorCode.ERR_DATACARD_NOEXIST;
				} else if(!dc.getUserId().equals(datacardBindReq.getUserid())) {
					status = ErrorCode.ERR_DATACARD_BINDED;
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), datacardBindReq.getUserid(),datacardBindReq.getSessionId());
		ucResp.addAttribute("datacard", dc);
		postProcess(baseRequest, response, ucResp);
	}

}
