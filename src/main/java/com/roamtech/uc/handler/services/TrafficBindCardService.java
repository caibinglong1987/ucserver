package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.ServicePackage;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.User;

public class TrafficBindCardService extends AbstractService {
	private class TrafficBindCardReq extends UCRequest {
		private Long orderdetailid;
		private Long trafficid;
		private String simid;

		public TrafficBindCardReq(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderdetailid");
			orderdetailid = -1L;
			if(temp != null) {
				orderdetailid = Long.parseLong(temp);
			}
			temp = getParameter("trafficid");
			trafficid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				trafficid = Long.parseLong(temp);
			}
			simid = getParameter("simid");
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderdetailid != -1L || trafficid != -1L)&& StringUtils.isNotBlank(simid));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TrafficBindCardReq datacardBindReq = new TrafficBindCardReq(request);
		int status = ErrorCode.SUCCESS;
		ServicePackage sp = null;
		UCResponse ucResp = null;
		if(datacardBindReq.validate()) {
			if(ucService.isSessionValid(datacardBindReq.getSessionId())) {	
				try {
					sp = ucService.trafficBindDataCard(datacardBindReq.getUserid(), datacardBindReq.simid, datacardBindReq.orderdetailid, datacardBindReq.trafficid);
				/*if(sp == null) {
					status = ErrorCode.ERR_DATACARD_NOEXIST;
				}*/
				} catch (UCBaseException ex) {
					ucResp = ex.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		if(ucResp == null) {
			ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), datacardBindReq.getUserid(),datacardBindReq.getSessionId());
		} else {
			ucResp.setUserId(datacardBindReq.getUserid());
			ucResp.setSessionId(datacardBindReq.getSessionId());
		}
		ucResp.addAttribute("servicepackage", sp);
		postProcess(baseRequest, response, ucResp);
	}

}
