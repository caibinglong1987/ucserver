package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.AreaCode;

public class AreaCodeGetsService extends AbstractService {
	private class AreaGetsRequest extends GuestRequest {
		private Long groupId;
		AreaGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("groupid");
			groupId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				groupId = Long.parseLong(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		AreaGetsRequest areaCodeGetsReq = new AreaGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<AreaCode> areaCodes = null;
		if(areaCodeGetsReq.validate()) {
			if(ucService.isSessionValid(areaCodeGetsReq.getSessionId())) {
				areaCodes = ucService.getAreaCodes(areaCodeGetsReq.groupId);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), areaCodeGetsReq.getUserid(),areaCodeGetsReq.getSessionId());
		ucResp.addAttribute("areacodes", areaCodes);
		postProcess(baseRequest, response, ucResp);
	}
}
