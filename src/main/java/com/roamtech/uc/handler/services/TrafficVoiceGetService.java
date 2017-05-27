package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.AvailableServiceRDO;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class TrafficVoiceGetService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest tvReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		AvailableServiceRDO rdo = null;
		if(tvReq.validate()) {
			if(ucService.isSessionValid(tvReq.getSessionId())) {
				rdo = ucService.getCurrentAvailableService(tvReq.getUserid());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), tvReq.getUserid(),tvReq.getSessionId());
		if(rdo != null) {
			ucResp.addAttribute("servicepackages", rdo.getServicePackages());
			ucResp.addAttribute("voiceavailable", rdo.getVoiceTalk());
		}
		postProcess(baseRequest, response, ucResp);
	}
}
