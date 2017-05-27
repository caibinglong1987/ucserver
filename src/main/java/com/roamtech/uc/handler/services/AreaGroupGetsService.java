package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.AreaGroup;

public class AreaGroupGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest areaGroupGetsReq = new GuestRequest(request);
		int status = ErrorCode.SUCCESS;
		List<AreaGroup> areaGroups = null;
		if(areaGroupGetsReq.validate()) {
			if(ucService.isSessionValid(areaGroupGetsReq.getSessionId())) {
				areaGroups = ucService.getAreaGroups();
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), areaGroupGetsReq.getUserid(),areaGroupGetsReq.getSessionId());
		ucResp.addAttribute("areagroups", areaGroups);
		postProcess(baseRequest, response, ucResp);
	}
}
